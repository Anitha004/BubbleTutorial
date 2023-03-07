package com.example.bubbleapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.example.bubbleapplication.data.Contact
import com.example.bubbleapplication.data.NavigationController
import com.example.bubbleapplication.data.viewBindings
import com.example.bubbleapplication.databinding.ActivityMainBinding
import com.example.bubbleapplication.fragment.ChatFragment
import com.example.bubbleapplication.fragment.MainFragment
import com.example.bubbleapplication.fragment.PhotoFragment

class MainActivity : AppCompatActivity(R.layout.activity_main), NavigationController {

    companion object {
        private const val FRAGMENT_CHAT = "chat"
    }

    private val binding by viewBindings(ActivityMainBinding::bind)

    private lateinit var transition: Transition

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        transition = TransitionInflater.from(this).inflateTransition(R.transition.app_bar)
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.container, MainFragment())
            }
            intent?.let(::handleIntent)
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {

            Intent.ACTION_VIEW -> {
                val id = intent.data?.lastPathSegment?.toLongOrNull()
                if (id != null) {
                    openChat(id, null)
                }
            }

            Intent.ACTION_SEND -> {
                val shortcutId = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID)
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                val contact = Contact.CONTACTS.find { it.shortcutId == shortcutId }
                if (contact != null) {
                    openChat(contact.id, text)
                }
            }
        }
    }

    override fun updateAppBar(
        showContact: Boolean,
        hidden: Boolean,
        body: (name: TextView, icon: ImageView) -> Unit
    ) {
        if (hidden) {
            binding.appBar.visibility = View.GONE
        } else {
            binding.appBar.visibility = View.VISIBLE
            val beginDelayedTransition =
                TransitionManager.beginDelayedTransition(binding.appBar, transition)
            if (showContact) {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                binding.name.visibility = View.VISIBLE
                binding.icon.visibility = View.VISIBLE
            } else {
                supportActionBar?.setDisplayShowTitleEnabled(true)
                binding.name.visibility = View.GONE
                binding.icon.visibility = View.GONE
            }
        }
        body(binding.name, binding.icon)
    }

    override fun openChat(id: Long, prepopulateText: String?) {
        supportFragmentManager.popBackStack(FRAGMENT_CHAT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.commit {
            addToBackStack(FRAGMENT_CHAT)
            replace(R.id.container, ChatFragment.newInstance(id, true, prepopulateText))
        }
    }

    override fun openPhoto(photo: Uri) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.container, PhotoFragment.newInstance(photo))
        }
    }
}