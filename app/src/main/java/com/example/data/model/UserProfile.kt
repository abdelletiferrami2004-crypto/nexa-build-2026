package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val phone: String = "",
    val name: String = "عبدالعزيز الماجد",
    val age: Int = 16,
    val isTeenMode: Boolean = true, // automatically true if age < 18
    val isBiometricEnabled: Boolean = true,
    val isChatPinEnabled: Boolean = false,
    val chatPin: String = "",
    val isLoggedIn: Boolean = false,
    val avatarUrl: String = "",
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val totalViewsCount: Long = 0L,
    val isProfileLocked: Boolean = false,
    val points: Int = 450,
    val isVipMember: Boolean = false,
    val vipTierName: String = "NEXA Standard",
    val creditsBalance: Int = 0,
    val referralCode: String = "NEXA-8821",
    val claimedDailyRewardDays: Int = 3,
    val lastDailyClaimTimestamp: Long = 0L,
    val isE2eEncryptionEnabled: Boolean = true,
    val bio: String = "عاشق للتقنية والابتكار ومصمم محتوى في منصة NEXA",
    val username: String = "abdulaziz_majed",
    val isContactsSynced: Boolean = false,
    val isVerified: Boolean = false, // Blue Badge Verification
    val verificationBadgeCategory: String = "عضو موثق",
    val isTwoFactorEnabled: Boolean = false, // 2FA Security
    val twoFactorMethod: String = "authenticator", // "sms", "authenticator", "biometric"
    val twoFactorSecret: String = "NEXA-2FA-SECURE-8891",
    val isNexaProSubscriber: Boolean = false, // NEXA AI Pro Subscriber
    val email: String = "user@nexa.ai",
    val firebaseUid: String = "",
    val uid: String = "",
    val fcmToken: String = "",
    val dailyAiGenerationsUsed: Int = 0, // Daily AI Image & Video generations used
    val lastAiGenerationDate: String = "", // e.g. "2026-08-24" for daily reset
    val maxFreeDailyAiGenerations: Int = 3 // Free limit: 3 generations per day
)

typealias User = UserProfile
