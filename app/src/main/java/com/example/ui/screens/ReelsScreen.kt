package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import com.example.ui.theme.EncryptedGreen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.material.icons.filled.Mic
import com.example.ui.components.NexaVoiceAssistantModal
import com.example.ui.components.NexaPrivateVaultModal
import com.example.ui.components.QuickActionRadialOverlay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.ui.MajarrahViewModel
import com.example.ui.components.CreatorAvatarWithAura
import com.example.ui.components.GlassBadge
import com.example.ui.components.PostCommentsModal
import com.example.ui.components.ReportAndBlockDialog
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ReelItem(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val soundTrack: String,
    val likesCount: String,
    val commentsCount: String,
    val repostsCount: String,
    val sharesCount: String,
    val savesCount: String,
    val gradientColors: List<Color>
)

@Composable
fun ReelsScreen(
    viewModel: MajarrahViewModel
) {
    val isDataSaverEnabled by viewModel.isDataSaverEnabled.collectAsState()

    val reelList = listOf(
        ReelItem(
            id = "reel_1",
            title = "تجربة الواقع الافتراضي النيون",
            author = "@majarrah_official",
            description = "شاهد أحدث التقنيات الافتراضية مع نظارات GlassVR المتوفرة الآن في المتجر الرسمية للتطبيق",
            soundTrack = "صوت مجرة الأصلي - نيون شات",
            likesCount = "204 ألف",
            commentsCount = "1,847",
            repostsCount = "2,866",
            sharesCount = "58.1 ألف",
            savesCount = "5,333",
            gradientColors = listOf(Color(0xFF0F0C20), Color(0xFF1F104D), Color(0xFF0D0620))
        ),
        ReelItem(
            id = "reel_2",
            title = "برمجة تطبيقات أندرويد بالذكاء الاصطناعي",
            author = "@tech_teen_sa",
            description = "كيف تبني تطبيق زجاجي مع خيارات الأمان والـ PIN بخطوات بسيطة مع استجابة فورية",
            soundTrack = "إيقاعات ذكاء اصطناعي - نكسة ميوزيك",
            likesCount = "142 ألف",
            commentsCount = "956",
            repostsCount = "1,420",
            sharesCount = "32.4 ألف",
            savesCount = "3,890",
            gradientColors = listOf(Color(0xFF0B192C), Color(0xFF1E3E62), Color(0xFF000000))
        ),
        ReelItem(
            id = "reel_3",
            title = "حقيبة اللوموس المضيئة للناشئة",
            author = "@lumos_gear",
            description = "استعراض حقيبة الظهر الذكية المقاومة للماء للشباب واليافعين مع إضاءة نيون خلفية",
            soundTrack = "موسيقى مستقبلية حماسية - سينمائي",
            likesCount = "318 ألف",
            commentsCount = "2,410",
            repostsCount = "4,105",
            sharesCount = "74.2 ألف",
            savesCount = "8,920",
            gradientColors = listOf(Color(0xFF130E26), Color(0xFF381452), Color(0xFF090714))
        )
    )

    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val reportedContentIds by viewModel.reportedContentIds.collectAsState()

    val filteredReels = remember(reelList, blockedUsers, reportedContentIds) {
        reelList.filter { reel ->
            !blockedUsers.contains(reel.author) &&
            !reportedContentIds.contains("reel_${reel.id}") &&
            !reportedContentIds.contains(reel.id)
        }
    }

    val pagerState = rememberPagerState(pageCount = { filteredReels.size })

    if (filteredReels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "لا توجد مقاطع ريلز متاحة حالياً (تم إخفاء المحتوى المبلغ عنه أو أصحاب الحسابات المحظورة)",
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(24.dp)
            )
        }
    } else {
        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) { page ->
            val reel = filteredReels[page]
        val haptic = LocalHapticFeedback.current
        var showVoiceAssistantModal by remember { mutableStateOf(false) }
        var showPrivateVaultModal by remember { mutableStateOf(false) }
        var showQuickActionRadialOverlay by remember { mutableStateOf(false) }

        var isLiked by remember { mutableStateOf(false) }
        var isBookmarked by remember { mutableStateOf(false) }
        var isFollowing by remember { mutableStateOf(false) }
        var isPlayingVideo by remember(isDataSaverEnabled) { mutableStateOf(!isDataSaverEnabled) }
        var showStoryReplyModal by remember { mutableStateOf(false) }
        var showReportModal by remember { mutableStateOf(false) }
        var showCommentsModal by remember { mutableStateOf(false) }
        var showNotificationBanner by remember { mutableStateOf<String?>(null) }

        // Double-Tap Heart Animation states
        var showDoubleTapHeart by remember { mutableStateOf(false) }
        val heartScale = remember { Animatable(0.2f) }
        val heartAlpha = remember { Animatable(1f) }
        val coroutineScope = rememberCoroutineScope()

        fun triggerDoubleTapLike() {
            isLiked = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            coroutineScope.launch {
                showDoubleTapHeart = true
                heartScale.snapTo(0.3f)
                heartAlpha.snapTo(1f)
                launch {
                    heartScale.animateTo(
                        targetValue = 1.3f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
                delay(600)
                heartAlpha.animateTo(0f, animationSpec = tween(300))
                showDoubleTapHeart = false
            }
        }

        // Vinyl Disc rotation transition
        val infiniteTransition = rememberInfiniteTransition(label = "vinylDisc")
        val discAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "discRotation"
        )

        if (showReportModal) {
            ReportAndBlockDialog(
                targetAuthorName = reel.author,
                contentId = "reel_${page}_${reel.author}",
                contentTypeTitle = "فيديو الريلز (${reel.title})",
                onReport = { reason ->
                    viewModel.reportContent("reel_${page}", reason)
                },
                onReportWithAi = { category, onFinished ->
                    viewModel.submitReportWithAiModeration(
                        targetAuthorName = reel.author,
                        contentId = "reel_${page}",
                        contentTypeTitle = "فيديو الريلز (${reel.title})",
                        category = category,
                        contentText = reel.description,
                        onCompleted = onFinished
                    )
                },
                onBlock = {
                    viewModel.blockUser(reel.author)
                },
                onDismiss = { showReportModal = false }
            )
        }

        if (showStoryReplyModal) {
            com.example.ui.components.StoryCreatorModal(
                initialReplyAuthor = reel.author,
                initialReplyText = reel.description,
                onDismiss = { showStoryReplyModal = false },
                onPublishStory = { story ->
                    viewModel.publishStory(story)
                }
            )
        }

        if (showCommentsModal) {
            val dummyPost = remember(reel) {
                Post(
                    id = page + 1,
                    authorName = reel.author,
                    content = reel.description,
                    timestamp = System.currentTimeMillis(),
                    likesCount = 12400,
                    commentsCount = 1847
                )
            }
            PostCommentsModal(
                post = dummyPost,
                viewModel = viewModel,
                onDismiss = { showCommentsModal = false },
                onReplyWithStory = { authorName, commentText ->
                    showCommentsModal = false
                    showStoryReplyModal = true
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = reel.gradientColors)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { triggerDoubleTapLike() },
                        onTap = { isPlayingVideo = !isPlayingVideo },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showQuickActionRadialOverlay = true
                        }
                    )
                }
        ) {
            // Play/Pause Center Overlay Indicator when paused
            if (!isPlayingVideo) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Reel",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Double Tap Heart Center Animation
            if (showDoubleTapHeart) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            scaleX = heartScale.value
                            scaleY = heartScale.value
                            alpha = heartAlpha.value
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Double Tap Heart",
                        tint = NeonPink,
                        modifier = Modifier.size(110.dp)
                    )
                }
            }

            // Top Bar Floating Voice Assistant & Vault Shortcuts
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 40.dp, start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { showVoiceAssistantModal = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showPrivateVaultModal = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, EncryptedGreen, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted Vault",
                        tint = EncryptedGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            if (showNotificationBanner != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                ) {
                    Text(
                        text = showNotificationBanner ?: "",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            // 2. Right-Aligned Interactive Floating Action Bar (Instagram Reels Style)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Like Button (Heart Icon) + Count
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { isLiked = !isLiked },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) NeonPink else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isLiked) "205 ألف" else reel.likesCount,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Comment Button (Speech Bubble Icon) + Count
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showCommentsModal = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reel.commentsCount,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Repost / Repost Story Button (Retweet Icon) + Count
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            viewModel.publishReelToStory(
                                reelTitle = reel.title,
                                reelAuthor = reel.author,
                                reelLikesCount = reel.likesCount,
                                reelCommentsCount = reel.commentsCount,
                                reelSoundTrack = reel.soundTrack,
                                userCaption = "شاهدوا هذا المقطع الرهيب في مجرة!"
                            )
                            showNotificationBanner = "تمت إعادة نشر الريلز إلى قصتك بنجاح"
                            coroutineScope.launch {
                                delay(2000)
                                showNotificationBanner = null
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Repost",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reel.repostsCount,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share / DM Button (Paper Plane Send Icon) + Count
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showStoryReplyModal = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reel.sharesCount,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bookmark / Save Button + Count
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { isBookmarked = !isBookmarked },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isBookmarked) NeonCyan else Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reel.savesCount,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // More Options Button (3-dots)
                IconButton(
                    onClick = { showReportModal = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.25f))
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Audio Disc Thumb / Creator Avatar Preview
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer { rotationZ = if (isPlayingVideo) discAngle else 0f }
                        .clip(CircleShape)
                        .background(Color.Black)
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(NeonCyan, NeonPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music Disc",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // 3. Bottom Creator Info Bar (Overlay at Bottom-Start)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 28.dp, end = 85.dp)
            ) {
                // Creator Avatar, Username & Follow Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    CreatorAvatarWithAura(
                        followersCount = if (reel.author.contains("majarrah")) 1_250_000 else 600_000,
                        authorInitial = reel.author,
                        size = 38.dp,
                        showBadgeChip = false
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = reel.author,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // "متابعة / Follow" Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isFollowing) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (isFollowing) Color.White.copy(alpha = 0.4f) else Color.White,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { isFollowing = !isFollowing }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isFollowing) "تمت المتابعة" else "متابعة",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Video Description / Caption
                Text(
                    text = reel.description,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Audio Track Ticker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sound Track",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reel.soundTrack,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (showVoiceAssistantModal) {
                NexaVoiceAssistantModal(
                    onDismiss = { showVoiceAssistantModal = false },
                    onNavigateToReels = { showVoiceAssistantModal = false },
                    onOpenVault = {
                        showVoiceAssistantModal = false
                        showPrivateVaultModal = true
                    },
                    onOpenChat = { showVoiceAssistantModal = false },
                    onSearchPosts = { showVoiceAssistantModal = false }
                )
            }

            if (showPrivateVaultModal) {
                NexaPrivateVaultModal(
                    onDismiss = { showPrivateVaultModal = false }
                )
            }

            if (showQuickActionRadialOverlay) {
                QuickActionRadialOverlay(
                    targetTitle = reel.title,
                    authorName = reel.author,
                    onDismiss = { showQuickActionRadialOverlay = false },
                    onLike = { isLiked = !isLiked },
                    onBookmark = { isBookmarked = !isBookmarked },
                    onShare = { showNotificationBanner = "تم مشاركة فيديو الريلز بنجاح! 🚀" },
                    onSendDirectMessage = { showStoryReplyModal = true },
                    onMoveToVault = { showPrivateVaultModal = true }
                )
            }
        }
    }
}
}
