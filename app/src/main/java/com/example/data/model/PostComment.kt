package com.example.data.model

data class PostComment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val postId: Int,
    val authorName: String,
    val authorAvatarUrl: String = "",
    val text: String,
    val timestamp: String = "منذ قليل",
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val taggedProductOrService: String? = null
)
