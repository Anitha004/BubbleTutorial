package com.example.bubbleapplication

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bubbleapplication.data.viewBindings
import com.example.bubbleapplication.databinding.ActivityVoiceCallBinding

class VoiceCallActivity : AppCompatActivity(R.layout.activity_voice_call) {

    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_ICON_URI = "iconUri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra(EXTRA_NAME)
        val icon = intent.getParcelableExtra<Uri>(EXTRA_ICON_URI)
        if (name == null || icon == null) {
            finish()
            return
        }
        val binding: ActivityVoiceCallBinding by viewBindings(ActivityVoiceCallBinding::bind)
        binding.name.text = name
        Glide.with(binding.icon)
            .load(icon)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.icon)
    }
}