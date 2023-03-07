package com.example.bubbleapplication

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.example.bubbleapplication.data.NavigationController
import com.example.bubbleapplication.fragment.ChatFragment
import com.example.bubbleapplication.fragment.PhotoFragment

class BubbleActivity : AppCompatActivity(R.layout.activity_bubble), NavigationController {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.data?.lastPathSegment?.toLongOrNull() ?: return
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.container, ChatFragment.newInstance(id, false))
            }
        }
        isLaunchedFromBubble
    }

    override fun openChat(id: Long, prepopulateText: String?) {
        throw UnsupportedOperationException("BubbleActivity always shows a single chat thread.")
    }

    override fun openPhoto(photo: Uri) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.container, PhotoFragment.newInstance(photo))
        }
    }

    override fun updateAppBar(
        showContact: Boolean,
        hidden: Boolean,
        body: (name: TextView, icon: ImageView) -> Unit
    ) {

    }
}