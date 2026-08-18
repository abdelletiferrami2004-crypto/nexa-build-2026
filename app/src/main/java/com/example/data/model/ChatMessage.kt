package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val conversationId: String,
    val senderName: String,
    val senderAvatar: String = "",
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromUser: Boolean = false,
    val isEncrypted: Boolean = true,
    val mediaType: String = "text", // "text", "voice", "image", "product"
    val mediaUrl: String? = null,
    val reaction: String? = null,
    val deliveryStatus: String = "read", // "sending", "sent", "delivered", "read"
    val isRead: Boolean = true,
    val isSenderVerified: Boolean = false
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val contactName: String,
    val contactAvatar: String = "",
    val lastMessage: String,
    val lastTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinRequired: Boolean = true,
    val isMessageRequest: Boolean = false,
    val requestStatus: String = "accepted", // "accepted", "pending", "ignored", "blocked"
    val isBlocked: Boolean = false,
    val targetUserId: String = "",
    val isContactVerified: Boolean = true,
    val isVerified: Boolean = true
)
