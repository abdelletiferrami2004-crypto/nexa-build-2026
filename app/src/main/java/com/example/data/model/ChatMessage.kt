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
    val isSenderVerified: Boolean = false,
    val translatedText: String? = null,
    val translatedLanguage: String? = null, // "ar", "en", "fr"
    val isTranslating: Boolean = false,
    val showTranslation: Boolean = false,
    val isModerationFlagged: Boolean = false,
    val moderationWarning: String? = null,
    val isGroupMessage: Boolean = false,
    val senderRole: String = "member" // "owner", "admin", "creator", "bot", "member"
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
    val isVerified: Boolean = true,
    val isGroup: Boolean = false,
    val memberCount: Int = 1,
    val pinnedMessage: String? = null,
    val groupChannelDescription: String? = null,
    val isChannel: Boolean = false,
    val isFastCached: Boolean = true
)
