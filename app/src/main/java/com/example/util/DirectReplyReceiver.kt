package com.example.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.data.local.MajarrahDatabase
import com.example.data.local.MajarrahRepository
import com.example.data.model.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles background direct reply actions from system notifications.
 * Persists the user's reply to Room & Firebase without needing to open the app.
 */
class DirectReplyReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "DirectReplyReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null || intent.action != NexaNotificationManager.ACTION_DIRECT_REPLY) return

        val conversationId = intent.getStringExtra(NexaNotificationManager.EXTRA_CONVERSATION_ID) ?: return
        val notificationId = intent.getIntExtra(NexaNotificationManager.EXTRA_NOTIFICATION_ID, 1001)

        val remoteInputBundle = RemoteInput.getResultsFromIntent(intent)
        val replyText = remoteInputBundle?.getCharSequence(NexaNotificationManager.KEY_TEXT_REPLY)?.toString()

        if (!replyText.isNullOrBlank()) {
            Log.d(TAG, "Direct reply received for conv: $conversationId | text: $replyText")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = MajarrahDatabase.getDatabase(context)
                    val repository = MajarrahRepository(db.majarrahDao())

                    val userMessage = ChatMessage(
                        conversationId = conversationId,
                        senderName = "أنت",
                        senderAvatar = "",
                        text = replyText,
                        timestamp = System.currentTimeMillis(),
                        isFromUser = true,
                        isEncrypted = true,
                        mediaType = "text",
                        deliveryStatus = "sent"
                    )

                    repository.sendMessage(userMessage)

                    // Update the notification with confirmation feedback
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    val repliedNotification = NotificationCompat.Builder(context, NexaNotificationManager.CHANNEL_MESSAGES_ID)
                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                        .setContentTitle("تم إرسال الرد بنجاح ✔️")
                        .setContentText(replyText)
                        .setColor(0xFF00E5FF.toInt())
                        .setTimeoutAfter(3000)
                        .build()

                    notificationManager?.notify(notificationId, repliedNotification)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to persist direct reply", e)
                }
            }
        }
    }
}
