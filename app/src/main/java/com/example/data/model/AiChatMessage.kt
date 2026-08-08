package com.example.data.model

import android.graphics.Bitmap

data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: String = "الآن",
    val imageUri: String? = null,
    val imageBitmap: Bitmap? = null,
    val isError: Boolean = false,
    val modelUsed: String = "gemini-3.5-flash"
)

