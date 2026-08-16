package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.StoryItem
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GamificationRewardsModal
import com.example.ui.components.GlassCard
import com.example.ui.components.InAppNotificationToast
import com.example.ui.components.NexaVoiceCompanionModal
import com.example.ui.components.ReelStoryViewerModal
import com.example.ui.components.SmartNotificationCenterModal
import com.example.ui.components.StoryCreatorModal
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun HomeScreen(
    viewModel: MajarrahViewModel? = null,
    storiesList: List<StoryItem> = emptyList(),
    onStoryClick: (StoryItem) -> Unit = {},
    onCreateStoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    onOpenServicesMenu: () -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateToReels: (String) -> Unit = {}
) {
    // ViewModel state bindings if provided
    val vmStories by (viewModel?.stories?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val vmPosts by (viewModel?.posts?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val isDarkTheme by (viewModel?.isDarkTheme?.collectAsState() ?: remember { mutableStateOf(true) })
    
    // Notifications & Gamification States
    val notifications by (viewModel?.notifications?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val unreadNotifCount by (viewModel?.unreadNotificationsCount?.collectAsState() ?: remember { mutableStateOf(0) })
    val activeToast by (viewModel?.activeInAppToast?.collectAsState() ?: remember { mutableStateOf(null) })
    
    val userLevel by (viewModel?.userLevel?.collectAsState() ?: remember { mutableStateOf(6) })
    val currentExp by (viewModel?.currentExp?.collectAsState() ?: remember { mutableStateOf(720) })
    val nextLevelExp by (viewModel?.nextLevelExp?.collectAsState() ?: remember { mutableStateOf(1000) })
    val streakDays by (viewModel?.streakDays?.collectAsState() ?: remember { mutableStateOf(6) })
    val badges by (viewModel?.gamificationBadges?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val dailyQuests by (viewModel?.dailyQuests?.collectAsState() ?: remember { mutableStateOf(emptyList()) })

    // Use storiesList if provided, otherwise fallback to viewModel stories
    val displayStories = if (storiesList.isNotEmpty()) storiesList else vmStories

    // Modals
    var showStoryCreatorModal by remember { mutableStateOf(false) }
    var activeStoryForViewer by remember { mutableStateOf<StoryItem?>(null) }
    var showNotificationsModal by remember { mutableStateOf(false) }
    var showVoiceCompanionModal by remember { mutableStateOf(false) }
    var showRewardsModal by remember { mutableStateOf(false) }

    // Pulsing Mic Animation for Voice Companion
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micAuraScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micAura"
    )

    if (showStoryCreatorModal) {
        StoryCreatorModal(
            onDismiss = { showStoryCreatorModal = false },
            onPublishStory = { story ->
                if (viewModel != null) {
                    viewModel.publishStory(story)
                }
                showStoryCreatorModal = false
            }
        )
    }

    if (showNotificationsModal) {
        SmartNotificationCenterModal(
            notifications = notifications,
            onDismiss = { showNotificationsModal = false },
            onMarkAsRead = { id -> viewModel?.markNotificationAsRead(id) },
            onMarkAllAsRead = { viewModel?.markAllNotificationsAsRead() },
            onDeleteNotification = { id -> viewModel?.deleteNotification(id) },
            onClearAll = { viewModel?.clearAllNotifications() },
            onClaimRewardExp = { id, exp -> viewModel?.claimNotificationExp(id, exp) },
            onGenerateAiTipNotification = { viewModel?.triggerSmartAiTipNotification() },
            onActionClick = { route ->
                when (route) {
                    "voice" -> showVoiceCompanionModal = true
                    "rewards" -> showRewardsModal = true
                    "chat" -> onOpenChat()
                    "reels" -> onNavigateToReels("reels")
                }
            }
        )
    }

    if (showVoiceCompanionModal) {
        NexaVoiceCompanionModal(
            onDismiss = { showVoiceCompanionModal = false },
            onVoiceInteractionSuccess = { exp ->
                viewModel?.handleVoiceInteractionSuccess(exp)
            },
            onNavigateToReels = { onNavigateToReels("reels") },
            onOpenChat = onOpenChat,
            onOpenVault = onOpenServicesMenu,
            onSearchTopic = {}
        )
    }

    if (showRewardsModal) {
        GamificationRewardsModal(
            userLevel = userLevel,
            currentExp = currentExp,
            nextLevelExp = nextLevelExp,
            streakDays = streakDays,
            badges = badges,
            dailyQuests = dailyQuests,
            onDismiss = { showRewardsModal = false },
            onClaimQuestReward = { quest -> viewModel?.claimQuestReward(quest) },
            onClaimBadgeReward = { badge -> viewModel?.claimBadgeReward(badge) },
            onPerformQuestAction = { key ->
                when (key) {
                    "voice" -> showVoiceCompanionModal = true
                    "reels" -> onNavigateToReels("reels")
                    "services" -> onOpenServicesMenu()
                    "chat" -> onOpenChat()
                }
            }
        )
    }

    activeStoryForViewer?.let { story ->
        ReelStoryViewerModal(
            story = story,
            onLikeToggle = {
                viewModel?.toggleStoryLike(story.id)
            },
            onSendReply = { text ->
                viewModel?.sendStoryReply(story.id, text)
            },
            onShareForward = {},
            onNavigateToReels = {
                onNavigateToReels("reels")
                activeStoryForViewer = null
            },
            onDismiss = { activeStoryForViewer = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // In-App Notification Toast Slide Down
            InAppNotificationToast(
                notification = activeToast,
                onDismiss = { viewModel?.dismissInAppToast() },
                onClick = {
                    activeToast?.actionRoute?.let { route ->
                        when (route) {
                            "voice" -> showVoiceCompanionModal = true
                            "rewards" -> showRewardsModal = true
                            "chat" -> onOpenChat()
                            "reels" -> onNavigateToReels("reels")
                        }
                    }
                    viewModel?.dismissInAppToast()
                }
            )

        // TOP HEADER BAR (NEXA Logo, Level Chip, Voice Mic, Theme Toggle, Notification Bell, Chat)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "NEXA",
                    color = NeonCyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                // Level / EXP Gamification Quick Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonAmber.copy(alpha = 0.18f))
                        .border(1.dp, NeonAmber.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable { showRewardsModal = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Lv.$userLevel",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Voice Companion Quick Button with Animated Sound Wave aura
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .scale(micAuraScale)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f))
                    )
                    IconButton(
                        onClick = { showVoiceCompanionModal = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Voice Companion",
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Dark/Light Theme Toggle
                IconButton(
                    onClick = { viewModel?.toggleTheme() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "تبديل المظهر",
                        tint = if (isDarkTheme) NeonAmber else NeonPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Smart Notifications Bell with Unread Badge
                IconButton(
                    onClick = { showNotificationsModal = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(
                                    containerColor = NeonPink,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "الإشعارات الذكية",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Instagram Direct Paper Plane Chat Icon
                IconButton(
                    onClick = { onOpenChat() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "الرسائل المباشرة",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer(rotationZ = -22f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. CIRCULAR STORIES TRAY (دوائر القصص أعلى الشاشة)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Add Story Button
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onCreateStoryClick()
                                showStoryCreatorModal = true
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.5.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Story",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "قصتك",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // User Stories List (Circular Avatars)
                    items(displayStories) { story ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                onStoryClick(story)
                                activeStoryForViewer = story
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.3f))
                                    .border(2.dp, NeonPink, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = story.authorName.take(1).ifEmpty { "م" }.uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = story.authorName.split(" ").firstOrNull() ?: story.authorName,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            // 2. DAILY QUESTS & VOICE COMPANION QUICK BANNER
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRewardsModal = true },
                        shape = RoundedCornerShape(20.dp),
                        borderColor = NeonCyan.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan.copy(alpha = 0.2f))
                                        .border(1.dp, NeonCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "المستوى $userLevel • $currentExp/$nextLevelExp EXP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "أكمل مهامك اليومية واحصل على شارات النيون",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonCyan)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "استعراض",
                                    color = BackgroundDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. CLEAN FEED (خلاصة المنشورات بدون إعلانات أو كروت مزعجة)
            if (vmPosts.isNotEmpty()) {
                items(vmPosts) { post ->
                    PostCardItem(
                        post = post,
                        onLikeClick = { viewModel?.toggleLike(post) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else if (displayStories.isNotEmpty()) {
                items(displayStories) { storyPost ->
                    PostCardItemFromStory(story = storyPost)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        } // LazyColumn

        // Glowing Floating NEXA AI Assistant Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NeonCyan, NeonPurple, NeonPink)
                        )
                    )
                    .clickable {
                        viewModel?.selectConversation("nexa_ai")
                        onOpenChat()
                    }
                    .padding(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .background(BackgroundDark.copy(alpha = 0.92f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "NEXA AI",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "ذكاء NEXA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
        } // Column
    } // Box
}

@Composable
fun PostCardItem(
    post: Post,
    onLikeClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(14.dp)
    ) {
        // Author Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NeonPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.authorName.take(1).ifEmpty { "م" }.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = post.authorName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "منذ قليل",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Post Text / Content
        Text(
            text = post.content,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Post Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = NeonPink,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${post.likesCount}", color = Color.Gray, fontSize = 12.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Comment",
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${post.commentsCount} تعليق", color = Color.Gray, fontSize = 12.sp)
            }

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun PostCardItemFromStory(story: StoryItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(14.dp)
    ) {
        // Author Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NeonPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = story.authorName.take(1).ifEmpty { "م" }.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = story.authorName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = story.timestamp,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Post Text / Content
        Text(
            text = story.text.ifEmpty { story.reelTitle ?: "محتوى حصري" },
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Post Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = NeonPink,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${story.storyLikes}", color = Color.Gray, fontSize = 12.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Comment",
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "تعليق", color = Color.Gray, fontSize = 12.sp)
            }

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
