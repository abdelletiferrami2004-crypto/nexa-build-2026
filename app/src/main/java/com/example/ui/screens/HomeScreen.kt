package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Post
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.NexaPrivateVaultModal
import com.example.ui.components.NexaVoiceAssistantModal
import com.example.ui.components.QuickActionRadialOverlay
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

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
            onLikeToggle = { viewModel.toggleStoryLike(story.id) },
            onSendReply = { text -> viewModel.sendStoryReply(story.id, text) },
            onShareForward = {
                viewModel.publishReelToStory(
                    reelTitle = story.reelTitle ?: "مقطع مميز",
                    reelAuthor = story.reelAuthor ?: "@nexa_official",
                    userCaption = "مشاركة القصة"
                )
            },
            onNavigateToReels = {
                activeStoryForViewer = null
                onNavigateToReels()
            },
            onDismiss = { activeStoryForViewer = null }
        )
    }

    var selectedSecondaryTab by remember { mutableStateOf(0) }
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
            onReport = { reason -> viewModel.reportContent("post_${postToReport.id}", reason) },
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
            onBlock = { viewModel.blockUser(postToReport.authorName) },
            onDismiss = { activeReportPost = null }
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
            onPublishStory = { story -> viewModel.publishStory(story) }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. TOP HEADER & NAVIGATION BAR
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedSecondaryTab = 0 }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nexa_3d_icon_1785719681308),
                            contentDescription = "NEXA Logo",
                            modifier = Modifier
                                .size(36.dp)
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = { showVoiceAssistantModal = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { showVoiceAssistantModal = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Assistant",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { showCreatePostDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Post",
                                tint = BackgroundDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Secondary Tab Bar
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(2.dp)
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
                        .background(Color.White.copy(alpha = 0.08f))
                )
            }
        }

        // 2. STORIES CAROUSEL (Instagram-Style Circular Circles)
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item 1: Add My Story Circle
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showStoryCreatorModal = true }
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(1.5.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = profile?.name?.take(1) ?: "م",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan)
                                        .border(2.dp, BackgroundDark, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Story",
                                        tint = BackgroundDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "قصتك",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // User Stories
                    items(stories) { story ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { activeStoryForViewer = story }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(NeonCyan, NeonPink, NeonPurple)
                                        )
                                    )
                                    .padding(2.5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(BackgroundDark)
                                        .padding(2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(NeonPurple.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = story.authorName.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = story.authorName,
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(68.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.05f))
                )
            }
        }

        // 3. WHAT'S ON YOUR MIND PROMPT BAR
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.35f))
                            .border(1.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.name?.take(1) ?: "م",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showCreatePostDialog = true }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "بم تفكر اليوم فـ NEXA؟...",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(EncryptedGreen.copy(alpha = 0.15f))
                            .clickable { showCreatePostDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
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
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 4. POSTS FEED SECTION HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المنشورات الحديثة",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "الأحدث",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 5. SOCIAL POSTS FEED LIST
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
                onReportClick = { activeReportPost = post },
                onLongClick = { activeQuickActionOverlayPost = post }
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
                        text = "نشر محتوى جديد",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newPostText,
                        onValueChange = { newPostText = it },
                        placeholder = { Text("اكتب منشورك هنا...", color = Color.Gray) },
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
            onSearchPosts = { showVoiceAssistantModal = false }
        )
    }

    if (showPrivateVaultModal) {
        NexaPrivateVaultModal(onDismiss = { showPrivateVaultModal = false })
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
                            .size(40.dp)
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
                            text = "منذ بضع دقائق",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (post.isTeenSafe) {
                        GlassBadge(text = "آمن", accentColor = TeenProtectionCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (onLongClick != null) {
                        IconButton(onClick = onLongClick, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Quick Menu",
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
                        text = "منتج مرتبط من المتجر - انقر للاستعراض",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer (Likes & Comments)
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
                            text = "التعليقات (${post.commentsCount})",
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
                            text = "رد بفيديو",
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
