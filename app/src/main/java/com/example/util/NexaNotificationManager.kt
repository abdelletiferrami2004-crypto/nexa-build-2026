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
import com.example.MainActivity

/**
 * Push & Local Notifications Manager for NEXA.
 * Sets up High-Priority Notification Channels for Incoming Messages and Calls.
 */
object NexaNotificationManager {

    private const val TAG = "NexaNotificationManager"
    const val CHANNEL_MESSAGES_ID = "nexa_messages_channel"
    const val CHANNEL_CALLS_ID = "nexa_calls_channel"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // 1. Messages Channel
            val messagesChannel = NotificationChannel(
                CHANNEL_MESSAGES_ID,
                "رسائل NEXA الفورية (Instant Messages)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات الرسائل الفورية ومحادثات NEXA والذكاء الاصطناعي"
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
     * Triggers a local/push incoming message notification banner with direct deep link to the conversation.
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

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("conversationId", conversationId)
                putExtra("senderName", senderName)
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                conversationId.hashCode(),
                intent,
                pendingIntentFlags
            )

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            // Using system notification icon
            val iconRes = android.R.drawable.stat_notify_chat

            val builder = NotificationCompat.Builder(context, CHANNEL_MESSAGES_ID)
                .setSmallIcon(iconRes)
                .setContentTitle(senderName)
                .setContentText(messageText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setColor(0xFF00E5FF.toInt()) // Neon Cyan

            if (avatarBitmap != null) {
                builder.setLargeIcon(avatarBitmap)
            }

            val notificationId = (System.currentTimeMillis() % 10000).toInt()
            notificationManager.notify(notificationId, builder.build())
            Log.d(TAG, "Notification displayed for sender: $senderName")
        } catch (e: Throwable) {
            Log.e(TAG, "Error displaying notification", e)
        }
    }
}
