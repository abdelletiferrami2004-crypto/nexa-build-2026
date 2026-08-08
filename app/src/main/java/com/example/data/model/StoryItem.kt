package com.example.data.model

data class StoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val authorName: String,
    val authorAvatarUrl: String = "",
    val text: String,
    val isVideo: Boolean = false,
    val bgGradient: List<Long> = listOf(0xFF8B5CF6, 0xFF00F5FF),
    val replyToAuthor: String? = null,
    val replyToText: String? = null,
    val deepLinkUrl: String? = null,
    val timestamp: String = "الآن",
    // Reel Story Sharing Properties
    val isReelShare: Boolean = false,
    val reelTitle: String? = null,
    val reelAuthor: String? = null,
    val reelLikesCount: String = "12.4K",
    val reelViewsCount: String = "45.2K",
    val reelCommentsCount: String = "890",
 val reelSoundTrack: String ="صوت مجرة الأصلي - نيون شات",
    val reelGradient: List<Long> = listOf(0xFF0F0C20, 0xFF1F104D, 0xFF0D0620),
    // Story Social Engagement Stats
    val storyLikes: Int = 184,
    val isLikedByMe: Boolean = false
)
