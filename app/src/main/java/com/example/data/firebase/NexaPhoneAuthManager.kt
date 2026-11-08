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
        val code: String,
        val isoRegion: String
    )

    val supportedCountryCodes = listOf(
        CountryCode("🇲🇦", "المغرب", "+212", "MA"),
        CountryCode("🇸🇦", "السعودية", "+966", "SA"),
        CountryCode("🇦🇪", "الإمارات", "+971", "AE"),
        CountryCode("🇪🇬", "مصر", "+20", "EG"),
        CountryCode("🇺🇸", "أمريكا / كندا", "+1", "US"),
        CountryCode("🇯🇴", "الأردن", "+962", "JO"),
        CountryCode("🇰🇼", "الكويت", "+965", "KW"),
        CountryCode("🇶🇦", "قطر", "+974", "QA"),
        CountryCode("🇮🇶", "العراق", "+964", "IQ"),
        CountryCode("🇩🇿", "الجزائر", "+213", "DZ"),
        CountryCode("🇹🇳", "تونس", "+216", "TN"),
        CountryCode("🇱🇧", "لبنان", "+961", "LB")
    )

    fun getDefaultCountry(context: Context): CountryCode {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? android.telephony.TelephonyManager
            val simCountry = telephonyManager?.simCountryIso?.uppercase()
            val networkCountry = telephonyManager?.networkCountryIso?.uppercase()
            val localeCountry = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0)?.country?.uppercase()
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale?.country?.uppercase()
            }
            val targetIso = simCountry?.ifBlank { null } ?: networkCountry?.ifBlank { null } ?: localeCountry ?: "MA"
            return supportedCountryCodes.find { it.isoRegion.equals(targetIso, ignoreCase = true) }
                ?: supportedCountryCodes.first()
        } catch (_: Throwable) {
            return supportedCountryCodes.first()
        }
    }

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
        if (activity == null) {
            onError("فشل في تحديد نافذة التوثيق")
            return
        }
        val cleanNumber = phoneNumber.trim()
        if (cleanNumber.isBlank()) {
            onError("يرجى إدخال رقم الهاتف.")
            return
        }

        val phoneUtil = io.michaelrocks.libphonenumber.android.PhoneNumberUtil.createInstance(activity)
        val defaultRegion = supportedCountryCodes.find { it.code == countryCode }?.isoRegion
            ?: getDefaultCountry(activity).isoRegion

        val fullPhoneNumber: String = try {
            val parsed = phoneUtil.parse(cleanNumber, defaultRegion)
            if (!phoneUtil.isValidNumber(parsed)) {
                onError("رقم الهاتف غير صالح للدولة المحددة ($countryCode). يرجى التأكد من كتابة الرقم بشكل صحيح.")
                return
            }
            phoneUtil.format(parsed, io.michaelrocks.libphonenumber.android.PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (e: Exception) {
            onError("رقم الهاتف غير صالح ($cleanNumber). يرجى التأكد من الرقم والمحاولة مجدداً.")
            return
        }

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
