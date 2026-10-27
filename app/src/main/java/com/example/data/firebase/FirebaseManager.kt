package com.example.data.firebase

import android.net.Uri
import android.util.Log
import com.example.data.model.ChatMessage
import com.example.data.model.Post
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseManager {

    private const val TAG = "NEXA_FirebaseManager"

    private val _isFirebaseAvailable = MutableStateFlow(false)
    val isFirebaseAvailable: StateFlow<Boolean> = _isFirebaseAvailable.asStateFlow()

    private val _currentFirebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val currentFirebaseUser: StateFlow<FirebaseUser?> = _currentFirebaseUser.asStateFlow()

    private val _cloudSyncStatus = MutableStateFlow("موافق وقيد الاتصال بسحابة NEXA Firebase (Server-Side Persistence Active)")
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null

    private var postsListenerRegistration: ListenerRegistration? = null
    private val activeChatListeners = mutableMapOf<String, ListenerRegistration>()

    init {
        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            storage = FirebaseStorage.getInstance()

            // Enable Firestore Server-Side Data Persistence for offline caching
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            firestore?.firestoreSettings = settings

            _isFirebaseAvailable.value = true
            _currentFirebaseUser.value = auth?.currentUser
            auth?.addAuthStateListener { fa ->
                _currentFirebaseUser.value = fa.currentUser
            }
            Log.d(TAG, "Firebase initialized successfully with Server-Side Persistence enabled.")
        } catch (e: Throwable) {
            _isFirebaseAvailable.value = false
            _cloudSyncStatus.value = "وضع السحابة الاحتياطي المحلي (مُجهز للتعديل والتمدد بدون انقطاع)"
            Log.w(TAG, "Firebase not fully configured or google-services.json missing (Safe fallback active)", e)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth?.currentUser

    suspend fun authenticateUserAnonymously(): Boolean {
        val authInstance = auth ?: return false
        if (authInstance.currentUser != null) return true
        return try {
            val result = authInstance.signInAnonymously().await()
            _currentFirebaseUser.value = result.user
            _cloudSyncStatus.value = "تم تسجيل الدخول سحابياً بـ Firebase Auth: ${result.user?.uid?.take(8)}..."
            Log.d(TAG, "Signed in anonymously to Firebase Auth: ${result.user?.uid}")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Anonymous sign-in unavailable on current backend (Google Sign-In required): ${e.message}")
            _cloudSyncStatus.value = "يتطلب تسجيل الدخول بحساب Google (Fallback Mode Active)"
            false
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(Exception("خدمة المصادقة السحابية غير مفعلة"))
        return try {
            val res = authInstance.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            val user = res.user ?: throw Exception("المستخدم غير موجود")
            _currentFirebaseUser.value = user
            _cloudSyncStatus.value = "تم تسجيل الدخول بنجاح عبر البريد: ${user.email}"
            Log.d(TAG, "Signed in with email: ${user.email}, UID: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithEmail error", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        photoUrl: String? = null
    ): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(Exception("خدمة المصادقة السحابية غير مفعلة"))
        return try {
            val res = authInstance.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
            val user = res.user ?: throw Exception("تعذر إنشاء حساب المستخدم")
            
            // Update display name and photo if provided
            if (displayName.isNotBlank() || !photoUrl.isNullOrBlank()) {
                val profileUpdates = UserProfileChangeRequest.Builder().apply {
                    if (displayName.isNotBlank()) setDisplayName(displayName)
                    if (!photoUrl.isNullOrBlank()) setPhotoUri(Uri.parse(photoUrl))
                }.build()
                user.updateProfile(profileUpdates).await()
            }
            
            _currentFirebaseUser.value = user
            _cloudSyncStatus.value = "تم إنشاء الحساب سحابياً بنجاح: ${user.email}"
            Log.d(TAG, "User registered with email: ${user.email}, UID: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "signUpWithEmail error", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleCredential(idToken: String): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(Exception("خدمة المصادقة السحابية غير مفعلة"))
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val res = authInstance.signInWithCredential(credential).await()
            val user = res.user ?: throw Exception("تعذر تسجيل الدخول بحساب Google")
            _currentFirebaseUser.value = user
            _cloudSyncStatus.value = "تم تسجيل الدخول بحساب Google: ${user.displayName ?: user.email}"
            Log.d(TAG, "Signed in with Google credential: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithGoogleCredential error", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
            _currentFirebaseUser.value = null
            _cloudSyncStatus.value = "تم تسجيل الخروج من سحابة Firebase"
            Log.d(TAG, "User signed out successfully")
        } catch (e: Throwable) {
            Log.e(TAG, "Sign out error", e)
        }
    }

    suspend fun saveUserProfileToCloud(profile: UserProfile): Boolean {
        val db = firestore ?: return false
        val userId = auth?.currentUser?.uid ?: profile.firebaseUid.ifBlank { "user_${profile.id}" }
        return try {
            val profileMap = hashMapOf(
                "id" to profile.id,
                "firebaseUid" to userId,
                "name" to profile.name,
                "username" to profile.username,
                "email" to profile.email,
                "bio" to profile.bio,
                "phone" to profile.phone,
                "age" to profile.age,
                "avatarUrl" to profile.avatarUrl,
                "isTeenMode" to profile.isTeenMode,
                "isBiometricEnabled" to profile.isBiometricEnabled,
                "isChatPinEnabled" to profile.isChatPinEnabled,
                "postsCount" to profile.postsCount,
                "followersCount" to profile.followersCount,
                "totalViewsCount" to profile.totalViewsCount,
                "points" to profile.points,
                "isVerified" to profile.isVerified,
                "verificationBadgeCategory" to profile.verificationBadgeCategory,
                "isTwoFactorEnabled" to profile.isTwoFactorEnabled,
                "twoFactorMethod" to profile.twoFactorMethod,
                "dailyAiGenerationsUsed" to profile.dailyAiGenerationsUsed,
                "lastAiGenerationDate" to profile.lastAiGenerationDate,
                "fcmToken" to profile.fcmToken,
                "isLoggedIn" to true,
                "updatedAt" to System.currentTimeMillis(),
                "chatPin" to FieldValue.delete()
            )
            db.collection("nexa_users").document(userId)
                .set(profileMap, SetOptions.merge()).await()
            _cloudSyncStatus.value = "تمت مزامنة الملف الشخصي سحابياً في Firestore Cloud ☁️"
            Log.d(TAG, "Profile saved to Firestore for $userId without chatPin")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save profile to Firestore", e)
            _cloudSyncStatus.value = "فشل المزامنة السحابية: ${e.localizedMessage}"
            false
        }
    }

    suspend fun fetchUserProfileFromCloud(userId: String): UserProfile? {
        val db = firestore ?: return null
        return try {
            val doc = db.collection("nexa_users").document(userId).get().await()
            if (doc.exists()) {
                UserProfile(
                    id = doc.getLong("id")?.toInt() ?: 1,
                    firebaseUid = userId,
                    phone = doc.getString("phone") ?: "",
                    name = doc.getString("name") ?: (auth?.currentUser?.displayName ?: ""),
                    age = doc.getLong("age")?.toInt() ?: 20,
                    isTeenMode = doc.getBoolean("isTeenMode") ?: false,
                    isBiometricEnabled = doc.getBoolean("isBiometricEnabled") ?: true,
                    isChatPinEnabled = doc.getBoolean("isChatPinEnabled") ?: false,
                    chatPin = doc.getString("chatPin") ?: "",
                    isLoggedIn = true,
                    avatarUrl = doc.getString("avatarUrl") ?: "",
                    postsCount = doc.getLong("postsCount")?.toInt() ?: 0,
                    followersCount = doc.getLong("followersCount")?.toInt() ?: 0,
                    totalViewsCount = doc.getLong("totalViewsCount") ?: 0L,
                    points = doc.getLong("points")?.toInt() ?: 0,
                    isVipMember = false,
                    vipTierName = "NEXA Standard",
                    creditsBalance = doc.getLong("creditsBalance")?.toInt() ?: 0,
                    bio = doc.getString("bio") ?: "",
                    username = doc.getString("username") ?: "user_${userId.take(6)}",
                    isContactsSynced = doc.getBoolean("isContactsSynced") ?: false,
                    isVerified = doc.getBoolean("isVerified") ?: false,
                    verificationBadgeCategory = doc.getString("verificationBadgeCategory") ?: "عضو موثق",
                    isTwoFactorEnabled = doc.getBoolean("isTwoFactorEnabled") ?: false,
                    twoFactorMethod = doc.getString("twoFactorMethod") ?: "authenticator",
                    isNexaProSubscriber = false,
                    email = doc.getString("email") ?: (auth?.currentUser?.email ?: ""),
                    fcmToken = doc.getString("fcmToken") ?: "",
                    dailyAiGenerationsUsed = doc.getLong("dailyAiGenerationsUsed")?.toInt() ?: 0,
                    lastAiGenerationDate = doc.getString("lastAiGenerationDate") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch profile from Firestore for $userId", e)
            null
        }
    }

    fun listenToUserProfileRealtime(userId: String, onUpdate: (UserProfile) -> Unit): ListenerRegistration? {
        val db = firestore ?: return null
        if (auth?.currentUser == null) {
            Log.d(TAG, "Postponing listenToUserProfileRealtime: waiting for authenticated session")
            return null
        }
        return db.collection("nexa_users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Real-time user profile notice: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val profile = UserProfile(
                            id = snapshot.getLong("id")?.toInt() ?: 1,
                            firebaseUid = userId,
                            phone = snapshot.getString("phone") ?: "",
                            name = snapshot.getString("name") ?: (auth?.currentUser?.displayName ?: ""),
                            age = snapshot.getLong("age")?.toInt() ?: 20,
                            isTeenMode = snapshot.getBoolean("isTeenMode") ?: false,
                            isBiometricEnabled = snapshot.getBoolean("isBiometricEnabled") ?: true,
                            isChatPinEnabled = snapshot.getBoolean("isChatPinEnabled") ?: false,
                            chatPin = snapshot.getString("chatPin") ?: "",
                            isLoggedIn = true,
                            avatarUrl = snapshot.getString("avatarUrl") ?: "",
                            postsCount = snapshot.getLong("postsCount")?.toInt() ?: 0,
                            followersCount = snapshot.getLong("followersCount")?.toInt() ?: 0,
                            totalViewsCount = snapshot.getLong("totalViewsCount") ?: 0L,
                            points = snapshot.getLong("points")?.toInt() ?: 0,
                            isVipMember = false,
                            vipTierName = "NEXA Standard",
                            creditsBalance = snapshot.getLong("creditsBalance")?.toInt() ?: 0,
                            bio = snapshot.getString("bio") ?: "",
                            username = snapshot.getString("username") ?: "user_${userId.take(6)}",
                            isContactsSynced = snapshot.getBoolean("isContactsSynced") ?: false,
                            isVerified = snapshot.getBoolean("isVerified") ?: false,
                            verificationBadgeCategory = snapshot.getString("verificationBadgeCategory") ?: "عضو موثق",
                            isTwoFactorEnabled = snapshot.getBoolean("isTwoFactorEnabled") ?: false,
                            twoFactorMethod = snapshot.getString("twoFactorMethod") ?: "authenticator",
                            isNexaProSubscriber = false,
                            email = snapshot.getString("email") ?: (auth?.currentUser?.email ?: ""),
                            fcmToken = snapshot.getString("fcmToken") ?: "",
                            dailyAiGenerationsUsed = snapshot.getLong("dailyAiGenerationsUsed")?.toInt() ?: 0,
                            lastAiGenerationDate = snapshot.getString("lastAiGenerationDate") ?: ""
                        )
                        onUpdate(profile)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing real-time user profile", e)
                    }
                }
            }
    }

    suspend fun updateFcmTokenInCloud(token: String) {
        val db = firestore ?: return
        val userId = auth?.currentUser?.uid ?: return
        try {
            db.collection("nexa_users").document(userId)
                .set(mapOf("fcmToken" to token, "updatedAt" to System.currentTimeMillis()), SetOptions.merge()).await()
            Log.d(TAG, "FCM token updated in Firestore for $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update FCM token in Firestore", e)
        }
    }

    suspend fun recordModerationViolation(
        result: com.example.util.NexaSafetyModerator.ModerationResult,
        senderId: String,
        textSnippet: String
    ) {
        val db = firestore ?: return
        try {
            val violationMap = hashMapOf(
                "senderId" to senderId,
                "safetyLevel" to result.safetyLevel.name,
                "reason" to result.reason,
                "actionTaken" to result.actionTaken,
                "flaggedUrl" to (result.flaggedUrl ?: ""),
                "snippet" to textSnippet.take(100),
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("moderation_reports").document("mod_${System.currentTimeMillis()}")
                .set(violationMap, SetOptions.merge()).await()
            Log.w(TAG, "Safety violation logged to Firestore: ${result.reason}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record moderation violation", e)
        }
    }

    suspend fun savePostToCloud(post: Post): Boolean {
        val db = firestore ?: return false
        return try {
            val postMap = hashMapOf(
                "id" to post.id,
                "authorName" to post.authorName,
                "content" to post.content,
                "likesCount" to post.likesCount,
                "commentsCount" to post.commentsCount,
                "isLiked" to post.isLiked,
                "taggedProductId" to post.taggedProductId,
                "isTeenSafe" to post.isTeenSafe,
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("nexa_posts").document("post_${post.id}")
                .set(postMap, SetOptions.merge()).await()
            _cloudSyncStatus.value = "تم نشر المشاركة سحابياً في Firestore Cloud"
            Log.d(TAG, "Post saved to Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save post to Firestore", e)
            false
        }
    }

    suspend fun saveStoryToCloud(story: com.example.data.model.StoryItem): Boolean {
        val db = firestore ?: return false
        return try {
            val storyMap = hashMapOf(
                "id" to story.id,
                "authorName" to story.authorName,
                "authorAvatarUrl" to story.authorAvatarUrl,
                "timestamp" to story.timestamp,
                "text" to story.text,
                "isLikedByMe" to story.isLikedByMe,
                "storyLikes" to story.storyLikes,
                "isVideo" to story.isVideo,
                "reelTitle" to story.reelTitle,
                "reelAuthor" to story.reelAuthor,
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("nexa_stories").document("story_${story.id}")
                .set(storyMap, SetOptions.merge()).await()
            _cloudSyncStatus.value = "تم نشر القصة سحابياً في Firestore Cloud"
            Log.d(TAG, "Story saved to Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save story to Firestore", e)
            false
        }
    }

    fun listenToStoriesRealtime(onStoriesUpdated: (List<com.example.data.model.StoryItem>) -> Unit) {
        val db = firestore ?: return
        if (auth?.currentUser == null) {
            Log.d(TAG, "Postponing listenToStoriesRealtime: waiting for authenticated session")
            return
        }
        db.collection("nexa_stories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Real-time Stories Firestore notice: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val fetchedStories = snapshot.documents.mapNotNull { doc ->
                        try {
                            com.example.data.model.StoryItem(
                                id = doc.getString("id") ?: doc.id,
                                authorName = doc.getString("authorName") ?: "صانع مجرة",
                                authorAvatarUrl = doc.getString("authorAvatarUrl") ?: "",
                                timestamp = doc.getString("timestamp") ?: "الآن",
                                text = doc.getString("text") ?: "",
                                isLikedByMe = doc.getBoolean("isLikedByMe") ?: false,
                                storyLikes = doc.getLong("storyLikes")?.toInt() ?: 0,
                                isVideo = doc.getBoolean("isVideo") ?: false,
                                reelTitle = doc.getString("reelTitle"),
                                reelAuthor = doc.getString("reelAuthor")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (fetchedStories.isNotEmpty()) {
                        onStoriesUpdated(fetchedStories)
                    }
                }
            }
    }

    fun listenToPostsRealtime(onPostsUpdated: (List<Post>) -> Unit) {
        val db = firestore ?: return
        if (auth?.currentUser == null) {
            Log.d(TAG, "Postponing listenToPostsRealtime: waiting for authenticated session")
            return
        }
        postsListenerRegistration?.remove()
        postsListenerRegistration = db.collection("nexa_posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Real-time Posts Firestore notice: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val fetchedPosts = snapshot.documents.mapNotNull { doc ->
                        try {
                            Post(
                                id = doc.getLong("id")?.toInt() ?: (doc.id.hashCode() and 0x7FFFFFFF),
                                authorName = doc.getString("authorName") ?: "مستخدم مجرة",
                                content = doc.getString("content") ?: "",
                                likesCount = doc.getLong("likesCount")?.toInt() ?: 0,
                                commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0,
                                isLiked = doc.getBoolean("isLiked") ?: false,
                                taggedProductId = doc.getLong("taggedProductId")?.toInt(),
                                isTeenSafe = doc.getBoolean("isTeenSafe") ?: true
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (fetchedPosts.isNotEmpty()) {
                        Log.d(TAG, "Real-time posts update received: ${fetchedPosts.size} posts")
                        onPostsUpdated(fetchedPosts)
                    }
                }
            }
    }

    suspend fun uploadMediaToCloudStorage(
        uri: Uri?,
        folderName: String = "posts_media",
        onProgress: (Int) -> Unit = {},
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (uri == null) {
            onError("ملف الوسائط غير موجود")
            return
        }

        val storageInst = storage
        if (storageInst == null) {
            Log.w(TAG, "Storage SDK is unavailable")
            onError("خدمة التخزين السحابي غير متوفرة")
            return
        }

        try {
            val fileName = "${UUID.randomUUID()}_${System.currentTimeMillis()}.jpg"
            val ref = storageInst.reference.child("$folderName/$fileName")
            val uploadTask = ref.putFile(uri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
                onProgress(progress)
            }

            uploadTask.await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d(TAG, "Cloud media uploaded successfully: $downloadUrl")
            onSuccess(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Cloud media upload failed", e)
            onError(e.localizedMessage ?: "فشل في رفع الوسائط إلى السحابة")
        }
    }

    suspend fun saveMessageToCloud(message: ChatMessage): Boolean {
        val db = firestore ?: return false
        return try {
            val encryptedText = if (message.isEncrypted) {
                com.example.util.E2EEncryptionManager.encryptMessage(message.text, message.conversationId)
            } else {
                message.text
            }
            val msgMap = hashMapOf(
                "id" to message.id,
                "conversationId" to message.conversationId,
                "senderName" to message.senderName,
                "senderAvatar" to message.senderAvatar,
                "text" to encryptedText,
                "isFromUser" to message.isFromUser,
                "isEncrypted" to message.isEncrypted,
                "mediaType" to message.mediaType,
                "mediaUrl" to (message.mediaUrl ?: ""),
                "reaction" to (message.reaction ?: ""),
                "deliveryStatus" to message.deliveryStatus,
                "isRead" to message.isRead,
                "timestamp" to System.currentTimeMillis(),
                "isSenderVerified" to message.isSenderVerified
            )
            db.collection("nexa_conversations")
                .document(message.conversationId)
                .collection("messages")
                .document("msg_${message.id}")
                .set(msgMap, SetOptions.merge()).await()
            Log.d(TAG, "Message securely encrypted & saved to Firestore for conv ${message.conversationId}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save chat message to Firestore", e)
            false
        }
    }

    fun listenToMessagesRealtime(
        conversationId: String,
        onMessagesUpdated: (List<ChatMessage>) -> Unit
    ) {
        val db = firestore ?: return
        if (auth?.currentUser == null) {
            Log.d(TAG, "Postponing listenToMessagesRealtime: waiting for authenticated session")
            return
        }
        activeChatListeners[conversationId]?.remove()

        val listener = db.collection("nexa_conversations")
            .document(conversationId)
            .collection("messages")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Real-time Chat Firestore notice for $conversationId: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        try {
                            val rawText = doc.getString("text") ?: ""
                            val isEncrypted = doc.getBoolean("isEncrypted") ?: true
                            val decryptedText = if (isEncrypted) {
                                com.example.util.E2EEncryptionManager.decryptMessage(rawText, conversationId)
                            } else {
                                rawText
                            }
                            ChatMessage(
                                id = doc.getLong("id")?.toInt() ?: (doc.id.hashCode() and 0x7FFFFFFF),
                                conversationId = doc.getString("conversationId") ?: conversationId,
                                senderName = doc.getString("senderName") ?: "مستخدم NEXA",
                                senderAvatar = doc.getString("senderAvatar") ?: "",
                                text = decryptedText,
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                isFromUser = doc.getBoolean("isFromUser") ?: false,
                                isEncrypted = isEncrypted,
                                mediaType = doc.getString("mediaType") ?: "text",
                                mediaUrl = doc.getString("mediaUrl")?.ifBlank { null },
                                reaction = doc.getString("reaction")?.ifBlank { null },
                                deliveryStatus = doc.getString("deliveryStatus") ?: "read",
                                isRead = doc.getBoolean("isRead") ?: true,
                                isSenderVerified = doc.getBoolean("isSenderVerified") ?: false
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (messages.isNotEmpty()) {
                        Log.d(TAG, "Real-time messages decrypted & received for $conversationId: ${messages.size}")
                        onMessagesUpdated(messages)
                    }
                }
            }

        activeChatListeners[conversationId] = listener
    }

    suspend fun loadConversationHistoryFromCloud(conversationId: String): List<ChatMessage> {
        val db = firestore ?: return emptyList()
        return try {
            val snapshot = db.collection("nexa_conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val rawText = doc.getString("text") ?: ""
                    val isEncrypted = doc.getBoolean("isEncrypted") ?: true
                    val decryptedText = if (isEncrypted) {
                        com.example.util.E2EEncryptionManager.decryptMessage(rawText, conversationId)
                    } else {
                        rawText
                    }
                    ChatMessage(
                        id = doc.getLong("id")?.toInt() ?: (doc.id.hashCode() and 0x7FFFFFFF),
                        conversationId = doc.getString("conversationId") ?: conversationId,
                        senderName = doc.getString("senderName") ?: "مستخدم NEXA",
                        senderAvatar = doc.getString("senderAvatar") ?: "",
                        text = decryptedText,
                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                        isFromUser = doc.getBoolean("isFromUser") ?: false,
                        isEncrypted = isEncrypted,
                        mediaType = doc.getString("mediaType") ?: "text",
                        mediaUrl = doc.getString("mediaUrl")?.ifBlank { null },
                        reaction = doc.getString("reaction")?.ifBlank { null },
                        deliveryStatus = doc.getString("deliveryStatus") ?: "read",
                        isRead = doc.getBoolean("isRead") ?: true,
                        isSenderVerified = doc.getBoolean("isSenderVerified") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load cloud history for $conversationId", e)
            emptyList()
        }
    }

    suspend fun saveConversationSummary(conversation: com.example.data.model.Conversation) {
        val db = firestore ?: return
        try {
            val convMap = hashMapOf(
                "id" to conversation.id,
                "contactName" to conversation.contactName,
                "contactAvatar" to conversation.contactAvatar,
                "lastMessage" to conversation.lastMessage,
                "lastTimestamp" to conversation.lastTimestamp,
                "unreadCount" to conversation.unreadCount,
                "isVerified" to conversation.isVerified,
                "isGroup" to conversation.isGroup,
                "isChannel" to conversation.isChannel,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("nexa_conversations").document(conversation.id)
                .set(convMap, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save conversation summary", e)
        }
    }

    var lastDeletionErrorMessage: String? = null
        private set

    suspend fun deleteUserCloudData(userId: String): Boolean {
        lastDeletionErrorMessage = null
        val db = firestore
        return try {
            if (db != null) {
                val userDocRef = db.collection("nexa_users").document(userId)

                // Delete documents in activities subcollection
                val activitiesDocs = userDocRef.collection("activities").get().await()
                for (doc in activitiesDocs.documents) {
                    doc.reference.delete().await()
                }

                // Delete documents in settings subcollection
                val settingsDocs = userDocRef.collection("settings").get().await()
                for (doc in settingsDocs.documents) {
                    doc.reference.delete().await()
                }

                // Delete root user document
                userDocRef.delete().await()
                Log.d(TAG, "GDPR cloud profile and subcollections erased for $userId")
            }

            // Delete the FirebaseAuth account
            val user = auth?.currentUser
            user?.delete()?.await()
            Log.d(TAG, "FirebaseAuth user account deleted successfully")
            true
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Log.e(TAG, "Re-authentication required to delete account for $userId", e)
            lastDeletionErrorMessage = "يتطلب حذف الحساب إعادة تسجيل الدخول لدواعي الأمان. يرجى تسجيل الدخول مجدداً ثم إعادة المحاولة."
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to erase cloud data for $userId", e)
            lastDeletionErrorMessage = e.localizedMessage ?: "فشل في حذف بيانات الحساب"
            false
        }
    }

    suspend fun submitReportToFirestore(
        reportType: String, // "ABUSE_REPORT" or "TECH_SUPPORT"
        targetSubjectOrUser: String,
        category: String,
        details: String,
        senderContact: String,
        severityLevel: String = "NORMAL"
    ): Pair<Boolean, String> {
        val db = firestore
        if (db == null) {
            Log.e(TAG, "Firestore is unavailable for report")
            return Pair(false, "خدمة قاعدة البيانات السحابية (Firestore) غير متوفرة")
        }

        return try {
            val docRef = db.collection("reports").document()
            val docId = docRef.id
            val ticketCode = "REP-" + docId.take(8).uppercase()

            val reportData = hashMapOf(
                "reportId" to docId,
                "ticketCode" to ticketCode,
                "reportType" to reportType,
                "target" to targetSubjectOrUser,
                "category" to category,
                "details" to details,
                "senderContact" to senderContact,
                "severityLevel" to severityLevel,
                "timestamp" to System.currentTimeMillis(),
                "status" to "PENDING_REVIEW",
                "appVersion" to "NEXA 2026.1.0",
                "platform" to "Android Jetpack Compose"
            )

            docRef.set(reportData).await()
            Log.d(TAG, "Report $ticketCode ($docId) saved to Firestore collection 'reports' successfully")
            Pair(true, ticketCode)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore write error for report", e)
            Pair(false, e.localizedMessage ?: "فشل إرسال البلاغ إلى السحابة")
        }
    }

    suspend fun recordUserActivityInFirestore(
        userId: String,
        title: String,
        type: String,
        details: String = "",
        metadata: Map<String, Any> = emptyMap()
    ): Boolean {
        val db = firestore ?: return false
        return try {
            val activityId = "act_${System.currentTimeMillis()}_${(1000..9999).random()}"
            val activityMap = hashMapOf<String, Any>(
                "activityId" to activityId,
                "userId" to userId,
                "title" to title,
                "type" to type,
                "details" to details,
                "timestamp" to System.currentTimeMillis(),
                "metadata" to metadata
            )
            db.collection("nexa_users").document(userId)
                .collection("activities").document(activityId)
                .set(activityMap, SetOptions.merge()).await()
            Log.d(TAG, "Activity '$title' recorded to Firestore for user $userId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record activity in Firestore", e)
            false
        }
    }

    suspend fun saveUserPreferencesToFirestore(
        userId: String,
        preferences: Map<String, Any>
    ): Boolean {
        val db = firestore ?: return false
        return try {
            val prefData = preferences.toMutableMap()
            prefData["updatedAt"] = System.currentTimeMillis()
            db.collection("nexa_users").document(userId)
                .collection("settings").document("preferences")
                .set(prefData, SetOptions.merge()).await()
            Log.d(TAG, "Preferences saved to Firestore for user $userId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save preferences to Firestore", e)
            false
        }
    }
}
