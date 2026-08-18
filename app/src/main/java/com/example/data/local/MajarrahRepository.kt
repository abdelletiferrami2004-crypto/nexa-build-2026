package com.example.data.local

import com.example.data.firebase.FirebaseManager
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.Post
import com.example.data.model.Product
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MajarrahRepository(private val dao: MajarrahDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val allPosts: Flow<List<Post>> = dao.getAllPosts()
    val allConversations: Flow<List<Conversation>> = dao.getAllConversations()
    val cartItems: Flow<List<CartItem>> = dao.getCartItems()

    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return dao.getMessagesForConversation(conversationId)
    }

    suspend fun saveProfile(profile: UserProfile) {
        dao.insertOrUpdateProfile(profile)
        FirebaseManager.saveUserProfileToCloud(profile)
    }

    suspend fun addPost(post: Post) {
        dao.insertPost(post)
        FirebaseManager.savePostToCloud(post)
    }

    suspend fun toggleLikePost(post: Post) {
        val updated = post.copy(
            isLiked = !post.isLiked,
            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
        )
        dao.updatePost(updated)
        FirebaseManager.savePostToCloud(updated)
    }

    suspend fun saveConversation(conversation: Conversation) {
        dao.insertConversation(conversation)
    }

    suspend fun sendMessage(message: ChatMessage): ChatMessage {
        val rowId = dao.insertMessage(message)
        val savedMsg = if (message.id == 0) message.copy(id = rowId.toInt()) else message
        FirebaseManager.saveMessageToCloud(savedMsg)
        return savedMsg
    }

    suspend fun updateMessageStatus(message: ChatMessage, status: String, isRead: Boolean = (status == "read")) {
        val updated = message.copy(deliveryStatus = status, isRead = isRead)
        dao.insertMessage(updated)
        FirebaseManager.saveMessageToCloud(updated)
    }

    suspend fun updateMessageReaction(message: ChatMessage, newReaction: String?) {
        val updated = message.copy(reaction = if (message.reaction == newReaction) null else newReaction)
        dao.insertMessage(updated)
        FirebaseManager.saveMessageToCloud(updated)
    }

    fun startCloudRealtimeSync(scope: kotlinx.coroutines.CoroutineScope) {
        FirebaseManager.listenToPostsRealtime { fetchedPosts ->
            scope.launch {
                dao.insertPosts(fetchedPosts)
            }
        }
    }

    fun listenToConversationRealtime(conversationId: String, scope: kotlinx.coroutines.CoroutineScope) {
        FirebaseManager.listenToMessagesRealtime(conversationId) { fetchedMessages ->
            scope.launch {
                dao.insertMessages(fetchedMessages)
            }
        }
    }

    suspend fun uploadPostMediaToCloud(
        uri: android.net.Uri?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseManager.uploadMediaToCloudStorage(
            uri = uri,
            folderName = "nexa_feed_media",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    suspend fun addToCart(product: Product) {
        dao.insertCartItem(
            CartItem(
                productId = product.id,
                title = product.title,
                price = product.price,
                imageUrl = product.imageUrl,
                quantity = 1
            )
        )
    }

    suspend fun removeFromCart(productId: Int) {
        dao.removeCartItem(productId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun deleteAccountAndAllDataGdpr(userId: String) {
        dao.deleteAllProfiles()
        dao.deleteAllPosts()
        dao.deleteAllConversations()
        dao.deleteAllMessages()
        dao.clearCart()
        dao.deleteAllProducts()
        FirebaseManager.deleteUserCloudData(userId)
    }

    suspend fun populateInitialDataIfEmpty() {
        // Pre-populate Profile if null
        val defaultProfile = UserProfile(
            id = 1,
            phone = "+966 55 987 6543",
            name = "سارة النمر",
            age = 16,
            isTeenMode = true,
            isBiometricEnabled = true,
            isChatPinEnabled = false,
            chatPin = "",
            isLoggedIn = true,
            postsCount = 18,
            followersCount = 1_250_000,
            totalViewsCount = 1_450_000L,
            points = 890,
            isVerified = true,
            verificationBadgeCategory = "صانع محتوى موثق",
            isTwoFactorEnabled = true,
            twoFactorMethod = "authenticator"
        )
        dao.insertOrUpdateProfile(defaultProfile)

        // Seed Sample Products
        val sampleProducts = listOf(
            Product(
                id = 1,
                title = "نظارة واقع افتراضي GlassVR Neon",
                price = 899.0,
                category = "إلكترونيات",
                imageUrl = "https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=600&auto=format&fit=crop",
                rating = 4.9f,
                isTeenFriendly = true,
                isFeatured = true,
                description = "نظارة واقع افتراضي فائقة الخفة بتصميم زجاجي نيون ودقة 4K تناسب الألعاب والتواصل ثلاثي الأبعاد."
            ),
            Product(
                id = 2,
                title = "ساعة مجرة الذكية CyberWatch Pro",
                price = 649.0,
                category = "إلكترونيات",
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop",
                rating = 4.8f,
                isTeenFriendly = true,
                isFeatured = true,
                description = "ساعة ذكية بشاشة OLED هولوجرافية لقياس المؤشرات الحيوية والتنبهات الفورية."
            ),
            Product(
                id = 3,
                title = "سماعات رأس لاسلكية Hologram Sound",
                price = 399.0,
                category = "إلكترونيات",
                imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop",
                rating = 4.7f,
                isTeenFriendly = true,
                isFeatured = false,
                description = "سماعات إلغاء الضوضاء المحيطية بلمسات مضيئة وألوان متغيرة."
            ),
            Product(
                id = 4,
                title = "حقيبة الظهر المستقبلي المضيئة Lumos",
                price = 280.0,
                category = "أزياء",
                imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop",
                rating = 4.6f,
                isTeenFriendly = true,
                isFeatured = true,
                description = "حقيبة مريحة مقاومة للماء مع شريحة شحن ذكية وألياف ضوئية للناشئة."
            ),
            Product(
                id = 5,
                title = "مجموعة أدوات البرمجة للأذكياء CyberCode Kit",
                price = 320.0,
                category = "ألعاب وتعليم",
                imageUrl = "https://images.unsplash.com/photo-1517048676732-d65bc937f952?w=600&auto=format&fit=crop",
                rating = 4.9f,
                isTeenFriendly = true,
                isFeatured = true,
                description = "حقيبة تعليمية برمجية تفاعلية لبناء الروبوتات وتطبيقات الذكاء الاصطناعي للشباب والناشئة."
            ),
            Product(
                id = 6,
                title = "عطر مجرة الملكي Dark Nebula VIP",
                price = 1200.0,
                category = "عطور فاخرة",
                imageUrl = "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=600&auto=format&fit=crop",
                rating = 5.0f,
                isTeenFriendly = false,
                isFeatured = false,
                description = "عطر فاخر بخلاصة العود النادر والمسك الأسود مخصص للفئات البالغة فقط."
            )
        )
        dao.insertProducts(sampleProducts)

        // Seed Sample Posts
        val samplePosts = listOf(
            Post(
                id = 1,
                authorName = "فيصل العتيبي",
                content = "أطلقت اليوم أول مجتمع برمجيات ذكية على منصة مجرة! شاركونا أفكاركم حول تجربة الدردشة المشفرة الجديدة.",
                likesCount = 142,
                commentsCount = 28,
                isLiked = true,
                taggedProductId = 1,
                isTeenSafe = true,
                isAuthorVerified = true
            ),
            Post(
                id = 2,
                authorName = "ريم الشمري",
                content = "سماعات Hologram Sound رهيبة جداً! جودة الصوت والنقاء غير طبيعية مع وضع الناشئة",
                likesCount = 89,
                commentsCount = 12,
                isLiked = false,
                taggedProductId = 3,
                isTeenSafe = true,
                isAuthorVerified = false
            ),
            Post(
                id = 3,
                authorName = "أكاديمية المستقبل للذكاء الاصطناعي",
                content = "ورشة عمل تفاعلية جديدة للناشئة والأجيال الواعدة في تصميم واجهات Glassmorphism وتطبيقات أندرويد الحديثة. انضموا إلينا!",
                likesCount = 310,
                commentsCount = 45,
                isLiked = true,
                isTeenSafe = true,
                isAuthorVerified = true
            )
        )
        dao.insertPosts(samplePosts)

        // Seed Conversations & Chat Messages
        val now = System.currentTimeMillis()
        val sampleConversations = listOf(
            Conversation(
                id = "ai_bot",
                contactName = "ذكاء NEXA AI",
                contactAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150&auto=format&fit=crop",
                lastMessage = "أهلاً بك! أنا مساعدك الذكي المدعوم بـ Gemini 3.5 Flash. كيف يمكنني مساعدتك اليوم؟",
                lastTimestamp = now,
                unreadCount = 1,
                isPinRequired = false,
                isContactVerified = true
            ),
            Conversation(
                id = "conv_1",
                contactName = "نورا القحطاني",
                contactAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
                lastMessage = "هل استلمت حقيبة البرمجة الذكية اليوم؟",
                lastTimestamp = now - (1000 * 60 * 5),
                unreadCount = 2,
                isPinRequired = false,
                isContactVerified = true
            ),
            Conversation(
                id = "conv_2",
                contactName = "فيصل العتيبي",
                contactAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop",
                lastMessage = "🎙️ تسجيل صوتي (0:24)",
                lastTimestamp = now - (1000 * 60 * 45),
                unreadCount = 1,
                isPinRequired = false,
                isContactVerified = true
            ),
            Conversation(
                id = "conv_3",
                contactName = "سارة النمر",
                contactAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop",
                lastMessage = "📷 صورة مرفقة: تصميم النيون الجديد مذهل!",
                lastTimestamp = now - (1000 * 60 * 120),
                unreadCount = 0,
                isPinRequired = false,
                isContactVerified = true
            ),
            Conversation(
                id = "conv_4",
                contactName = "عبدالله الشهري",
                contactAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop",
                lastMessage = "ما رأيك في تجربة التسوق الجديدة بالألوان النيون؟",
                lastTimestamp = now - (1000 * 60 * 360),
                unreadCount = 0,
                isPinRequired = false,
                isContactVerified = false
            ),
            Conversation(
                id = "conv_req_1",
                contactName = "خالد الدوسري",
                contactAvatar = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150&auto=format&fit=crop",
                lastMessage = "السلام عليكم، أود الاستفسار عن كود البرمجة الذكي الذي قمت بمشاركته؟",
                lastTimestamp = now - (1000 * 60 * 30),
                unreadCount = 1,
                isPinRequired = false,
                isMessageRequest = true,
                requestStatus = "pending",
                targetUserId = "user_207",
                isContactVerified = false
            ),
            Conversation(
                id = "conv_5",
                contactName = "فريق دعم مجرة الذكي",
                contactAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150&auto=format&fit=crop",
                lastMessage = "تم تفعيل التشفير التام 256-bit لحسابك بنجاح.",
                lastTimestamp = now - (1000 * 60 * 60 * 24),
                unreadCount = 0,
                isPinRequired = false,
                isMessageRequest = false,
                requestStatus = "accepted",
                isContactVerified = true
            )
        )
        dao.insertConversations(sampleConversations)

        val sampleMessagesReq = listOf(
            ChatMessage(
                id = 201,
                conversationId = "conv_req_1",
                senderName = "خالد الدوسري",
                senderAvatar = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150&auto=format&fit=crop",
                text = "السلام عليكم، أود الاستفسار عن كود البرمجة الذكي الذي قمت بمشاركته في المجتمع؟",
                timestamp = now - (1000 * 60 * 30),
                isFromUser = false,
                isEncrypted = true,
                mediaType = "text",
                deliveryStatus = "delivered",
                isRead = false
            )
        )
        dao.insertMessages(sampleMessagesReq)

        val sampleMessagesAi = listOf(
            ChatMessage(
                id = 100,
                conversationId = "ai_bot",
                senderName = "ذكاء NEXA AI",
                senderAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150&auto=format&fit=crop",
                text = "مرحباً بك! أنا مساعدك الذكي المتقدم المدعوم بنموذج Gemini 3.5 Flash ⚡. يمكنك سؤالي عن أي شيء، تحليل الصور، استكشاف المنتجات، أو الترجمة والمحادثة الحية!",
                timestamp = now - (1000 * 60 * 10),
                isFromUser = false,
                isEncrypted = true,
                mediaType = "text",
                deliveryStatus = "read",
                isRead = true
            )
        )
        dao.insertMessages(sampleMessagesAi)

        val sampleMessagesConv1 = listOf(
            ChatMessage(
                id = 1,
                conversationId = "conv_1",
                senderName = "نورا القحطاني",
                senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
                text = "مرحباً! كيف حالك يا صديقي؟",
                timestamp = now - (1000 * 60 * 30),
                isFromUser = false,
                isEncrypted = true,
                mediaType = "text"
            ),
            ChatMessage(
                id = 2,
                conversationId = "conv_1",
                senderName = "سارة النمر",
                senderAvatar = "",
                text = "أهلاً نورا! كل شيء رائع، استكشفت مزايا التشفير والمحادثات الجديدة في تطبيق NEXA.",
                timestamp = now - (1000 * 60 * 25),
                isFromUser = true,
                isEncrypted = true,
                mediaType = "text"
            ),
            ChatMessage(
                id = 3,
                conversationId = "conv_1",
                senderName = "نورا القحطاني",
                senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
                text = "هل استلمت حقيبة البرمجة الذكية اليوم؟",
                timestamp = now - (1000 * 60 * 5),
                isFromUser = false,
                isEncrypted = true,
                mediaType = "text"
            )
        )
        dao.insertMessages(sampleMessagesConv1)

        val sampleMessagesConv2 = listOf(
            ChatMessage(
                id = 4,
                conversationId = "conv_2",
                senderName = "فيصل العتيبي",
                senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop",
                text = "السلام عليكم، هل جربت ميزة المكالمات الصوتية والمحادثة المشفرة؟",
                timestamp = now - (1000 * 60 * 60),
                isFromUser = false,
                isEncrypted = true,
                mediaType = "text"
            ),
            ChatMessage(
                id = 5,
                conversationId = "conv_2",
                senderName = "فيصل العتيبي",
                senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop",
                text = "استمع للتسجيل الصوتي المرفق حول مشروعنا القادم!",
                timestamp = now - (1000 * 60 * 45),
                isFromUser = false,
                isEncrypted = true,
                mediaType = "voice"
            )
        )
        dao.insertMessages(sampleMessagesConv2)

        val sampleMessagesConv3 = listOf(
            ChatMessage(
                id = 6,
                conversationId = "conv_3",
                senderName = "سارة النمر",
                senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop",
                text = "تصميم النيون الجديد مذهل!",
                timestamp = now - (1000 * 60 * 120),
                isFromUser = false,
                isEncrypted = true,
                mediaType = "image",
                mediaUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop"
            )
        )
        dao.insertMessages(sampleMessagesConv3)
    }
}
