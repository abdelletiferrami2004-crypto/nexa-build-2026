package com.example.util

import android.content.Context
import android.content.SharedPreferences

/**
 * PinLockManager:
 * Provides safe, persistent, crash-proof PIN lock settings and verification.
 * Prevents NullPointerExceptions and ensures isPinEnabled defaults to false.
 */
object PinLockManager {
    private const val PREFS_NAME = "nexa_pin_lock_prefs"
    private const val KEY_IS_PIN_ENABLED = "is_pin_enabled"
    private const val KEY_SAVED_PIN = "saved_pin"
    private const val MASTER_PIN = "0000"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Safe check whether PIN lock is enabled. Defaults to false.
     */
    fun isPinEnabled(context: Context): Boolean {
        return try {
            getPrefs(context).getBoolean(KEY_IS_PIN_ENABLED, false)
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * Enable or disable PIN lock safely.
     */
    fun setPinEnabled(context: Context, enabled: Boolean, pin: String = "") {
        try {
            getPrefs(context).edit().apply {
                putBoolean(KEY_IS_PIN_ENABLED, enabled)
                if (enabled && pin.isNotBlank()) {
                    putString(KEY_SAVED_PIN, pin)
                } else if (!enabled) {
                    putString(KEY_SAVED_PIN, "")
                }
                apply()
            }
        } catch (e: Throwable) {
            // Safe fallback
        }
    }

    /**
     * Get saved PIN safely.
     */
    fun getSavedPin(context: Context): String {
        return try {
            getPrefs(context).getString(KEY_SAVED_PIN, "") ?: ""
        } catch (e: Throwable) {
            ""
        }
    }

    /**
     * Verify entered PIN against saved PIN, Master PIN 0000, or fallback.
     */
    fun verifyPin(context: Context, enteredPin: String, fallbackPin: String = ""): Boolean {
        if (enteredPin == MASTER_PIN) return true
        val saved = getSavedPin(context).ifBlank { fallbackPin }
        if (saved.isBlank()) return true // No PIN configured
        return enteredPin == saved
    }
}
