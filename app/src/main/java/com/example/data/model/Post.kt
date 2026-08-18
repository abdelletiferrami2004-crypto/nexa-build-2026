package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorAvatarUrl: String = "",
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 24,
    val commentsCount: Int = 5,
    val isLiked: Boolean = false,
    val taggedProductId: Int? = null,
    val isTeenSafe: Boolean = true,
    val isAuthorVerified: Boolean = false
)
