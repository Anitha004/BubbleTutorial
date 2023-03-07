package com.example.bubbleapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bubbleapplication.R
import com.example.bubbleapplication.databinding.ContactHeaderBinding

class HeaderAdapter(private val onClick: () -> Unit) : RecyclerView.Adapter<HeaderViewHolder>() {
    init {
        setHasStableIds(true)
    }

    private var cachedHolder: HeaderViewHolder? = null

    var shouldShowRationale = false
        set(value) {
            field = value
            cachedHolder?.let { holder ->
                onBindViewHolder(holder, 0)
            }
        }

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        return HeaderViewHolder(parent).also {
            cachedHolder = it
            it.binding.grant.setOnClickListener { onClick() }
        }
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.binding.rationale.visibility =
            if (shouldShowRationale) View.VISIBLE else View.GONE
    }
}

class HeaderViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.contact_header, parent, false)
) {
    val binding = ContactHeaderBinding.bind(itemView)
}