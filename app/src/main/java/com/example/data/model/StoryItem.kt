package com.example.data.model

data class StoryItem(
    val id: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val timestamp: String = "الآن",
    val text: String = "",
    val isLikedByMe: Boolean = false,
    val storyLikes: Int = 0,
    val isVideo: Boolean = false,
    val bgGradient: List<Long> = listOf(0xFF8B5CF6, 0xFF00F5FF),
    val replyToAuthor: String? = null,
    val replyToText: String? = null,
    val deepLinkUrl: String? = null,
    val isReelShare: Boolean = false,
    val reelTitle: String? = null,
    val reelAuthor: String? = null,
    val reelViewsCount: String = "0",
    val reelLikesCount: String = "0",
    val reelCommentsCount: String = "0",
    val reelSoundTrack: String = "الصوت الأصلي",
    val reelGradient: List<Long> = listOf(0xFF0F0C20, 0xFF1F104D, 0xFF0D0620)
)
