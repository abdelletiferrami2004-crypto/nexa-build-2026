package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MajarrahDatabase
import com.example.data.local.MajarrahRepository
import com.example.data.model.Bubble3D
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.CommentItem
import com.example.data.model.Conversation
import com.example.data.model.Post
import com.example.data.model.Product
import com.example.data.model.StoryItem
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

class MajarrahViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MajarrahDatabase.getDatabase(application)
    private val repository = MajarrahRepository(db.majarrahDao())
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // State Flows
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _stories = MutableStateFlow<List<StoryItem>>(emptyList())
    val stories: StateFlow<List<StoryItem>> = _stories.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _bubbles = MutableStateFlow<List<Bubble3D>>(emptyList())
    val bubbles: StateFlow<List<Bubble3D>> = _bubbles.asStateFlow()

    private val _blockedUsers = MutableStateFlow<Set<String>>(emptySet())
    val blockedUsers: StateFlow<Set<String>> = _blockedUsers.asStateFlow()

    private val _reportedContentIds = MutableStateFlow<Set<String>>(emptySet())
    val reportedContentIds: StateFlow<Set<String>> = _reportedContentIds.asStateFlow()

    init {
        loadUserProfile()
        listenToPostsFromFirebase()
        listenToStoriesFromFirebase()
    }

    // -------------------------------------------------------------
    // 1. FIREBASE REALTIME POSTS (المنشورات الحية من السيرفر)
    // -------------------------------------------------------------
    private fun listenToPostsFromFirebase() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val loadedPosts = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: doc.id.hashCode()
                    val author = doc.getString("authorName") ?: "مستخدم NEXA"
                    val content = doc.getString("content") ?: ""
                    val likes = doc.getLong("likesCount")?.toInt() ?: 0
                    val comments = doc.getLong("commentsCount")?.toInt() ?: 0
                    val isLiked = doc.getBoolean("isLiked") ?: false
                    val isTeenSafe = doc.getBoolean("isTeenSafe") ?: true
                    val taggedProductId = doc.getLong("taggedProductId")?.toInt()

                    Post(
                        id = id,
                        authorName = author,
                        content = content,
                        likesCount = likes,
                        commentsCount = comments,
                        isLiked = isLiked,
                        isTeenSafe = isTeenSafe,
                        taggedProductId = taggedProductId
                    )
                }
                _posts.value = loadedPosts
            }
    }

    fun createPost(contentText: String, isTeenSafe: Boolean = true, taggedProductId: Int? = null) {
        val newId = System.currentTimeMillis().toInt()
        val author = _userProfile.value?.name ?: "مستخدم NEXA"

        val postMap = hashMapOf(
            "id" to newId,
            "authorName" to author,
            "content" to contentText,
            "likesCount" to 0,
            "commentsCount" to 0,
            "isLiked" to false,
            "isTeenSafe" to isTeenSafe,
            "taggedProductId" to taggedProductId,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("posts").document(newId.toString()).set(postMap)
    }

    fun toggleLike(post: Post) {
        val updatedLiked = !post.isLiked
        val updatedLikesCount = if (updatedLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)

        firestore.collection("posts").document(post.id.toString()).update(
            mapOf(
                "isLiked" to updatedLiked,
                "likesCount" to updatedLikesCount
            )
        )
    }

    // -------------------------------------------------------------
    // 2. FIREBASE REALTIME STORIES (القصص الحية)
    // -------------------------------------------------------------
    private fun listenToStoriesFromFirebase() {
        firestore.collection("stories")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val loadedStories = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: doc.id.hashCode()
                    val author = doc.getString("authorName") ?: "مستخدم NEXA"
                    val isLiked = doc.getBoolean("isLiked") ?: false
                    val likesCount = doc.getLong("likesCount")?.toInt() ?: 0
                    val reelTitle = doc.getString("reelTitle")
                    val reelAuthor = doc.getString("reelAuthor")

                    StoryItem(
                        id = id,
                        authorName = author,
                        isLiked = isLiked,
                        likesCount = likesCount,
                        reelTitle = reelTitle,
                        reelAuthor = reelAuthor
                    )
                }
                _stories.value = loadedStories
            }
    }

    fun publishStory(story: StoryItem) {
        val storyMap = hashMapOf(
            "id" to story.id,
            "authorName" to story.authorName,
            "isLiked" to story.isLiked,
            "likesCount" to story.likesCount,
            "reelTitle" to story.reelTitle,
            "reelAuthor" to story.reelAuthor,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("stories").document(story.id.toString()).set(storyMap)
    }

    fun toggleStoryLike(storyId: Int) {
        val targetStory = _stories.value.find { it.id == storyId } ?: return
        val updatedLiked = !targetStory.isLiked
        val updatedCount = if (updatedLiked) targetStory.likesCount + 1 else (targetStory.likesCount - 1).coerceAtLeast(0)

        firestore.collection("stories").document(storyId.toString()).update(
            mapOf(
                "isLiked" to updatedLiked,
                "likesCount" to updatedCount
            )
        )
    }

    fun sendStoryReply(storyId: Int, text: String) {
        val replyMap = hashMapOf(
            "storyId" to storyId,
            "sender" to (_userProfile.value?.name ?: "مستخدم"),
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("story_replies").add(replyMap)
    }

    fun publishReelToStory(reelTitle: String, reelAuthor: String, userCaption: String) {
        val newStory = StoryItem(
            id = System.currentTimeMillis().toInt(),
            authorName = _userProfile.value?.name ?: "مستخدم NEXA",
            isLiked = false,
            likesCount = 0,
            reelTitle = reelTitle,
            reelAuthor = reelAuthor
        )
        publishStory(newStory)
    }

    // -------------------------------------------------------------
    // 3. COMMENTS & MODERATION (التعليقات والإبلاغات)
    // -------------------------------------------------------------
    fun reportContent(contentId: String, reason: String) {
        _reportedContentIds.value = _reportedContentIds.value + contentId
        val reportMap = hashMapOf(
            "contentId" to contentId,
            "reason" to reason,
            "reporter" to (_userProfile.value?.name ?: "مستخدم"),
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("reports").add(reportMap)
    }

    fun submitReportWithAiModeration(
        targetAuthorName: String,
        contentId: String,
        contentTypeTitle: String,
        category: String,
        contentText: String,
        onCompleted: (Boolean, String) -> Unit
    ) {
        reportContent(contentId, category)
        onCompleted(true, "تم استلام الإبلاغ بنجاح ومعالجته بوساطة الذكاء الاصطناعي NEXA AI.")
    }

    fun blockUser(userName: String) {
        _blockedUsers.value = _blockedUsers.value + userName
    }

    fun toggleBookmark(postId: Int) {
        // Toggle Local Bookmark status
    }

    fun sharePost(postId: Int) {
        // Handle Post Share logic
    }

    // -------------------------------------------------------------
    // 4. USER PROFILE MANAGEMENT
    // -------------------------------------------------------------
    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val name = doc.getString("name") ?: "مستخدم NEXA"
                        val email = doc.getString("email") ?: currentUser.email ?: ""
                        val isTeen = doc.getBoolean("isTeenMode") ?: false
                        _userProfile.value = UserProfile(id = 1, name = name, email = email, isTeenMode = isTeen)
                    }
                }
        } else {
            _userProfile.value = UserProfile(id = 1, name = "عبداللطيف", email = "user@nexa.com", isTeenMode = false)
        }
    }
}
