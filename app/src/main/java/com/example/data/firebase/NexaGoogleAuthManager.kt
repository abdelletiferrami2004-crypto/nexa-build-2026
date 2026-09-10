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
    
    // Web client ID configured for Google Sign-In
    // In production, this matches the OAuth 2.0 Web Client ID from Google Cloud Console
    private const val DEFAULT_SERVER_CLIENT_ID = "619082711624-prod-nexa-app.apps.googleusercontent.com"

    suspend fun signInWithGoogle(
        context: Context,
        serverClientId: String = DEFAULT_SERVER_CLIENT_ID,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
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
                    } else {
                        onError("فشل استرجاع بيانات المستخدم")
                    }
                } else {
                    onError(result.exceptionOrNull()?.localizedMessage ?: "فشل تسجيل الدخول مع Firebase")
                }
            } else {
                Log.w(TAG, "Unsupported credential type received: ${credential.type}")
                onError("نوع بيانات الاعتماد غير مدعوم")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In was cancelled by user")
            onError("تم إلغاء عملية تسجيل الدخول")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error", e)
            onError("خطأ في تسجيل الدخول عبر Google: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in Google Sign-In", e)
            onError(e.localizedMessage ?: "حدث خطأ غير متوقع أثناء تسجيل الدخول")
        }
    }
}
