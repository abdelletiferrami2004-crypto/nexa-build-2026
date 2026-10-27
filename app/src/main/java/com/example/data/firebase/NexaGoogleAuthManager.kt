package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser

object NexaGoogleAuthManager {
    private const val TAG = "NexaGoogleAuth"

    val isGoogleAuthConfigured: Boolean
        get() = com.example.BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()

    suspend fun signInWithGoogle(
        context: Context,
        serverClientId: String = com.example.BuildConfig.GOOGLE_WEB_CLIENT_ID,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        if (serverClientId.isBlank()) {
            onError("تسجيل الدخول بـ Google غير مُعد")
            return
        }

        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                
                val result = FirebaseManager.signInWithGoogleCredential(idToken)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        onSuccess(user)
                        return
                    } else {
                        onError("فشل استرجاع بيانات المستخدم بعد المصادقة")
                        return
                    }
                } else {
                    val errMsg = result.exceptionOrNull()?.localizedMessage ?: "تعذر تسجيل الدخول بحساب Google عبر Firebase"
                    onError(errMsg)
                    return
                }
            } else {
                onError("نوع بيانات الاعتماد المستلمة غير مدعوم")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In was cancelled by user: ${e.message}")
            onError("تم إلغاء تسجيل الدخول بحساب Google")
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Credential Manager error: ${e.message}", e)
            val msg = e.localizedMessage?.ifBlank { "فشل الاتصال بخدمة Google Credentials" } ?: "فشل الاتصال بخدمة Google Credentials"
            onError(msg)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in Google Sign-In", e)
            val msg = e.localizedMessage?.ifBlank { "حدث خطأ غير متوقع أثناء تسجيل الدخول بحساب Google" } ?: "حدث خطأ غير متوقع أثناء تسجيل الدخول بحساب Google"
            onError(msg)
        }
    }
}
