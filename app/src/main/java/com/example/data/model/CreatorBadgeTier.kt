package com.example.data.model

enum class CreatorBadgeTier(
    val titleAr: String,
    val titleEn: String,
    val minFollowers: Int,
    val badgeEmoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val hasGlow: Boolean,
    val hasDiamondAura: Boolean
) {
    NONE("عضو", "Member", 0, "", 0xFFFFFFFF, 0xFF888888, false, false),
 SILVER("صانع فضي","Silver Creator", 100_000,"", 0xFFC0C0C0, 0xFFE0E0E0, true, false),
 GOLD_CRYSTAL("صانع ذهبي 3D","3D Gold Crystal", 500_000,"", 0xFFFFD700, 0xFFFFA500, true, false),
 DIAMOND_VIP("صانع أسطوري VIP","VIP Diamond Aura", 1_000_000,"", 0xFF00F5FF, 0xFFFF007F, true, true);

    companion object {
        fun fromFollowers(followers: Int): CreatorBadgeTier {
            return when {
                followers >= 1_000_000 -> DIAMOND_VIP
                followers >= 500_000 -> GOLD_CRYSTAL
                followers >= 100_000 -> SILVER
                else -> NONE
            }
        }
    }
}
