package com.example.bubbleapplication.fragment

import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.bubbleapplication.R
import com.example.bubbleapplication.data.getNavigationController
import com.example.bubbleapplication.data.viewBindings
import com.example.bubbleapplication.databinding.FragmentPhotoBinding


class PhotoFragment : Fragment(R.layout.fragment_photo) {

    companion object {
        private const val ARG_PHOTO = "photo"

        fun newInstance(photo: Uri) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PHOTO, photo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Fade()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val photo = arguments?.getParcelable<Uri>(ARG_PHOTO)
        if (photo == null) {
            if (isAdded) {
                parentFragmentManager.popBackStack()
            }
            return
        }
        getNavigationController().updateAppBar(hidden = true)
        val binding by viewBindings(FragmentPhotoBinding::bind)
        Glide.with(this).load(photo).into(binding.photo)
    }
}