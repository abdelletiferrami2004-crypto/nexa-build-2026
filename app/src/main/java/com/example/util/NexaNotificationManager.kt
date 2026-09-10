package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.MainActivity

/**
 * Push & Local Notifications Manager for NEXA.
 * Sets up High-Priority Notification Channels for Incoming Messages, Calls, and Interactive Direct Replies.
 */
object NexaNotificationManager {

    private const val TAG = "NexaNotificationManager"
    const val CHANNEL_MESSAGES_ID = "nexa_messages_channel"
    const val CHANNEL_CALLS_ID = "nexa_calls_channel"
    const val KEY_TEXT_REPLY = "key_text_reply"
    const val ACTION_DIRECT_REPLY = "com.example.ACTION_DIRECT_REPLY"
    const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // 1. Messages Channel
            val messagesChannel = NotificationChannel(
                CHANNEL_MESSAGES_ID,
                "رسائل NEXA الفورية (Instant Messages)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات الرسائل الفورية والرد المباشر بدون فتح التطبيق"
                enableLights(true)
                lightColor = Color.CYAN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 80, 150)
                setShowBadge(true)
            }

            // 2. Calls Channel
            val callsChannel = NotificationChannel(
                CHANNEL_CALLS_ID,
                "مكالمات NEXA (Voice & Video Calls)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات المكالمات الصوتية والمرئية الواردة"
                enableLights(true)
                lightColor = Color.MAGENTA
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(messagesChannel)
            notificationManager.createNotificationChannel(callsChannel)
            Log.d(TAG, "Notification channels initialized successfully.")
        }
    }

    /**
     * Triggers a Rich Message Notification with inline RemoteInput Direct Reply Action.
     */
    fun showIncomingMessageNotification(
        context: Context,
        conversationId: String,
        senderName: String,
        messageText: String,
        avatarBitmap: Bitmap? = null
    ) {
        try {
            initNotificationChannels(context)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val notificationId = (conversationId.hashCode() and 0x7FFFFFFF) % 10000

            // 1. Main Intent (Open App)
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("conversationId", conversationId)
                putExtra("senderName", senderName)
            }

            val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val contentPendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                contentIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            )

            // 2. Direct Reply RemoteInput Action
            val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("اكتب رداً سريعاً... / Type reply...")
                .build()

            val replyIntent = Intent(context, DirectReplyReceiver::class.java).apply {
                action = ACTION_DIRECT_REPLY
                putExtra(EXTRA_CONVERSATION_ID, conversationId)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }

            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId + 100,
                replyIntent,
                pendingFlags
            )

            val replyAction = NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_send,
                "رد سريع 💬",
                replyPendingIntent
            ).addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val iconRes = android.R.drawable.stat_notify_chat

            val builder = NotificationCompat.Builder(context, CHANNEL_MESSAGES_ID)
                .setSmallIcon(iconRes)
                .setContentTitle(senderName)
                .setContentText(messageText)
                .setStyle(
                    NotificationCompat.MessagingStyle(
                        androidx.core.app.Person.Builder().setName("أنت").build()
                    ).addMessage(
                        messageText,
                        System.currentTimeMillis(),
                        androidx.core.app.Person.Builder().setName(senderName).build()
                    )
                )
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentPendingIntent)
                .addAction(replyAction)
                .setColor(0xFF00E5FF.toInt()) // Neon Cyan

            if (avatarBitmap != null) {
                builder.setLargeIcon(avatarBitmap)
            }

            notificationManager.notify(notificationId, builder.build())
            Log.d(TAG, "Interactive notification displayed for sender: $senderName")
        } catch (e: Throwable) {
            Log.e(TAG, "Error displaying notification", e)
        }
    }
}
