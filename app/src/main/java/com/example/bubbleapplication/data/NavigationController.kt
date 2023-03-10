package com.example.bubbleapplication.data

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

interface NavigationController {
    fun openChat(id: Long, prepopulateText: String?)
    fun openPhoto(photo: Uri)
    fun updateAppBar(
        showContact: Boolean = true,
        hidden: Boolean = false,
        body: (name: TextView, icon: ImageView) -> Unit = { _, _ -> }
    )
}
fun Fragment.getNavigationController() = requireActivity() as NavigationController