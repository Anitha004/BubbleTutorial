package com.example.bubbleapplication.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.bubbleapplication.repository.ChatRepository
import com.example.bubbleapplication.repository.DefaultChatRepository

class ChatViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: ChatRepository = DefaultChatRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val chatId = MutableLiveData<Long>()

    private val _photoUri = MutableLiveData<Uri?>()
    val photo: LiveData<Uri?> = _photoUri

    private var _photoMimeType: String? = null

    var foreground = false
        set(value) {
            field = value
            chatId.value?.let { id ->
                if (value) {
                    repository.activateChat(id)
                } else {
                    repository.deactivateChat(id)
                }
            }
        }


    val contact = chatId.switchMap { id -> repository.findContact(id) }


    val messages = chatId.switchMap { id -> repository.findMessages(id) }


    val showAsBubbleVisible = chatId.map { id -> repository.canBubble(id) }

    fun setChatId(id: Long) {
        chatId.value = id
        if (foreground) {
            repository.activateChat(id)
        } else {
            repository.deactivateChat(id)
        }
    }

    fun send(text: String) {
        val id = chatId.value
        if (id != null && id != 0L) {
            repository.sendMessage(id, text, _photoUri.value, _photoMimeType)
        }
        _photoUri.value = null
        _photoMimeType = null
    }

    fun showAsBubble() {
        chatId.value?.let { id ->
            repository.showAsBubble(id)
        }
    }

    fun setPhoto(uri: Uri, mimeType: String) {
        _photoUri.value = uri
        _photoMimeType = mimeType
    }

    override fun onCleared() {
        chatId.value?.let { id -> repository.deactivateChat(id) }
    }
}