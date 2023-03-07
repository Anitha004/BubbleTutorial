package com.example.bubbleapplication.adapter

import android.graphics.drawable.Icon
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bubbleapplication.R
import com.example.bubbleapplication.data.Contact
import com.example.bubbleapplication.databinding.ItemChatBinding

class ContactAdapter( private val onChatClicked: (id: Long) -> Unit
) : ListAdapter<Contact, ContactViewHolder>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val holder = ContactViewHolder(parent)
        holder.itemView.setOnClickListener {
            onChatClicked(holder.itemId)
        }
        return holder
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact: Contact = getItem(position)
        holder.binding.icon.setImageIcon(Icon.createWithAdaptiveBitmapContentUri(contact.iconUri))
        holder.binding.name.text = contact.name
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}

class ContactViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
) {
    val binding: ItemChatBinding = ItemChatBinding.bind(itemView)
}