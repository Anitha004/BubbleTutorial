package com.example.bubbleapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bubbleapplication.repository.ChatRepository
import com.example.bubbleapplication.repository.DefaultChatRepository

class MainViewModel @JvmOverloads constructor(
    application: Application,
    repository: ChatRepository = DefaultChatRepository.getInstance(application)
) : AndroidViewModel(application) {


    val contacts = repository.getContacts()
}