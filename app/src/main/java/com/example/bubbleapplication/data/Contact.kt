package com.example.bubbleapplication.data

import androidx.core.net.toUri

abstract class Contact(
    val id: Long,
    val name: String,
    val icon: String
) {

    companion object {
        val CONTACTS = listOf(
            object : Contact(1L, "David", "image.jpeg") {
                override fun reply(text: String) = buildReply().apply { this.text = "Hello" }
            },
            object : Contact(2L, "Jacub", "jacub.jpg") {
                override fun reply(text: String) = buildReply().apply { this.text = "Hey!!" }
            },
            object : Contact(3L, "Martin", "martin.jpg") {
                override fun reply(text: String) = buildReply().apply { this.text = text }
            },
            object : Contact(4L, "Lukas", "image.jpeg") {
                override fun reply(text: String) = buildReply().apply {
                    this.text = "Hey!"
                    photo = "content://com.example.bubbleapplication/photo/image.jpg".toUri()
                    photoMimeType = "image/jpeg"
                }
            }
        )
    }

    val iconUri = "content://com.example.bubbleapplication/icon/$id".toUri()

    val shortcutId = "contact_$id"

    fun buildReply() = Message.Builder().apply {
        sender = this@Contact.id
        timestamp = System.currentTimeMillis()
    }

    abstract fun reply(text: String): Message.Builder

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        return result
    }
}