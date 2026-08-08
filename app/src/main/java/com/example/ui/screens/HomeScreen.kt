package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import com.example.ui.components.NexaVoiceAssistantModal
import com.example.ui.components.NexaPrivateVaultModal
import com.example.ui.components.QuickActionRadialOverlay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Bubble3D
import com.example.data.model.Post
import com.example.ui.MajarrahViewModel
import com.example.ui.components.Bubble3DOrbit
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Toll
import com.example.ui.components.AdMobBannerSpace
import com.example.ui.components.DailyRewardsAndReferralModal
import com.example.ui.components.InAppCreditsTopUpModal
import com.example.ui.components.NexaVipSubscriptionModal

@Composable
fun HomeScreen(
    viewModel: MajarrahViewModel,
    onOpenChat: () -> Unit,
    onOpenServicesMenu: () -> Unit,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToReels: () -> Unit = {}
) {
    val profile by viewModel.userProfile.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val bubbles by viewModel.bubbles.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val reportedContentIds by viewModel.reportedContentIds.collectAsState()

    val isTeenMode = profile?.isTeenMode ?: true
    val filteredPosts = remember(posts, blockedUsers, reportedContentIds, isTeenMode) {
        posts.filter { post ->
            (!isTeenMode || post.isTeenSafe) &&
            !blockedUsers.contains(post.authorName) &&
            !reportedContentIds.contains("post_${post.id}") &&
            !reportedContentIds.contains(post.id.toString())
        }
    }

    var activeStoryForViewer by remember { mutableStateOf<com.example.data.model.StoryItem?>(null) }

    activeStoryForViewer?.let { story ->
        com.example.ui.components.ReelStoryViewerModal(
            story = story,
            onLikeToggle = {
                viewModel.toggleStoryLike(story.id)
            },
            onSendReply = { text ->
                viewModel.sendStoryReply(story.id, text)
            },
            onShareForward = {
                viewModel.publishReelToStory(
                    reelTitle = story.reelTitle ?: "مقطع نيون مميز",
                    reelAuthor = story.reelAuthor ?: "@majarrah_official",
 userCaption ="بارتاج ستوري إلى أصدقائك"
                )
            },
            onNavigateToReels = {
                activeStoryForViewer = null
                onNavigateToReels()
            },
            onDismiss = { activeStoryForViewer = null }
        )
    }

    val isVipMember = profile?.isVipMember ?: false
    val creditsBalance = profile?.creditsBalance ?: 850
    val claimedDailyRewardDays = profile?.claimedDailyRewardDays ?: 3
    val referralCode = profile?.referralCode ?: "NEXA-8821"

    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adWatchProgress by viewModel.adWatchProgress.collectAsState()
    val monetizationMessage by viewModel.monetizationMessage.collectAsState()

    val isPostingRestricted by viewModel.isPostingRestricted.collectAsState()
    val postingRestrictionMessage by viewModel.postingRestrictionMessage.collectAsState()

    val isDataSaverEnabled by viewModel.isDataSaverEnabled.collectAsState()
    val isSocialPass6Active by viewModel.isSocialPass6Active.collectAsState()

    val activeScreenTimeSeconds by viewModel.activeScreenTimeSeconds.collectAsState()

    var selectedSecondaryTab by remember { mutableStateOf(0) }

    var showVipModal by remember { mutableStateOf(false) }
    var showCreditsModal by remember { mutableStateOf(false) }
    var showDailyRewardsModal by remember { mutableStateOf(false) }

    var newPostText by remember { mutableStateOf("") }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    var showStoryCreatorModal by remember { mutableStateOf(false) }
    var activeReplyAuthor by remember { mutableStateOf<String?>(null) }
    var activeReplyText by remember { mutableStateOf<String?>(null) }

    var activeCommentsPost by remember { mutableStateOf<Post?>(null) }
    var activeReportPost by remember { mutableStateOf<Post?>(null) }

    var showVoiceAssistantModal by remember { mutableStateOf(false) }
    var showPrivateVaultModal by remember { mutableStateOf(false) }
    var activeQuickActionOverlayPost by remember { mutableStateOf<Post?>(null) }

    activeReportPost?.let { postToReport ->
        com.example.ui.components.ReportAndBlockDialog(
            targetAuthorName = postToReport.authorName,
            contentId = "post_${postToReport.id}",
            contentTypeTitle = "المنشور (${postToReport.content.take(20)}...)",
            onReport = { reason ->
                viewModel.reportContent("post_${postToReport.id}", reason)
            },
            onReportWithAi = { category, onFinished ->
                viewModel.submitReportWithAiModeration(
                    targetAuthorName = postToReport.authorName,
                    contentId = "post_${postToReport.id}",
                    contentTypeTitle = "المنشور (${postToReport.content.take(20)}...)",
                    category = category,
                    contentText = postToReport.content,
                    onCompleted = onFinished
                )
            },
            onBlock = {
                viewModel.blockUser(postToReport.authorName)
            },
            onDismiss = { activeReportPost = null }
        )
    }

    if (showVipModal) {
        NexaVipSubscriptionModal(
            isCurrentlyVip = isVipMember,
            onSubscribe = { tier -> viewModel.activateVipSubscription(tier) },
            onDismiss = { showVipModal = false }
        )
    }

    if (showCreditsModal) {
        InAppCreditsTopUpModal(
            creditsBalance = creditsBalance,
            isAdWatching = isAdWatching,
            adWatchProgress = adWatchProgress,
            onTopUp = { amount, price -> viewModel.topUpCredits(amount, price) },
            onWatchAd = { viewModel.watchRewardedAdForCredits() },
            onDismiss = { showCreditsModal = false }
        )
    }

    if (showDailyRewardsModal) {
        DailyRewardsAndReferralModal(
            claimedStreakDays = claimedDailyRewardDays,
            referralCode = referralCode,
            onClaimDaily = { viewModel.claimDailyReward() },
            onApplyReferral = { code -> viewModel.applyReferralCode(code) },
            onDismiss = { showDailyRewardsModal = false }
        )
    }


    activeCommentsPost?.let { activePost ->
        com.example.ui.components.PostCommentsModal(
            post = activePost,
            viewModel = viewModel,
            onDismiss = { activeCommentsPost = null },
            onReplyWithStory = { author, commentText ->
                activeReplyAuthor = author
                activeReplyText = commentText
                showStoryCreatorModal = true
                activeCommentsPost = null
            }
        )
    }

    if (showStoryCreatorModal) {
        com.example.ui.components.StoryCreatorModal(
            initialReplyAuthor = activeReplyAuthor,
            initialReplyText = activeReplyText,
            onDismiss = {
                showStoryCreatorModal = false
                activeReplyAuthor = null
                activeReplyText = null
            },
            onPublishStory = { story ->
                viewModel.publishStory(story)
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. TOP NAVIGATION & HEADER BAR (Facebook Mobile Style)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark)
            ) {
 // Top Header Row: Brand Logo ("NEXA") on left + Max 3 Action Icons on right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: NEXA Brand Logo & Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedSecondaryTab = 0 }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nexa_3d_icon_1785719681308),
                            contentDescription = "NEXA Logo",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, NeonCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "NEXA",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    // Right Actions: Exactly 3 essential action icons (Search, Voice Assistant Mic, Plus Create)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Search Icon (البحث)
                        IconButton(
                            onClick = { showVoiceAssistantModal = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 2. Voice Assistant Mic Icon 🎙️ (المساعد الصوتي)
                        IconButton(
                            onClick = { showVoiceAssistantModal = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.18f))
                                .border(1.dp, NeonCyan, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Assistant",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 3. Quick Create / Plus Icon (+) ➕ (إنشاء منشور)
                        IconButton(
                            onClick = { showCreatePostDialog = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.25f))
                                .border(1.dp, NeonCyan, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Post",
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

 // Secondary Tab Navigation Bar (Home , Friends , Messenger (+15), Reels , Notifications (+5), Profile )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        Triple(0, Icons.Default.Home, null),
                        Triple(1, Icons.Default.People, null),
                        Triple(2, Icons.Default.Chat, "+15"),
                        Triple(3, Icons.Default.PlayCircle, null),
                        Triple(4, Icons.Default.Notifications, "+5"),
                        Triple(5, Icons.Default.Person, null)
                    )

                    tabs.forEach { (index, icon, badge) ->
                        val isSelected = selectedSecondaryTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedSecondaryTab = index
                                    when (index) {
                                        2 -> onOpenChat()
                                        3 -> onNavigateToReels()
                                        5 -> onOpenServicesMenu()
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.TopEnd) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Tab $index",
                                        tint = if (isSelected) NeonCyan else Color.Gray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    if (badge != null) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 10.dp)
                                                .clip(CircleShape)
                                                .background(NeonPink)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = badge,
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Active tab indicator underline
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (isSelected) NeonCyan else Color.Transparent)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )
            }
        }

        // Facebook-Style Data Saver Top Banner (*6 Social Media Pass)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                com.example.ui.components.FacebookDataSaverBanner(
                    isDataSaverEnabled = isDataSaverEnabled,
                    isSocialPass6Active = isSocialPass6Active,
                    onToggleDataSaver = { viewModel.toggleDataSaver(it) }
                )
            }
        }

        // 2. STORY / STATUS CREATION PROMPT BAR ("بم تفكر؟" What's on your mind?)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right: User Profile Avatar (RTL start)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.35f))
                            .border(1.5.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.name?.take(1) ?: "م",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Middle: "بم تفكر؟" (What's on your mind?) rounded input button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .clickable { showCreatePostDialog = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "بم تفكر في مجرة اليوم؟...",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Left: "صورة" (Photo/Media shortcut button)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(EncryptedGreen.copy(alpha = 0.2f))
                            .border(1.dp, EncryptedGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { showCreatePostDialog = true }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Photo/Media",
                            tint = EncryptedGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "صورة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 3. VERTICAL RECTANGULAR STORIES FEED (Facebook Style Stories Carousel)
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // First Story Card: "أنشئ قصة" (Create Story Card)
                    item {
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(175.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                                .clickable { showStoryCreatorModal = true }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Top 62%: User Cover / Profile Avatar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.62f)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(NeonPurple, BackgroundDark)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .border(1.5.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = profile?.name?.take(1) ?: "م",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }

                                // Bottom 38%: Plus Action Circle Button & Label "أنشئ قصة"
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.38f)
                                        .background(Color.Black.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(NeonCyan),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add Story",
                                                tint = BackgroundDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "أنشئ قصة",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // User Story Cards List
                    items(stories) { story ->
                        val gradientColors = story.bgGradient.map { Color(it) }
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(175.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Brush.verticalGradient(gradientColors))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                                .clickable { activeStoryForViewer = story }
                                .padding(8.dp)
                        ) {
                            // Top Profile Avatar with Glowing Ring
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BackgroundDark)
                                    .border(2.dp, NeonCyan, CircleShape)
                                    .align(Alignment.TopStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = story.authorName.take(1),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Story Caption / Title at Bottom
                            Column(
                                modifier = Modifier.align(Alignment.BottomStart)
                            ) {
                                Text(
                                    text = story.reelTitle ?: story.text,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = story.authorName,
                                    color = Color.LightGray,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
        }

        // Monetization System Notification Banner (if any)
        if (monetizationMessage != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
                        .clickable { viewModel.clearMonetizationMessage() }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = monetizationMessage ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
 text ="إغلاق",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Posting Restriction Warning Banner (AI Moderation)
        if (isPostingRestricted) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NeonAmber.copy(alpha = 0.18f))
                        .border(1.dp, NeonAmber, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
 text = postingRestrictionMessage ?:" إنذار أمان: تم تقييد النشر لمدة 24 ساعة بسبب مخالفة المعايير.",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "فهم التقييد",
                            color = NeonAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable { viewModel.dismissPostingRestriction() }
                        )
                    }
                }
            }
        }

        // Under 18 Smart Screen Time & Break Control Card
        if (isTeenMode) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(TeenProtectionCyan.copy(alpha = 0.15f))
                        .border(1.dp, TeenProtectionCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Screen Time Protection",
                                    tint = TeenProtectionCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
 text ="نظام استراحة الشاشة للناشئة (under_18_mode)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "تتبع نشط وتنبيه أوتوماتيكي بعد ساعتين لاستراحة عينيك ودراستك",
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Current Screen Time Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BackgroundDark)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = com.example.ui.components.formatTimeDuration(activeScreenTimeSeconds),
                                    color = TeenProtectionCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Test & Simulation Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { viewModel.simulateTwoHoursUsage() },
                                colors = ButtonDefaults.buttonColors(containerColor = TeenProtectionCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
 Text("محاكاة ساعتين", color = BackgroundDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.simulateReEntryAttempt() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
 Text("إعادة دخول", color = NeonAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.unlockScreenTimeForTest() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
 Text("فك القفل", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Google AdMob Space
        item {
            AdMobBannerSpace(
                isVipMember = isVipMember,
                isAdWatching = isAdWatching,
                adWatchProgress = adWatchProgress,
                onWatchRewardedAd = { viewModel.watchRewardedAdForCredits() },
                onGoVip = { showVipModal = true },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }


        // HERO BANNER CAROUSEL
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = if (isTeenMode) R.drawable.img_teen_protection else R.drawable.img_hero_banner
                    ),
                    contentDescription = "Hero Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.85f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        GlassBadge(
                            text = if (isTeenMode) "حماية الناشئة الذكية" else "المتجر المستقبلي",
                            accentColor = if (isTeenMode) TeenProtectionCyan else NeonPink
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isTeenMode) "محتوى آمن مع تصفح تفاعلي مشفر" else "عروض النيون والحصرية في مجرة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "استكشف أحدث المنشورات والأغراض الرقمية",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 3D BUBBLES INTERACTIVE ORBIT GRID
        item {
            Bubble3DOrbit(
                bubbles = bubbles,
                isTeenMode = isTeenMode,
                onBubbleClick = { bubble ->
                    if (bubble.iconType == "chat") {
                        onOpenChat()
                    }
                }
            )
        }

        // POSTS FEED SECTION TITLE
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
 text ="منشورات مجتمع مجرة",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "الكل",
                    color = NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // SOCIAL POSTS FEED LIST
        items(filteredPosts) { post ->
            PostCardItem(
                post = post,
                onLikeClick = { viewModel.toggleLike(post) },
                onCommentsClick = { activeCommentsPost = post },
                onProductClick = { post.taggedProductId?.let { id -> onNavigateToProduct(id) } },
                onReplyWithVideo = { author, text ->
                    activeReplyAuthor = author
                    activeReplyText = text
                    showStoryCreatorModal = true
                },
                onReportClick = {
                    activeReportPost = post
                },
                onLongClick = {
                    activeQuickActionOverlayPost = post
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // CREATE POST MODAL DIALOG
    if (showCreatePostDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showCreatePostDialog = false }) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
 text ="نشر محتوى جديد في مجرة",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("اكتب منشورك الزجاجي هنا...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showCreatePostDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("إلغاء", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (newPostText.isNotBlank()) {
                                    viewModel.createPost(newPostText)
                                    newPostText = ""
                                    showCreatePostDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("نشر", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showVoiceAssistantModal) {
        NexaVoiceAssistantModal(
            onDismiss = { showVoiceAssistantModal = false },
            onNavigateToReels = {
                showVoiceAssistantModal = false
                onNavigateToReels()
            },
            onOpenVault = {
                showVoiceAssistantModal = false
                showPrivateVaultModal = true
            },
            onOpenChat = {
                showVoiceAssistantModal = false
                onOpenChat()
            },
            onSearchPosts = { query ->
                showVoiceAssistantModal = false
            }
        )
    }

    if (showPrivateVaultModal) {
        NexaPrivateVaultModal(
            onDismiss = { showPrivateVaultModal = false }
        )
    }

    activeQuickActionOverlayPost?.let { post ->
        QuickActionRadialOverlay(
            targetTitle = post.content.take(30),
            authorName = post.authorName,
            onDismiss = { activeQuickActionOverlayPost = null },
            onLike = { viewModel.toggleLike(post) },
            onBookmark = { viewModel.toggleBookmark(post.id) },
            onShare = { viewModel.sharePost(post.id) },
            onSendDirectMessage = {
                activeQuickActionOverlayPost = null
                onOpenChat()
            },
            onMoveToVault = {
                activeQuickActionOverlayPost = null
                showPrivateVaultModal = true
            }
        )
    }
}

@Composable
fun PostCardItem(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentsClick: (() -> Unit)? = null,
    onProductClick: () -> Unit,
    onReplyWithVideo: ((author: String, text: String) -> Unit)? = null,
    onReportClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
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
                            .background(NeonPurple.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1),
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
                            text = "منذ بضع دقائق • مشفر زجاجياً",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (post.isTeenSafe) {
 GlassBadge(text ="آمن للناشئة", accentColor = TeenProtectionCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (onLongClick != null) {
                        IconButton(onClick = onLongClick, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Quick Radial Menu",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (onReportClick != null) {
                        IconButton(onClick = onReportClick, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Report Post",
                                tint = NeonPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = post.content,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Tagged product shortcut box
            if (post.taggedProductId != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onProductClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Product Tag",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
 text ="منتج مرتبط من المتجر - انقر للاستعراض",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer (Likes, Comments with Crowns, Video Reply)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLikeClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) NeonPink else Color.Gray
                        )
                    }
                    Text(
                        text = "${post.likesCount}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCommentsClick?.invoke() }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = "Comments",
                            tint = NeonAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
 text ="التيجان والتعليقات (${post.commentsCount})",
                            color = NeonAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (onReplyWithVideo != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonPurple.copy(alpha = 0.25f))
                            .border(1.dp, NeonPurple, RoundedCornerShape(10.dp))
                            .clickable { onReplyWithVideo(post.authorName, post.content) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
 text ="رد بفيديو/ستوري",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
