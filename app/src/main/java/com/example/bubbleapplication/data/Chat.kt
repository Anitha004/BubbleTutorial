package com.example.bubbleapplication.data


typealias ChatThreadListener = (List<Message>) -> Unit

class Chat(val contact: Contact) {

    private val listeners = mutableListOf<ChatThreadListener>()

    private val _messages = mutableListOf(
        Message(1L, contact.id, "Drop me a message", null, null, System.currentTimeMillis()),
        Message(2L, contact.id, "Get back in 5 seconds", null, null, System.currentTimeMillis())
    )
    val messages: List<Message>
        get() = _messages

    fun addListener(listener: ChatThreadListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ChatThreadListener) {
        listeners.remove(listener)
    }

    fun addMessage(builder: Message.Builder) {
        builder.id = _messages.last().id + 1
        _messages.add(builder.build())
        listeners.forEach { listener -> listener(_messages) }
    }
}