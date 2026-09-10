package com.example.util

import android.util.Log
import com.example.data.firebase.FirebaseManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * NexaFirebaseMessagingService handles incoming Firebase Cloud Messaging (FCM) push notifications
 * and token refreshes for real-time live message delivery.
 */
class NexaFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Firebase Cloud Messaging Token received: ${token.take(16)}...")
        serviceScope.launch {
            try {
                FirebaseManager.updateFcmTokenInCloud(token)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to update FCM token on server", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val conversationId = data["conversationId"] ?: "chat_general"
        val senderName = data["senderName"] ?: notification?.title ?: "NEXA"
        val messageText = data["message"] ?: data["body"] ?: notification?.body ?: "رسالة جديدة في مجرة"

        try {
            NexaNotificationManager.showIncomingMessageNotification(
                context = applicationContext,
                conversationId = conversationId,
                senderName = senderName,
                messageText = messageText,
                avatarBitmap = null
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to present notification from FCM payload", e)
        }
    }

    companion object {
        private const val TAG = "NexaFCMService"
    }
}
