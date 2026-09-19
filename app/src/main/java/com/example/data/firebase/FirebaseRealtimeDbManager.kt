package com.example.data.firebase

import android.os.Build
import android.util.Log
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * FirebaseRealtimeDbManager:
 * Dedicated manager for live synchronization with the primary Firebase Realtime Database backend:
 * Endpoint: https://nexa-system-default-rtdb.firebaseio.com/
 *
 * Handles:
 * 1. Live server maintenance check (/server_status and /maintenance_message)
 * 2. System flags and configurations (/system_flags)
 * 3. User session presence & device synchronization (/users/{uid}/session)
 * 4. Pro/VIP subscription live status (/users/{uid}/subscription)
 * 5. Daily AI generation quotas & history logging (/users/{uid}/quota, /users/{uid}/ai_generations)
 * 6. Realtime chat synchronization (/chats/{conversationId}/messages)
 */
object FirebaseRealtimeDbManager {

    private const val TAG = "NEXA_RTDB"
    const val PRIMARY_RTDB_URL = "https://nexa-system-default-rtdb.firebaseio.com/"

    private var database: FirebaseDatabase? = null
    private var isInitialized = false

    // Realtime Server Status & Maintenance State
    private val _serverStatus = MutableStateFlow("ONLINE")
    val serverStatus: StateFlow<String> = _serverStatus.asStateFlow()

    private val _maintenanceMessage = MutableStateFlow("خوادم نكسا تعمل بكفاءة عالية (All Systems Operational)")
    val maintenanceMessage: StateFlow<String> = _maintenanceMessage.asStateFlow()

    private val _isConnectedToRtdb = MutableStateFlow(false)
    val isConnectedToRtdb: StateFlow<Boolean> = _isConnectedToRtdb.asStateFlow()

    private val _systemFlags = MutableStateFlow<Map<String, Any>>(emptyMap())
    val systemFlags: StateFlow<Map<String, Any>> = _systemFlags.asStateFlow()

    // Active User Context
    private var currentUserId: String? = null
    private var serverStatusListener: ValueEventListener? = null
    private var maintenanceMessageListener: ValueEventListener? = null
    private var systemFlagsListener: ValueEventListener? = null
    private var connectionListener: ValueEventListener? = null
    private var subscriptionListener: ValueEventListener? = null
    private var quotaListener: ValueEventListener? = null

    /**
     * Initializes the Firebase Realtime Database with the primary backend URL.
     */
    fun init() {
        if (isInitialized) return

        try {
            val db = FirebaseDatabase.getInstance(PRIMARY_RTDB_URL)
            try {
                db.setPersistenceEnabled(true)
            } catch (e: Throwable) {
                // Persistence can only be set once before any other reference is created
                Log.w(TAG, "Persistence setting note: ${e.message}")
            }
            database = db
            isInitialized = true
            Log.d(TAG, "Initialized Realtime Database pointing to: $PRIMARY_RTDB_URL")

            // Attach core system listeners immediately at startup
            setupConnectionPresenceListener()
            setupServerStatusAndMaintenanceListeners()
            setupSystemFlagsListener()
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to initialize Firebase Realtime Database", e)
        }
    }

    private fun getDb(): FirebaseDatabase? {
        if (database == null) {
            init()
        }
        return database
    }

    /**
     * Listens to the client's live connection state (.info/connected)
     */
    private fun setupConnectionPresenceListener() {
        val db = getDb() ?: return
        try {
            val connectedRef = db.getReference(".info/connected")
            connectionListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    _isConnectedToRtdb.value = connected
                    Log.d(TAG, "RTDB Connection state changed: $connected")

                    // If user is logged in, sync online status presence
                    currentUserId?.let { uid ->
                        if (connected) {
                            val userPresenceRef = db.getReference("users").child(uid).child("session")
                            userPresenceRef.child("isOnline").setValue(true)
                            userPresenceRef.child("lastSeen").setValue(ServerValue.TIMESTAMP)

                            // Configure automatic disconnect cleanup on server side
                            val disconnectMap = mapOf(
                                "isOnline" to false,
                                "lastSeen" to ServerValue.TIMESTAMP
                            )
                            userPresenceRef.onDisconnect().updateChildren(disconnectMap)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Connection listener cancelled: ${error.message}")
                }
            }
            connectedRef.addValueEventListener(connectionListener as ValueEventListener)
        } catch (e: Throwable) {
            Log.e(TAG, "Error attaching connected presence listener", e)
        }
    }

    /**
     * Implement listeners for /server_status and /maintenance_message at startup.
     * If server_status is ONLINE, proceed normally.
     */
    private fun setupServerStatusAndMaintenanceListeners() {
        val db = getDb() ?: return
        try {
            // 1. /server_status
            val statusRef = db.getReference("server_status")
            statusRef.keepSynced(true)
            serverStatusListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)?.trim()?.uppercase() ?: "ONLINE"
                    _serverStatus.value = status
                    Log.d(TAG, "Live server_status update: $status")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "server_status listener cancelled: ${error.message}")
                    // Safe fallback: keep ONLINE so user is never stranded
                    _serverStatus.value = "ONLINE"
                }
            }
            statusRef.addValueEventListener(serverStatusListener as ValueEventListener)

            // 2. /maintenance_message
            val maintenanceRef = db.getReference("maintenance_message")
            maintenanceRef.keepSynced(true)
            maintenanceMessageListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val message = snapshot.getValue(String::class.java)
                    if (!message.isNullOrBlank()) {
                        _maintenanceMessage.value = message
                    } else {
                        _maintenanceMessage.value = "خوادم NEXA تخضع لعمليات صيانة وتحديث مجدولة لرفع كفاءة الذكاء الاصطناعي وتشفير البيانات. سنعود للعمل فوراً!"
                    }
                    Log.d(TAG, "Live maintenance_message update: ${_maintenanceMessage.value}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "maintenance_message listener cancelled: ${error.message}")
                }
            }
            maintenanceRef.addValueEventListener(maintenanceMessageListener as ValueEventListener)
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting up server_status / maintenance_message listeners", e)
            _serverStatus.value = "ONLINE"
        }
    }

    /**
     * Listens to system-wide flags (/system_flags)
     */
    private fun setupSystemFlagsListener() {
        val db = getDb() ?: return
        try {
            val flagsRef = db.getReference("system_flags")
            flagsRef.keepSynced(true)
            systemFlagsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = mutableMapOf<String, Any>()
                    for (child in snapshot.children) {
                        val key = child.key ?: continue
                        child.value?.let { map[key] = it }
                    }
                    _systemFlags.value = map
                    Log.d(TAG, "Live system_flags loaded: ${map.size} flags")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "system_flags listener cancelled: ${error.message}")
                }
            }
            flagsRef.addValueEventListener(systemFlagsListener as ValueEventListener)
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting up system_flags listener", e)
        }
    }

    /**
     * Sync user session data directly to /users/{userId}/session
     */
    fun syncUserSession(
        userId: String,
        username: String,
        email: String,
        phone: String = ""
    ) {
        val db = getDb() ?: return
        currentUserId = userId

        try {
            val sessionRef = db.getReference("users").child(userId).child("session")
            val sessionData = hashMapOf<String, Any>(
                "userId" to userId,
                "username" to username,
                "email" to email,
                "phone" to phone,
                "deviceModel" to "${Build.MANUFACTURER} ${Build.MODEL}",
                "androidVersion" to Build.VERSION.RELEASE,
                "appVersion" to "1.0-Production",
                "isOnline" to true,
                "lastActive" to ServerValue.TIMESTAMP,
                "loginTimestamp" to System.currentTimeMillis()
            )

            sessionRef.updateChildren(sessionData)

            // Setup onDisconnect hook
            val disconnectMap = mapOf(
                "isOnline" to false,
                "lastActive" to ServerValue.TIMESTAMP
            )
            sessionRef.onDisconnect().updateChildren(disconnectMap)

            Log.d(TAG, "Synced session for user: $userId to RTDB")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to sync user session", e)
        }
    }

    /**
     * Sync Pro subscription status to /users/{userId}/subscription
     */
    fun syncProSubscriptionStatus(
        userId: String,
        isPro: Boolean,
        planName: String = if (isPro) "NEXA AI PRO 👑" else "Free Tier",
        expiryTimestamp: Long = if (isPro) System.currentTimeMillis() + 30L * 86400000L else 0L
    ) {
        val db = getDb() ?: return
        try {
            val subRef = db.getReference("users").child(userId).child("subscription")
            val subData = hashMapOf<String, Any>(
                "isPro" to isPro,
                "isVip" to isPro,
                "planName" to planName,
                "expiryTimestamp" to expiryTimestamp,
                "lastUpdated" to ServerValue.TIMESTAMP
            )
            subRef.updateChildren(subData)
            Log.d(TAG, "Synced Pro subscription for user $userId (isPro=$isPro)")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to sync Pro subscription status", e)
        }
    }

    /**
     * Observe live subscription changes from RTDB (/users/{userId}/subscription)
     */
    fun listenToSubscriptionStatus(
        userId: String,
        onUpdate: (isPro: Boolean, plan: String) -> Unit
    ) {
        val db = getDb() ?: return
        try {
            subscriptionListener?.let {
                db.getReference("users").child(userId).child("subscription").removeEventListener(it)
            }
            val subRef = db.getReference("users").child(userId).child("subscription")
            subRef.keepSynced(true)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isPro = snapshot.child("isPro").getValue(Boolean::class.java)
                        ?: snapshot.child("isVip").getValue(Boolean::class.java)
                        ?: false
                    val plan = snapshot.child("planName").getValue(String::class.java) ?: "Free"
                    onUpdate(isPro, plan)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Subscription listener cancelled: ${error.message}")
                }
            }
            subscriptionListener = listener
            subRef.addValueEventListener(listener)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to listen to subscription status", e)
        }
    }

    /**
     * Log and sync an AI generation request to /users/{userId}/ai_generations
     * and update the user's daily quota in /users/{userId}/quota
     */
    fun logAiGeneration(
        userId: String,
        prompt: String,
        modelName: String,
        mediaType: String = "chat",
        isPro: Boolean = false,
        usedCount: Int = 1
    ) {
        val db = getDb() ?: return
        try {
            val genId = UUID.randomUUID().toString()
            val genRef = db.getReference("users").child(userId).child("ai_generations").child(genId)

            val genData = hashMapOf<String, Any>(
                "id" to genId,
                "prompt" to prompt.take(500),
                "model" to modelName,
                "mediaType" to mediaType,
                "isPro" to isPro,
                "timestamp" to ServerValue.TIMESTAMP
            )
            genRef.setValue(genData)

            // Update user daily quota
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val quotaRef = db.getReference("users").child(userId).child("quota")
            val quotaUpdate = hashMapOf<String, Any>(
                "dailyUsed" to usedCount,
                "isPro" to isPro,
                "maxDailyLimit" to if (isPro) 999999 else 3,
                "date" to todayStr,
                "lastGenerationTimestamp" to ServerValue.TIMESTAMP
            )
            quotaRef.updateChildren(quotaUpdate)

            Log.d(TAG, "Logged AI generation ($mediaType) for user $userId to RTDB")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to log AI generation to RTDB", e)
        }
    }

    /**
     * Listen to user quota changes live from RTDB (/users/{userId}/quota)
     */
    fun listenToUserQuota(
        userId: String,
        onUpdate: (usedCount: Int, maxLimit: Int) -> Unit
    ) {
        val db = getDb() ?: return
        try {
            quotaListener?.let {
                db.getReference("users").child(userId).child("quota").removeEventListener(it)
            }
            val quotaRef = db.getReference("users").child(userId).child("quota")
            quotaRef.keepSynced(true)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val used = snapshot.child("dailyUsed").getValue(Int::class.java) ?: 0
                    val max = snapshot.child("maxDailyLimit").getValue(Int::class.java) ?: 3
                    onUpdate(used, max)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Quota listener cancelled: ${error.message}")
                }
            }
            quotaListener = listener
            quotaRef.addValueEventListener(listener)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to attach quota listener", e)
        }
    }

    /**
     * Sync chat message live to /chats/{conversationId}/messages/{msgId}
     */
    fun syncChatMessage(
        conversationId: String,
        message: ChatMessage
    ) {
        val db = getDb() ?: return
        try {
            val msgKey = if (message.id != 0) message.id.toString() else UUID.randomUUID().toString()
            val msgRef = db.getReference("chats").child(conversationId).child("messages").child(msgKey)

            val msgData = hashMapOf<String, Any?>(
                "id" to message.id,
                "conversationId" to conversationId,
                "senderName" to message.senderName,
                "senderAvatar" to message.senderAvatar,
                "text" to message.text,
                "timestamp" to message.timestamp,
                "isFromUser" to message.isFromUser,
                "isEncrypted" to message.isEncrypted,
                "mediaType" to message.mediaType,
                "mediaUrl" to message.mediaUrl,
                "reaction" to message.reaction,
                "deliveryStatus" to message.deliveryStatus,
                "isRead" to message.isRead,
                "isSenderVerified" to message.isSenderVerified,
                "isHdPro" to message.isHdPro,
                "syncTimestamp" to ServerValue.TIMESTAMP
            )
            msgRef.setValue(msgData)

            // Update conversation last activity node
            val convSummaryRef = db.getReference("conversations_meta").child(conversationId)
            val summaryData = hashMapOf<String, Any>(
                "id" to conversationId,
                "lastMessage" to message.text.take(120),
                "lastTimestamp" to message.timestamp,
                "lastSender" to message.senderName,
                "serverUpdated" to ServerValue.TIMESTAMP
            )
            convSummaryRef.updateChildren(summaryData)

            Log.d(TAG, "Synced message to RTDB /chats/$conversationId/messages/$msgKey")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to sync message to RTDB", e)
        }
    }

    /**
     * Listen to chat messages live from RTDB (/chats/{conversationId}/messages)
     */
    fun listenToChatMessages(
        conversationId: String,
        onMessageAdded: (ChatMessage) -> Unit
    ): ChildEventListener? {
        val db = getDb() ?: return null
        try {
            val messagesRef = db.getReference("chats").child(conversationId).child("messages")
            messagesRef.keepSynced(true)

            val childListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        val text = snapshot.child("text").getValue(String::class.java) ?: return
                        val senderName = snapshot.child("senderName").getValue(String::class.java) ?: "المستخدم"
                        val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                        val isFromUser = snapshot.child("isFromUser").getValue(Boolean::class.java) ?: false
                        val mediaType = snapshot.child("mediaType").getValue(String::class.java) ?: "text"
                        val mediaUrl = snapshot.child("mediaUrl").getValue(String::class.java)
                        val id = snapshot.child("id").getValue(Int::class.java) ?: 0
                        val isEncrypted = snapshot.child("isEncrypted").getValue(Boolean::class.java) ?: true

                        val chatMessage = ChatMessage(
                            id = id,
                            conversationId = conversationId,
                            senderName = senderName,
                            text = text,
                            timestamp = timestamp,
                            isFromUser = isFromUser,
                            mediaType = mediaType,
                            mediaUrl = mediaUrl,
                            isEncrypted = isEncrypted
                        )
                        onMessageAdded(chatMessage)
                    } catch (e: Throwable) {
                        Log.w(TAG, "Error parsing incoming RTDB chat message", e)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Chat messages listener cancelled: ${error.message}")
                }
            }

            messagesRef.addChildEventListener(childListener)
            return childListener
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to attach RTDB chat messages listener", e)
            return null
        }
    }

    /**
     * Safe developer / testing helper to simulate or force server status
     */
    fun setLocalServerStatusForTesting(status: String) {
        _serverStatus.value = status.uppercase()
    }
}
