package com.example.bubbleapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.RemoteInput
import com.example.bubbleapplication.repository.ChatRepository
import com.example.bubbleapplication.repository.DefaultChatRepository

class ReplyReceiver : BroadcastReceiver() {

    companion object {
        const val KEY_TEXT_REPLY = "reply"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val repository: ChatRepository = DefaultChatRepository.getInstance(context)

        val results = RemoteInput.getResultsFromIntent(intent) ?: return
        val input = results.getCharSequence(KEY_TEXT_REPLY)?.toString()
        val uri = intent.data ?: return
        val chatId = uri.lastPathSegment?.toLong() ?: return

        if (chatId > 0 && !input.isNullOrBlank()) {
            repository.sendMessage(chatId, input.toString(), null, null)
            repository.updateNotification(chatId)
        }
    }
}