package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.data.model.StoryItem
import com.example.ui.MajarrahViewModel
import com.example.ui.components.BlueVerificationBadge
import com.example.ui.components.CreatorAvatarWithAura
import com.example.ui.components.FloatingReactionsPicker
import com.example.ui.components.GamificationRewardsModal
import com.example.ui.components.GlassCard
import com.example.ui.components.InAppNotificationToast
import com.example.ui.components.NexaVoiceCompanionModal
import com.example.ui.components.PostCommentsModal
import com.example.ui.components.PostShareModal
import com.example.ui.components.ReelStoryViewerModal
import com.example.ui.components.SmartNotificationCenterModal
import com.example.ui.components.StoryCreatorModal
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
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
    val haptic = LocalHapticFeedback.current

    // ViewModel state bindings
    val vmStories by (viewModel?.stories?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val vmPosts by (viewModel?.posts?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val isDarkTheme by (viewModel?.isDarkTheme?.collectAsState() ?: remember { mutableStateOf(true) })
    val viewedStoryIds by (viewModel?.viewedStoryIds?.collectAsState() ?: remember { mutableStateOf(emptySet()) })
    val userProfile by (viewModel?.userProfile?.collectAsState() ?: remember { mutableStateOf(null) })

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

    val displayStories = if (storiesList.isNotEmpty()) storiesList else vmStories

    // Modals and Active Interaction States
    var showStoryCreatorModal by remember { mutableStateOf(false) }
    var activeStoryForViewer by remember { mutableStateOf<StoryItem?>(null) }
    var showNotificationsModal by remember { mutableStateOf(false) }
    var showVoiceCompanionModal by remember { mutableStateOf(false) }
    var showRewardsModal by remember { mutableStateOf(false) }
    var activePostForComments by remember { mutableStateOf<Post?>(null) }
    var activePostForShare by remember { mutableStateOf<Post?>(null) }

    // Quick Post Creation State
    var isCreatePostExpanded by remember { mutableStateOf(false) }
    var newPostContent by remember { mutableStateOf("") }
    var selectedPostMediaType by remember { mutableStateOf("text") }
    var attachedImageUrl by remember { mutableStateOf<String?>(null) }
    var attachedVideoUrl by remember { mutableStateOf<String?>(null) }

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

    // 1. Story Creator Modal
    if (showStoryCreatorModal) {
        StoryCreatorModal(
            onDismiss = { showStoryCreatorModal = false },
            onPublishStory = { story ->
                viewModel?.publishStory(story)
                showStoryCreatorModal = false
            }
        )
    }

    // 2. Story Viewer Modal
    activeStoryForViewer?.let { story ->
        ReelStoryViewerModal(
            story = story,
            onLikeToggle = {
                viewModel?.toggleStoryLike(story.id)
            },
            onSendReply = { text ->
                viewModel?.sendStoryReply(story.id, text)
            },
            onShareForward = {
                viewModel?.sharePostToStory(
                    Post(
                        id = 0,
                        authorName = story.authorName,
                        content = story.text
                    )
                )
            },
            onNavigateToReels = {
                onNavigateToReels("reels")
                activeStoryForViewer = null
            },
            onDismiss = { activeStoryForViewer = null }
        )
    }

    // 3. Post Comments Modal
    activePostForComments?.let { post ->
        if (viewModel != null) {
            PostCommentsModal(
                post = post,
                viewModel = viewModel,
                onDismiss = { activePostForComments = null },
                onReplyWithStory = { author, commentText ->
                    activePostForComments = null
                    showStoryCreatorModal = true
                }
            )
        }
    }

    // 4. Post Share Modal
    activePostForShare?.let { post ->
        if (viewModel != null) {
            PostShareModal(
                post = post,
                viewModel = viewModel,
                onDismiss = { activePostForShare = null }
            )
        }
    }

    // 5. Notifications Modal
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

    // 6. Voice Companion Modal
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

    // 7. Gamification Rewards Modal
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    .padding(horizontal = 14.dp, vertical = 8.dp),
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
                    // Voice Companion Quick Button
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .scale(micAuraScale)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f))
                        )
                        IconButton(
                            onClick = { showVoiceCompanionModal = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Assistant",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Theme Mode Toggle
                    IconButton(
                        onClick = { viewModel?.toggleTheme() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = NeonAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Notification Bell with Unread Badge
                    IconButton(
                        onClick = { showNotificationsModal = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(
                                        containerColor = NeonPink,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$unreadNotifCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (unreadNotifCount > 0) NeonPink else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Direct Chat Messages Shortcut
                    IconButton(
                        onClick = onOpenChat,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.25f))
                            .border(1.dp, NeonPurple, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Direct Messages",
                            tint = NeonPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // FEED SCROLL VIEW
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. 24-HOUR HORIZONTAL STORIES TRAY
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // "قصتك" Add/View My Story Circle
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
                                        .size(66.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(2.dp, NeonCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = userProfile?.name?.take(1) ?: "أ",
                                            color = NeonCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                    // Plus Badge Icon
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan)
                                            .border(2.dp, BackgroundDark, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Create Story",
                                            tint = BackgroundDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "قصتك 24س",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Creators 24-Hour Stories List
                        items(displayStories) { story ->
                            val isViewed = viewedStoryIds.contains(story.id)
                            val storyBorder = if (isViewed) {
                                Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.35f), NeonCyan.copy(alpha = 0.2f)))
                            } else {
                                Brush.sweepGradient(listOf(NeonPink, NeonPurple, NeonCyan, NeonPink))
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel?.markStoryAsViewed(story.id)
                                    onStoryClick(story)
                                    activeStoryForViewer = story
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(66.dp)
                                        .clip(CircleShape)
                                        .background(storyBorder)
                                        .padding(2.5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(BackgroundDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = story.authorName.take(1).ifEmpty { "م" }.uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = story.authorName.split(" ").firstOrNull() ?: story.authorName,
                                    color = if (isViewed) Color.Gray else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = if (isViewed) FontWeight.Normal else FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 2. QUICK POST CREATION BOX ("ما الذي يدور في ذهنك؟")
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = userProfile?.name?.take(1) ?: "أ",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                OutlinedTextField(
                                    value = newPostContent,
                                    onValueChange = {
                                        newPostContent = it
                                        isCreatePostExpanded = true
                                    },
                                    placeholder = {
                                        Text(
                                            text = "ما الذي يدور في ذهنك؟ شارك مجتمع مجرة...",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    maxLines = if (isCreatePostExpanded) 4 else 1
                                )
                            }

                            // Attached Media Preview if any
                            if (attachedImageUrl != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = attachedImageUrl,
                                        contentDescription = "Attached Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { attachedImageUrl = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.7f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action Pills Row: Photo, Video, Story, AI writing
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Photo Action
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NeonCyan.copy(alpha = 0.12f),
                                        modifier = Modifier.clickable {
                                            attachedImageUrl = "https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=800&auto=format&fit=crop"
                                            attachedVideoUrl = null
                                            selectedPostMediaType = "image"
                                            isCreatePostExpanded = true
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = null,
                                                tint = NeonCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(text = "صورة", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Video Action
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NeonPink.copy(alpha = 0.12f),
                                        modifier = Modifier.clickable {
                                            attachedVideoUrl = "https://assets.mixkit.co/videos/preview/mixkit-futuristic-technology-interaction-animation-42512-large.mp4"
                                            attachedImageUrl = null
                                            selectedPostMediaType = "video"
                                            isCreatePostExpanded = true
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Videocam,
                                                contentDescription = null,
                                                tint = NeonPink,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(text = "فيديو", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // AI Writing Assistant
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NeonPurple.copy(alpha = 0.12f),
                                        modifier = Modifier.clickable {
                                            newPostContent = "مرحباً بكم في شبكة NEXA الاجتماعية! تجربة فائقة الأمان مع تشفير E2EE ومقاطع ريلز المستقبلية ✨🚀 #مجرة #تقنية"
                                            isCreatePostExpanded = true
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = NeonPurple,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(text = "ذكاء NEXA", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Publish Button
                                if (newPostContent.isNotBlank() || attachedImageUrl != null || attachedVideoUrl != null) {
                                    Button(
                                        onClick = {
                                            viewModel?.createPost(
                                                content = newPostContent,
                                                imageUrl = attachedImageUrl,
                                                videoUrl = attachedVideoUrl,
                                                mediaType = selectedPostMediaType
                                            )
                                            newPostContent = ""
                                            attachedImageUrl = null
                                            attachedVideoUrl = null
                                            isCreatePostExpanded = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "نشر",
                                            color = BackgroundDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. DAILY QUESTS & GAMIFICATION BANNER
                item {
                    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRewardsModal = true },
                            shape = RoundedCornerShape(18.dp),
                            borderColor = NeonCyan.copy(alpha = 0.35f)
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
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan.copy(alpha = 0.2f))
                                            .border(1.dp, NeonCyan, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
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
                                            text = "أكمل مهامك اليومية واكسب شارات النيون الحصرية",
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
                                        text = "المكافآت",
                                        color = BackgroundDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. INTERACTIVE TIMELINE / POST FEED
                if (vmPosts.isNotEmpty()) {
                    items(vmPosts) { post ->
                        PostCardItem(
                            post = post,
                            onLikeClick = { viewModel?.toggleLike(post) },
                            onReactionSelected = { emoji -> viewModel?.reactToPost(post, emoji) },
                            onCommentClick = { activePostForComments = post },
                            onShareClick = { activePostForShare = post },
                            onBuyProductClick = { productId -> onNavigateToProduct("$productId") }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            } // LazyColumn
        } // Column

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
    } // Box
}

@Composable
fun PostCardItem(
    post: Post,
    onLikeClick: () -> Unit = {},
    onReactionSelected: (String) -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onBuyProductClick: (Int) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var showReactionsPicker by remember { mutableStateOf(false) }

    val currentReaction = post.userReaction ?: if (post.isLiked) "❤️" else null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // 1. Author Row & Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonPurple, NeonPink)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1).ifEmpty { "م" }.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (post.isAuthorVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            BlueVerificationBadge(size = 15.dp)
                        }
                    }
                    Text(
                        text = "منذ قليل • منصة NEXA المشفرة",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }

            // More / Tag Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(EncryptedGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "آمن 🛡️",
                    color = EncryptedGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Post Content Text
        Text(
            text = post.content,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        // 3. Rich Media Render (Image or Video)
        if (post.imageUrl != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else if (post.videoUrl != null || post.mediaType == "video") {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E1035), Color(0xFF0F081C))
                        )
                    )
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Video Play Overlay
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.5.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = NeonCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Video Duration Chip
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = post.videoDuration ?: "0:30",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 4. Tagged Product Banner if any
        if (post.taggedProductName != null && post.taggedProductId != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBuyProductClick(post.taggedProductId) },
                shape = RoundedCornerShape(14.dp),
                color = NeonCyan.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = post.taggedProductName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${post.taggedProductPrice ?: 0.0} ر.س",
                                color = NeonAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "تسوق الآن",
                            color = BackgroundDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Floating Reactions Overlay if triggered
        AnimatedVisibility(
            visible = showReactionsPicker,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                FloatingReactionsPicker(
                    onSelectReaction = { emoji ->
                        onReactionSelected(emoji)
                        showReactionsPicker = false
                    },
                    onDismiss = { showReactionsPicker = false }
                )
            }
        }

        // 6. Action Buttons Bar (Animated Reactions, Comments, Share)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reaction / Like Pill with long-press support
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (currentReaction != null) NeonPink.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (currentReaction != null) NeonPink.copy(alpha = 0.5f) else Color.Transparent
                ),
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (currentReaction != null) {
                                onLikeClick()
                            } else {
                                onReactionSelected("❤️")
                            }
                        },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showReactionsPicker = true
                        }
                    )
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (currentReaction != null) {
                        Text(text = currentReaction, fontSize = 16.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "${post.likesCount}",
                        color = if (currentReaction != null) NeonPink else Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Comments Button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.clickable { onCommentClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = NeonCyan,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = "${post.commentsCount} تعليق",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Share Button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.clickable { onShareClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = NeonPurple,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = "${post.sharesCount}",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
