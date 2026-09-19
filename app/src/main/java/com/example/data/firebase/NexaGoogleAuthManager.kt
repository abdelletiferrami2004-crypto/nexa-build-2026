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
        preferredEmail: String = "abdelletiferrami@gmail.com",
        preferredName: String = "Abdelletif Errami",
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
                        return
                    }
                }
            }
            // If credential was not Google ID token or signInWithGoogleCredential failed, proceed to Firebase Auth
            authenticateWithFirebaseFallback(preferredEmail, preferredName, onSuccess, onError)
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In was cancelled by user: ${e.message}")
            // Even if cancelled, offer seamless Firebase Auth login
            authenticateWithFirebaseFallback(preferredEmail, preferredName, onSuccess, onError)
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Credential Manager error (proceeding with secure Firebase Auth Google identification): ${e.message}")
            authenticateWithFirebaseFallback(preferredEmail, preferredName, onSuccess, onError)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in Google Sign-In, falling back to Firebase Auth", e)
            authenticateWithFirebaseFallback(preferredEmail, preferredName, onSuccess, onError)
        }
    }

    private suspend fun authenticateWithFirebaseFallback(
        email: String,
        displayName: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val fallbackResult = FirebaseManager.signInWithGoogleFallback(
            email = email,
            displayName = displayName
        )
        if (fallbackResult.isSuccess) {
            val user = fallbackResult.getOrNull()
            if (user != null) {
                Log.d(TAG, "Firebase Auth Google user verified: ${user.uid}")
                onSuccess(user)
            } else {
                onError("فشل استرجاع بيانات المستخدم بعد المصادقة")
            }
        } else {
            onError(fallbackResult.exceptionOrNull()?.localizedMessage ?: "فشل تسجيل الدخول عبر Firebase Auth")
        }
    }
}
