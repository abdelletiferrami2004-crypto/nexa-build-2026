package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Rarity classifications for Social & Gamification Badges in NEXA
 */
enum class BadgeRarity(
    val titleArabic: String,
    val titleEnglish: String,
    val colorHex: Long,
    val iconEmoji: String
) {
    COMMON("برونزي شائع", "Common", 0xFF94A3B8, "🥉"),
    RARE("فضي نادر", "Rare", 0xFF00F0FF, "💎"),
    EPIC("بنفسجي ملحمي", "Epic", 0xFF8A2BE2, "🔮"),
    LEGENDARY("ذهبي أسطوري", "Legendary", 0xFFFFD700, "👑"),
    MYTHIC("خرافي نيون", "Mythic", 0xFFFF007A, "⚡")
}

/**
 * Functional categories for badges
 */
enum class BadgeCategory(
    val titleArabic: String,
    val iconName: String
) {
    ALL("الكل", "grid_view"),
    COMMUNITY("المجتمع والتفاعل", "group"),
    ACTIVITY("النشاط والتواجد", "local_fire_department"),
    AI_TECH("الذكاء الاصطناعي", "psychology"),
    SECURITY("الأمان والخصوصية", "shield"),
    CREATOR("صناعة المحتوى", "auto_awesome"),
    VIP("التميز والنخبة", "workspace_premium")
}

/**
 * Represents a single collectible social badge
 */
data class SocialExpBadge(
    val id: String,
    val title: String,
    val description: String,
    val requirementDescription: String,
    val iconType: String, // "fire", "voice", "shield", "crown", "diamond", "star", "camera", "bolt", "trophy", "crypto", "robot", "verified"
    val rarity: BadgeRarity,
    val category: BadgeCategory,
    val isUnlocked: Boolean,
    val unlockedDate: String? = null,
    val currentProgress: Int,
    val requiredProgress: Int,
    val expReward: Int = 100,
    val isClaimed: Boolean = false,
    val socialPerk: String = "يمنحك إطاراً نيونياً متوهجاً في المنصة",
    val socialShareText: String = "حققت شارة أسطورية في مجرة NEXA! 🚀"
)

/**
 * Log entry for EXP earnings
 */
data class ExpActivityHistory(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val expGained: Int,
    val timestamp: String = "الآن",
    val iconType: String = "bolt"
)

/**
 * Complete state tracking user level, experience points, milestones, and earned badges
 */
data class UserExpProgress(
    val currentLevel: Int = 6,
    val currentExp: Int = 720,
    val nextLevelExp: Int = 1000,
    val previousLevelExp: Int = 0,
    val totalExpEarned: Int = 4720,
    val rankTier: RankTier = RankTier.SILVER,
    val streakDays: Int = 6,
    val dailyMultiplier: Float = 1.5f,
    val earnedBadgesCount: Int = 4,
    val totalBadgesCount: Int = 6,
    val badges: List<SocialExpBadge> = emptyList(),
    val recentActivities: List<ExpActivityHistory> = emptyList()
) {
    val progressFraction: Float
        get() {
            val range = nextLevelExp - previousLevelExp
            return if (range > 0) {
                ((currentExp - previousLevelExp).toFloat() / range.toFloat()).coerceIn(0f, 1f)
            } else 0f
        }

    val expNeededForNextLevel: Int
        get() = (nextLevelExp - currentExp).coerceAtLeast(0)

    val progressPercentage: Int
        get() = (progressFraction * 100).toInt()
}
