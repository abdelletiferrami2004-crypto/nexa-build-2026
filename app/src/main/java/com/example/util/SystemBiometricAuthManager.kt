package com.example.util

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object SystemBiometricAuthManager {

    /**
     * Helper to locate FragmentActivity from a Context.
     */
    fun findFragmentActivity(context: Context): FragmentActivity? {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is FragmentActivity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }

    /**
     * Checks if the device has biometric hardware or device lock credentials configured.
     */
    fun canAuthenticate(context: Context): Boolean {
        return try {
            val biometricManager = BiometricManager.from(context)
            val authenticators = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL
            val status = biometricManager.canAuthenticate(authenticators)
            status == BiometricManager.BIOMETRIC_SUCCESS || status == BiometricManager.BIOMETRIC_STATUS_UNKNOWN
        } catch (e: Throwable) {
            android.util.Log.e("SystemBiometricAuthManager", "Error checking biometric status", e)
            false
        }
    }

    /**
     * Triggers the native OS system biometric authentication prompt.
     * Hardware-agnostic: automatically handles rear, side-key, in-display fingerprint, face unlock, and device PIN/Password fallback.
     */
    fun authenticate(
        context: Context,
        title: String = "تسجيل الدخول بالبصمة الحيوية - NEXA",
        subtitle: String = "استخدم بصمة الأصبع، التعرف على الوجه، أو رمز قفل الجهاز للدخول السريع",
        negativeButtonText: String = "استخدام كلمة السر / PIN",
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {},
        onFallbackToPassword: () -> Unit = {}
    ) {
        val activity = findFragmentActivity(context)
        if (activity == null) {
            android.util.Log.w("SystemBiometricAuthManager", "FragmentActivity not found. Falling back to password.")
            Toast.makeText(context, "الرجاء استخدام كلمة السر أو رمز PIN للدخول", Toast.LENGTH_SHORT).show()
            onFallbackToPassword()
            return
        }

        try {
            val executor = ContextCompat.getMainExecutor(activity)
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
 Toast.makeText(activity,"تم التحقق بالبصمة بنجاح!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    android.util.Log.w("SystemBiometricAuthManager", "Biometric error [$errorCode]: $errString")
                    
                    // Error codes 13 (ERROR_NEGATIVE_BUTTON) or 10 (ERROR_USER_CANCELED) or 11 (ERROR_UNABLE_TO_PROCESS)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        onFallbackToPassword()
                    } else {
                        Toast.makeText(activity, errString.toString(), Toast.LENGTH_SHORT).show()
                        onError(errString.toString())
                        onFallbackToPassword()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(activity, "لم يتم التعرف على البصمة، حاول مرة أخرى", Toast.LENGTH_SHORT).show()
                    onError("لم يتم التعرف على البصمة")
                }
            }

            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            val biometricManager = BiometricManager.from(activity)

            val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setConfirmationRequired(false)

            // Check if strong/weak biometric is available
            val canUseBiometricOnly = biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

            if (canUseBiometricOnly) {
                promptInfoBuilder
                    .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
                    .setNegativeButtonText(negativeButtonText)
            } else {
                // On Android, DEVICE_CREDENTIAL allows device PIN/pattern/password fallback
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    promptInfoBuilder.setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
                } else {
                    promptInfoBuilder
                        .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
                        .setNegativeButtonText(negativeButtonText)
                }
            }

            biometricPrompt.authenticate(promptInfoBuilder.build())
        } catch (e: Throwable) {
            android.util.Log.e("SystemBiometricAuthManager", "Safely caught biometric prompt error", e)
            Toast.makeText(context, "الانتقال إلى إدخال كلمة السر/رمز PIN", Toast.LENGTH_SHORT).show()
            onFallbackToPassword()
        }
    }
}
