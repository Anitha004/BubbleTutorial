package com.example.bubbleapplication.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.LocusIdCompat
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.example.bubbleapplication.BubbleActivity
import com.example.bubbleapplication.MainActivity
import com.example.bubbleapplication.R
import com.example.bubbleapplication.ReplyReceiver

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_NEW_MESSAGES = "new_messages"

        private const val REQUEST_CONTENT = 1
        private const val REQUEST_BUBBLE = 2
    }

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_NEW_MESSAGES,
                    context.getString(R.string.channel_new_messages),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.channel_new_messages_description)
                }
            )
        }
        updateShortcuts(null)
    }

    @WorkerThread
    fun updateShortcuts(importantContact: Contact?) {
        var shortcuts = Contact.CONTACTS.map { contact ->
            val icon = IconCompat.createWithAdaptiveBitmap(
                context.resources.assets.open(contact.icon).use { input ->
                    BitmapFactory.decodeStream(input)
                }
            )
            ShortcutInfoCompat.Builder(context, contact.shortcutId)
                .setLocusId(LocusIdCompat(contact.shortcutId))
                .setActivity(ComponentName(context, MainActivity::class.java))
                .setShortLabel(contact.name)
                .setIcon(icon)
                .setLongLived(true)
                .setCategories(setOf("com.example.android.bubbles.category.TEXT_SHARE_TARGET"))
                .setIntent(
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(
                            Uri.parse(
                                "https://android.example.com/chat/${contact.id}"
                            )
                        )
                )
                .setPerson(
                    Person.Builder()
                        .setName(contact.name)
                        .setIcon(icon)
                        .build()
                )
                .build()
        }
        if (importantContact != null) {
            shortcuts = shortcuts.sortedByDescending { it.id == importantContact.shortcutId }
        }
        val maxCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)
        if (shortcuts.size > maxCount) {
            shortcuts = shortcuts.take(maxCount)
        }
        for (shortcut in shortcuts) {
            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun flagUpdateCurrent(mutable: Boolean): Int {
        return if (mutable) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @WorkerThread
    fun showNotification(chat: Chat, fromUser: Boolean, update: Boolean = false) {
        updateShortcuts(chat.contact)
        val icon = IconCompat.createWithAdaptiveBitmapContentUri(chat.contact.iconUri)
        val user = Person.Builder().setName(context.getString(R.string.sender_you)).build()
        val person = Person.Builder().setName(chat.contact.name).setIcon(icon).build()
        val contentUri = "https://android.example.com/chat/${chat.contact.id}".toUri()

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_BUBBLE,
            Intent(context, BubbleActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .setData(contentUri),
            flagUpdateCurrent(mutable = true)
        )

        val messagingStyle = NotificationCompat.MessagingStyle(user)
        val lastId = chat.messages.last().id
        for (message in chat.messages) {
            val m = NotificationCompat.MessagingStyle.Message(
                message.text,
                message.timestamp,
                if (message.isIncoming) person else null
            ).apply {
                if (message.photoUri != null) {
                    setData(message.photoMimeType, message.photoUri)
                }
            }
            if (message.id < lastId) {
                messagingStyle.addHistoricMessage(m)
            } else {
                messagingStyle.addMessage(m)
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MESSAGES)
            .setBubbleMetadata(
                NotificationCompat.BubbleMetadata.Builder(pendingIntent, icon)
                    .setDesiredHeight(context.resources.getDimensionPixelSize(R.dimen.bubble_height))
                    .apply {
                        if (fromUser) {
                            setAutoExpandBubble(true)
                        }
                        if (fromUser || update) {
                            setSuppressNotification(true)
                        }
                    }
                    .setIntent(pendingIntent)
                    .setAutoExpandBubble(true)
                    .setSuppressNotification(true)
                    .build()
            )

            .setContentTitle(chat.contact.name)
            .setSmallIcon(R.drawable.baseline_message_24)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setShortcutId(chat.contact.shortcutId)
            .setLocusId(LocusIdCompat(chat.contact.shortcutId))
            .addPerson(person)
            .setShowWhen(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    REQUEST_CONTENT,
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(contentUri),
                    flagUpdateCurrent(mutable = false)
                )
            )
            .addAction(
                NotificationCompat.Action
                    .Builder(
                        IconCompat.createWithResource(context, R.drawable.baseline_send_24),
                        context.getString(R.string.label_reply),
                        PendingIntent.getBroadcast(
                            context,
                            REQUEST_CONTENT,
                            Intent(context, ReplyReceiver::class.java).setData(contentUri),
                            flagUpdateCurrent(mutable = true)
                        )
                    )
                    .addRemoteInput(
                        RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
                            .setLabel(context.getString(R.string.hint_input))
                            .build()
                    )
                    .setAllowGeneratedReplies(true)
                    .build()
            )

            .setStyle(messagingStyle)
            .setWhen(chat.messages.last().timestamp)
        if (update) {
            builder.setOnlyAlertOnce(true)
        }
        notificationManager.notify(chat.contact.id.toInt(), builder.build())
    }

    private fun dismissNotification(id: Long) {
        notificationManager.cancel(id.toInt())
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun canBubble(contact: Contact): Boolean {
        val channel = notificationManager.getNotificationChannel(
            CHANNEL_NEW_MESSAGES,
            contact.shortcutId
        )
        return notificationManager.areBubblesAllowed() || channel?.canBubble() == true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun updateNotification(chat: Chat, chatId: Long, prepopulatedMsgs: Boolean) {
        if (!prepopulatedMsgs) {
            showNotification(chat, fromUser = false, update = true)
        } else {
            dismissNotification(chatId)
        }
    }
}