package com.example.bubbleapplication.fragment

import android.content.LocusId
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bubbleapplication.R
import com.example.bubbleapplication.adapter.ContactAdapter
import com.example.bubbleapplication.adapter.HeaderAdapter
import com.example.bubbleapplication.data.PermissionRequest
import com.example.bubbleapplication.data.PermissionStatus
import com.example.bubbleapplication.data.getNavigationController
import com.example.bubbleapplication.data.viewBindings
import com.example.bubbleapplication.databinding.FragmentMainBinding
import com.example.bubbleapplication.viewmodel.MainViewModel
import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider


class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewBindings(FragmentMainBinding::bind)
@SuppressLint("InlineApi")
    private val permissionRequest = PermissionRequest(this, Manifest.permission.POST_NOTIFICATIONS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.slide_top)
    }

  @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navigationController = getNavigationController()
        navigationController.updateAppBar(false)

      // val viewModel: MainViewModel by viewModels()
       val viewModel= ViewModelProvider(this).get(MainViewModel::class.java)



        val headerAdapter = HeaderAdapter { permissionRequest.launch() }


        val contactAdapter = ContactAdapter { id ->
            navigationController.openChat(id, null)
        }
        binding.contacts.run {
            layoutManager = LinearLayoutManager(view.context)
            setHasFixedSize(true)
            adapter = contactAdapter
        }
        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contactAdapter.submitList(contacts)
        }


        permissionRequest.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                is PermissionStatus.Granted -> binding.contacts.adapter = contactAdapter
                is PermissionStatus.Denied -> {
                    val config = ConcatAdapter.Config.Builder()
                        .setStableIdMode(ConcatAdapter.Config.StableIdMode.SHARED_STABLE_IDS)
                        .build()
                    binding.contacts.adapter = ConcatAdapter(config, headerAdapter, contactAdapter)
                    headerAdapter.shouldShowRationale = status.shouldShowRationale
                }
            }
        }


        requireActivity().setLocusContext(LocusId("mainFragment"), null)
    }
}