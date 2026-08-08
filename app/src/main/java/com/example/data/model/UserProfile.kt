package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val phone: String = "+966 50 123 4567",
    val name: String = "عبدالعزيز الماجد",
    val age: Int = 16,
    val isTeenMode: Boolean = true, // automatically true if age < 18
    val isBiometricEnabled: Boolean = true,
    val chatPin: String = "1234",
    val isLoggedIn: Boolean = false,
    val avatarUrl: String = "",
    val postsCount: Int = 12,
    val followersCount: Int = 1_250_000, // Default 1.25M Followers to show VIP Diamond Aura
    val totalViewsCount: Long = 1_450_000L, // Default 1.45M Views to unlock Creator Monetization Fund
    val points: Int = 450,
    val isVipMember: Boolean = false,
    val vipTierName: String = "NEXA VIP Diamond",
    val creditsBalance: Int = 850,
    val referralCode: String = "NEXA-8821",
    val claimedDailyRewardDays: Int = 3,
    val lastDailyClaimTimestamp: Long = 0L,
    val isE2eEncryptionEnabled: Boolean = true,
 val bio: String ="عاشق للتقنية والابتكار ومصمم محتوى في منصة NEXA",
    val username: String = "abdulaziz_majed",
    val isContactsSynced: Boolean = false
)

typealias User = UserProfile
