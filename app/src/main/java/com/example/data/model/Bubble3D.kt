package com.example.data.model

data class Bubble3D(
    val id: String,
    val title: String,
    val category: String, // "قصة", "منتج", "موضوع", "محادثة"
    val colorPrimaryHex: Long,
    val colorSecondaryHex: Long,
    val iconType: String, // "story", "store", "fire", "chat", "sparkle"
    val sizeDp: Float = 95f,
    val isTeenFriendly: Boolean = true,
    val detailText: String = "",
    val initialX: Float = 0f,
    val initialY: Float = 0f
)
