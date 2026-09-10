package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * NexaQuotaManager enforces local storage-backed quota management for free vs Pro users.
 * - Limits free users to 3 AI media generations (images / videos) per day.
 * - Daily reset is tracked via date timestamp ("yyyy-MM-dd").
 * - Once quota hits 0, canGenerate() returns false and triggers paywall.
 * - For Pro users (nexa_pro_monthly), all limits are completely removed (unlimited access).
 */
class NexaQuotaManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val TAG = "NexaQuotaManager"
        const val PREFS_NAME = "nexa_quota_and_billing_prefs"

        const val KEY_DAILY_AI_USED = "daily_ai_generations_used"
        const val KEY_LAST_RESET_DATE = "daily_ai_last_reset_date"
        const val KEY_IS_PRO_USER = "is_nexa_pro_user"
        const val KEY_SUBSCRIPTION_ID = "nexa_active_subscription_id"
        const val KEY_PURCHASE_TOKEN = "nexa_purchase_token"
        const val KEY_PURCHASE_TIME = "nexa_purchase_timestamp"

        const val MAX_FREE_DAILY_GENERATIONS = 3
        const val SUBSCRIPTION_ID_MONTHLY = "nexa_pro_monthly"
        const val SUBSCRIPTION_ID_ANNUAL = "nexa_pro_annual"

        @Volatile
        private var instance: NexaQuotaManager? = null

        fun getInstance(context: Context): NexaQuotaManager {
            return instance ?: synchronized(this) {
                instance ?: NexaQuotaManager(context).also { instance = it }
            }
        }
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    /**
     * Checks if the user is currently verified as Nexa AI Pro.
     */
    @Synchronized
    fun isProUser(): Boolean {
        return prefs.getBoolean(KEY_IS_PRO_USER, false)
    }

    /**
     * Activates or deactivates Pro status in local storage.
     */
    @Synchronized
    fun setProUser(isPro: Boolean, subscriptionId: String? = null, purchaseToken: String? = null) {
        val editor = prefs.edit().putBoolean(KEY_IS_PRO_USER, isPro)
        if (subscriptionId != null) {
            editor.putString(KEY_SUBSCRIPTION_ID, subscriptionId)
        }
        if (purchaseToken != null) {
            editor.putString(KEY_PURCHASE_TOKEN, purchaseToken)
            editor.putLong(KEY_PURCHASE_TIME, System.currentTimeMillis())
        }
        editor.apply()
        Log.d(TAG, "Nexa Pro status updated: isPro=$isPro, subId=$subscriptionId")
    }

    @Synchronized
    fun getActiveSubscriptionId(): String? {
        return prefs.getString(KEY_SUBSCRIPTION_ID, null)
    }

    /**
     * Returns the count of generations used today.
     * Automatically resets to 0 if the calendar day has rolled over.
     */
    @Synchronized
    fun getDailyUsed(): Int {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_LAST_RESET_DATE, null)
        if (savedDate != today) {
            // Day changed - reset to 0
            prefs.edit()
                .putString(KEY_LAST_RESET_DATE, today)
                .putInt(KEY_DAILY_AI_USED, 0)
                .apply()
            Log.d(TAG, "Daily AI quota reset for new day: $today")
            return 0
        }
        return prefs.getInt(KEY_DAILY_AI_USED, 0)
    }

    /**
     * Returns remaining generations for today.
     * For Pro subscribers, returns 999999 (unlimited).
     * For free users, returns (3 - used) clamped to >= 0.
     */
    @Synchronized
    fun getRemainingGenerations(): Int {
        if (isProUser()) {
            return 999999
        }
        val used = getDailyUsed()
        return (MAX_FREE_DAILY_GENERATIONS - used).coerceAtLeast(0)
    }

    /**
     * Returns true if user has quota available or is Pro.
     */
    @Synchronized
    fun canGenerate(): Boolean {
        if (isProUser()) return true
        return getRemainingGenerations() > 0
    }

    /**
     * Consumes 1 generation from the daily quota.
     * Returns true if generation is permitted, or false if quota is exhausted.
     */
    @Synchronized
    fun consumeGeneration(): Boolean {
        if (isProUser()) {
            return true // Pro users have unlimited access
        }
        val used = getDailyUsed()
        if (used < MAX_FREE_DAILY_GENERATIONS) {
            val newUsed = used + 1
            prefs.edit().putInt(KEY_DAILY_AI_USED, newUsed).apply()
            Log.d(TAG, "Consumed 1 AI generation. New used count: $newUsed/$MAX_FREE_DAILY_GENERATIONS")
            return true
        }
        Log.w(TAG, "AI generation rejected: Daily quota exhausted ($used/$MAX_FREE_DAILY_GENERATIONS)")
        return false
    }

    @Synchronized
    fun resetForTesting() {
        prefs.edit().putInt(KEY_DAILY_AI_USED, 0).apply()
    }
}
