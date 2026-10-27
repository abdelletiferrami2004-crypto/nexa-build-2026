package com.example.data.firebase

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

object NexaPhoneAuthManager {
    private const val TAG = "NexaPhoneAuthManager"

    var currentVerificationId: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    data class CountryCode(
        val flag: String,
        val name: String,
        val code: String
    )

    val supportedCountryCodes = listOf(
        CountryCode("🇸🇦", "السعودية", "+966"),
        CountryCode("🇲🇦", "المغرب", "+212"),
        CountryCode("🇦🇪", "الإمارات", "+971"),
        CountryCode("🇪🇬", "مصر", "+20"),
        CountryCode("🇺🇸", "أمريكا / كندا", "+1"),
        CountryCode("🇯🇴", "الأردن", "+962"),
        CountryCode("🇰🇼", "الكويت", "+965"),
        CountryCode("🇶🇦", "قطر", "+974"),
        CountryCode("🇮🇶", "العراق", "+964"),
        CountryCode("🇩🇿", "الجزائر", "+213"),
        CountryCode("🇹🇳", "تونس", "+216"),
        CountryCode("🇱🇧", "لبنان", "+961")
    )

    fun findActivity(context: Context): Activity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    fun sendSmsOtp(
        activity: Activity?,
        phoneNumber: String,
        countryCode: String,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
        onAutoVerified: () -> Unit
    ) {
        val cleanNumber = phoneNumber.trim().replace(" ", "").replace("-", "")
        if (cleanNumber.length < 7) {
            onError("رقم الجوال غير صحيح! يرجى إدخال رقم هاتف صالح (على الأقل 7 إلى 10 أرقام).")
            return
        }

        val fullPhoneNumber = if (cleanNumber.startsWith("+")) cleanNumber else "$countryCode$cleanNumber"

        try {
            val auth = FirebaseAuth.getInstance()
            val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: Auto verification success")
                        try {
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        onAutoVerified()
                                    } else {
                                        val err = task.exception?.localizedMessage ?: "فشل التوثيق الآلي"
                                        onError(err)
                                    }
                                }
                        } catch (e: Exception) {
                            onError("فشل التوثيق الآلي: ${e.localizedMessage ?: "حدث خطأ غير متوقع"}")
                        }
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Log.e(TAG, "onVerificationFailed: ${e.message}", e)
                        val message = when (e) {
                            is FirebaseAuthInvalidCredentialsException -> "رقم الجوال المدخل غير صالح أو تنسيقه غير مدعوم في خدمة Firebase."
                            is FirebaseTooManyRequestsException -> "تجاوزت الحد المسموح لرسائل SMS لليوم! يرجى الانتظار والمحاولة لاحقاً."
                            else -> "خطأ في إرسال SMS عبر Firebase: ${e.localizedMessage ?: "تأكد من الاتصال بالإنترنت"}"
                        }
                        onError(message)
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        Log.d(TAG, "SMS OTP sent via Firebase. VerificationId: $verificationId")
                        currentVerificationId = verificationId
                        resendToken = token
                        onCodeSent(verificationId)
                    }
                })

            if (activity != null) {
                optionsBuilder.setActivity(activity)
            }

            if (resendToken != null) {
                optionsBuilder.setForceResendingToken(resendToken!!)
            }

            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        } catch (e: Throwable) {
            Log.e(TAG, "Firebase Phone Auth Exception", e)
            onError("فشل إرسال رمز التحقق عبر Firebase Phone Auth: ${e.localizedMessage ?: "تأكد من إعدادات الخدمة والاتصال بالإنترنت"}")
        }
    }

    fun verifyOtpCode(
        verificationId: String?,
        otpCode: String,
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        val cleanCode = otpCode.trim()
        if (cleanCode.length < 6) {
            onError("رمز OTP غير مكتمل! يرجى إدخال 6 أرقام كاملة.")
            return
        }

        val verId = verificationId ?: currentVerificationId

        if (verId.isNullOrBlank() || verId.startsWith("nexa_demo_")) {
            onError("لم يتم العثور على جلسة تحقق Firebase صالحة. يرجى إعادة طلب رمز التحقق عبر SMS.")
            return
        }

        try {
            val credential = PhoneAuthProvider.getCredential(verId, cleanCode)
            val auth = FirebaseAuth.getInstance()
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Firebase SMS credential verified successfully!")
                        onSuccess()
                    } else {
                        val exc = task.exception
                        val msg = when (exc) {
                            is FirebaseAuthInvalidCredentialsException -> "رمز OTP المدخل غير صحيح! يرجى التأكد من الرمز المدخل وإعادة المحاولة."
                            else -> "فشل التحقق من رمز OTP: ${exc?.localizedMessage ?: "رمز غير صحيح"}"
                        }
                        onError(msg)
                    }
                }
        } catch (e: Throwable) {
            Log.e(TAG, "Error verifying Firebase OTP credential", e)
            onError("فشل التحقق من رمز OTP عبر Firebase: ${e.localizedMessage ?: "رمز غير صحيح أو جلسة منتهية"}")
        }
    }
}
