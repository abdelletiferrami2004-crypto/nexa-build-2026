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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MajarrahViewModel
import com.example.ui.components.BlueVerificationBadge
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

data class FriendItem(
    val name: String,
    val status: String,
    val isOnline: Boolean,
    val isTeenSafe: Boolean,
    val isVerified: Boolean = true
)

@Composable
fun SocialScreen(
    viewModel: MajarrahViewModel,
    onOpenChatWithFriend: (String) -> Unit,
    onNavigateToReels: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showStoryCreator by remember { mutableStateOf(false) }
    var activeStoryForViewer by remember { mutableStateOf<com.example.data.model.StoryItem?>(null) }

    val stories by viewModel.stories.collectAsState()

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

    if (showStoryCreator) {
        com.example.ui.components.StoryCreatorModal(
            onDismiss = { showStoryCreator = false },
            onPublishStory = { story ->
                viewModel.publishStory(story)
            }
        )
    }

    val friendsList = listOf(
 FriendItem("نورا القحطاني","تستكشف المنتجات الجديدة في المتجر", true, true),
 FriendItem("فيصل العتيبي","يبني تطبيقه القادم بـ Jetpack Compose", true, true),
 FriendItem("عبدالله الشهري","متواجد في دردشة مجرة المشفرة", false, true),
 FriendItem("سارة النمر","تتابع أحدث القصص والريلز", true, true),
 FriendItem("أحمد الغامدي","مشغول بالتسوق الذكي", false, false)
    )

    val profile by viewModel.userProfile.collectAsState()
    val isTeen = profile?.isTeenMode ?: true

    val filteredFriends = if (isTeen) friendsList.filter { it.isTeenSafe } else friendsList

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Header Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
 text ="الأصدقاء والمجتمعات",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

 GlassBadge(text ="محيط مجرة", accentColor = NeonCyan)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث عن أصدقاء أو مجتمعات...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Stories Row (قصص الأصدقاء)
        item {
            Text(
 text ="قصص الأصدقاء التفاعلية",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Add Story Bubble
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { showStoryCreator = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(2.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Story", tint = NeonCyan, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("إنشاء ستوري", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Dynamic Published Stories
                items(stories) { story ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { activeStoryForViewer = story }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (story.isReelShare) NeonPink.copy(alpha = 0.35f) else NeonPurple.copy(alpha = 0.4f))
                                .border(2.dp, if (story.isReelShare) NeonPink else TeenProtectionCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(story.authorName.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
 text = if (story.isReelShare)" ${story.authorName.split("").first()}" else story.authorName.split("").first(),
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                items(filteredFriends) { friend ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.3f))
                                .border(2.dp, if (friend.isOnline) EncryptedGreen else NeonPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(friend.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(friend.name.split(" ").first(), color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Friends List
        item {
            Text(
                text = "قائمة الأصدقاء النشطين",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        items(filteredFriends) { friend ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(friend.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            if (friend.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(EncryptedGreen)
                                        .border(2.dp, BackgroundDark, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = friend.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (friend.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    BlueVerificationBadge(size = 14.dp)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = friend.status,
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Direct chat button
                    IconButton(
                        onClick = { onOpenChatWithFriend("conv_1") },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EncryptedGreen.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Chat",
                            tint = EncryptedGreen
                        )
                    }
                }
            }
        }
    }
}
