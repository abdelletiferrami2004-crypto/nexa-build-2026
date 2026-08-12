package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.StoryItem
import com.example.ui.MajarrahViewModel
import com.example.ui.components.ReelStoryViewerModal
import com.example.ui.components.StoryCreatorModal
import com.example.ui.theme.BackgroundDark
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

    // Use storiesList if provided, otherwise fallback to viewModel stories
    val displayStories = if (storiesList.isNotEmpty()) storiesList else vmStories

    // Modals for story creation and viewing
    var showStoryCreatorModal by remember { mutableStateOf(false) }
    var activeStoryForViewer by remember { mutableStateOf<StoryItem?>(null) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // TOP HEADER BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nexa",
                color = NeonCyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { onSettingsClick() }) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { onOpenChat() }) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Messages",
                        tint = Color.White
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
                                    .background(Color.White.copy(alpha = 0.1f))
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
                                color = Color.White,
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
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // 2. CLEAN FEED (خلاصة المنشورات بدون إعلانات أو كروت مزعجة)
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
        }
    }
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
