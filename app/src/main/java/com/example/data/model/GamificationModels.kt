package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class NotificationCategory(val titleArabic: String, val iconName: String) {
    ALL("الكل", "notifications"),
    AI("الذكاء الاصطناعي", "auto_awesome"),
    REWARDS("المكافآت والخبرة", "emoji_events"),
    SECURITY("الأمان والخصوصية", "shield"),
    SOCIAL("التواصل والمجتمع", "people"),
    SYSTEM("تحديثات النظام", "info")
}

data class NexaNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val timeAgo: String = "الآن",
    val category: NotificationCategory = NotificationCategory.AI,
    val isRead: Boolean = false,
    val actionRoute: String? = null,
    val rewardExp: Int = 0
)

data class GamificationBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconType: String, // "fire", "ai", "lock", "crown", "star", "chat", "shield", "vip", "reel", "crypto"
    val category: String,
    val requiredProgress: Int,
    val currentProgress: Int,
    val isUnlocked: Boolean,
    val expReward: Int = 100,
    val isClaimed: Boolean = false
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val expReward: Int,
    val creditsReward: Int,
    val currentProgress: Int,
    val targetProgress: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val actionKey: String // "voice_assistant", "like_posts", "create_post", "e2ee_active", "story_view"
)

enum class RankTier(
    val titleArabic: String,
    val titleEnglish: String,
    val minLevel: Int,
    val gradientStartHex: Long,
    val gradientEndHex: Long
) {
    BRONZE("المبتدئ البرونزي", "Bronze Explorer", 1, 0xFFCD7F32, 0xFF8B4513),
    SILVER("المحترف الفضي", "Silver Voyager", 5, 0xFF94A3B8, 0xFF64748B),
    GOLD("الرائد الذهبي", "Gold Pioneer", 10, 0xFFFFD700, 0xFFB8860B),
    PLATINUM("الخبير البلاتيني", "Platinum Master", 20, 0xFF00F0FF, 0xFF0284C7),
    DIAMOND("الماسي الأسطوري", "Diamond Legend", 35, 0xFF8A2BE2, 0xFFFF007A),
    NEXA_GOD("أسطورة مجرة NEXA", "NEXA God Tier", 50, 0xFFFF007A, 0xFFFFD700);

    companion object {
        fun fromLevel(level: Int): RankTier {
            return when {
                level >= 50 -> NEXA_GOD
                level >= 35 -> DIAMOND
                level >= 20 -> PLATINUM
                level >= 10 -> GOLD
                level >= 5 -> SILVER
                else -> BRONZE
            }
        }
    }
}
