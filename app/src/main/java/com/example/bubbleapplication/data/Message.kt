package com.example.bubbleapplication.data

import android.net.Uri

data class Message(
    val id: Long,
    val sender: Long,
    val text: String,
    val photoUri: Uri?,
    val photoMimeType: String?,
    val timestamp: Long
) {

    val isIncoming: Boolean
        get() = sender != 0L

    class Builder {
        var id: Long? = null
        var sender: Long? = null
        var text: String? = null
        var photo: Uri? = null
        var photoMimeType: String? = null
        var timestamp: Long? = null
        fun build() = Message(id!!, sender!!, text!!, photo, photoMimeType, timestamp!!)
    }
}