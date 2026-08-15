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

    suspend fun sendMessage(message: ChatMessage) {
        dao.insertMessage(message)
        FirebaseManager.saveMessageToCloud(message)
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
            followersCount = 1_250_000, // 1.25M Followers to show VIP Diamond Aura
            totalViewsCount = 1_450_000L, // 1.45M Views to unlock Creator Fund
            points = 890
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
                isTeenFriendly = false, // Restricted for adults in Adult Mode
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
 content ="أطلقت اليوم أول مجتمع برمجيات ذكية على منصة مجرة! شاركونا أفكاركم حول تجربة الدردشة المشفرة الجديدة.",
                likesCount = 142,
                commentsCount = 28,
                isLiked = true,
                taggedProductId = 1,
                isTeenSafe = true
            ),
            Post(
                id = 2,
                authorName = "ريم الشمري",
 content ="سماعات Hologram Sound رهيبة جداً! جودة الصوت والنقاء غير طبيعية مع وضع الناشئة",
                likesCount = 89,
                commentsCount = 12,
                isLiked = false,
                taggedProductId = 3,
                isTeenSafe = true
            ),
            Post(
                id = 3,
                authorName = "أكاديمية المستقبل للذكاء الاصطناعي",
                content = "ورشة عمل تفاعلية جديدة للناشئة والأجيال الواعدة في تصميم واجهات Glassmorphism وتطبيقات أندرويد الحديثة. انضموا إلينا!",
                likesCount = 310,
                commentsCount = 45,
                isLiked = true,
                isTeenSafe = true
            )
        )
        dao.insertPosts(samplePosts)

        // Seed Conversations & Chat Messages
        val sampleConversations = listOf(
            Conversation(
                id = "conv_1",
                contactName = "نورا القحطاني",
 lastMessage =" رسالة مشفرة بـ PIN: هل استلمت حقيبة البرمجة؟",
                unreadCount = 2,
                isPinRequired = false
            ),
            Conversation(
                id = "conv_2",
                contactName = "فريق دعم مجرة الذكي",
                lastMessage = "تم تفعيل وضع الناشئة تلقائياً لحسابك لحماية المحتوى.",
                unreadCount = 0,
                isPinRequired = false
            ),
            Conversation(
                id = "conv_3",
                contactName = "عبدالله الشهري",
                lastMessage = "ما رأيك في تجربة التسوق الجديدة بالألوان النيون؟",
                unreadCount = 1,
                isPinRequired = false
            )
        )
        dao.insertConversations(sampleConversations)

        val sampleMessagesConv1 = listOf(
            ChatMessage(
                id = 1,
                conversationId = "conv_1",
                senderName = "نورا القحطاني",
 text ="مرحباً! محادثتنا محمية ومجهزة بنظام التشفير الخاص برمز الـ PIN",
                isFromUser = false,
                isEncrypted = true,
                mediaType = "text"
            ),
            ChatMessage(
                id = 2,
                conversationId = "conv_1",
                senderName = "سارة النمر",
                text = "أهلاً نورا! هذا ممتاز، خصوصية كاملة وسرعة فائقة.",
                isFromUser = true,
                isEncrypted = true,
                mediaType = "text"
            ),
            ChatMessage(
                id = 3,
                conversationId = "conv_1",
                senderName = "نورا القحطاني",
                text = "لقد قمت بتجربة الشراء من المتجر اليوم بالتصميم الزجاجي المذهل!",
                isFromUser = false,
                isEncrypted = true,
                mediaType = "text"
            )
        )
        dao.insertMessages(sampleMessagesConv1)
    }
}
