package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.data.model.CreatorBadgeTier
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
import androidx.compose.ui.platform.LocalContext
import com.example.util.NotificationSoundManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Post
import com.example.data.model.PostComment
import com.example.ui.MajarrahViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun PostCommentsModal(
    post: Post,
    viewModel: MajarrahViewModel,
    onDismiss: () -> Unit,
    onReplyWithStory: (authorName: String, commentText: String) -> Unit
) {
    val context = LocalContext.current
    val commentsMap by viewModel.postCommentsMap.collectAsState()
    val commentsList = commentsMap[post.id] ?: emptyList()

    val pendingOffense by viewModel.pendingOffense.collectAsState()
    val timerSeconds by viewModel.offenseTimerSeconds.collectAsState()
    val isBanned by viewModel.isCommentBanned.collectAsState()
    val banSeconds by viewModel.banTimeRemainingSeconds.collectAsState()

    var commentText by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var showTagDropdown by remember { mutableStateOf(false) }

    var resolveEditText by remember { mutableStateOf("") }
    var showResolveDialog by remember { mutableStateOf(false) }
    var resolveErrorMsg by remember { mutableStateOf<String?>(null) }

 // Sort dynamically by likes descending to determine crown positions 
    val sortedComments = remember(commentsList) {
        commentsList.sortedByDescending { it.likesCount }
    }

    val availableTags = listOf(
"سماعات النيون",
"ساعة NEXA الذكية",
"خوذة الواقِع الافتراضي",
"ذكاء NEXA AI",
"خدمة التشفير PIN"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = BackgroundDark.copy(alpha = 0.95f),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .border(1.dp, NeonPurple.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NeonAmber.copy(alpha = 0.2f))
                                .border(1.dp, NeonAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
 Text("تعليقات وتيجان المنشور", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
 Text("المرتبة تتغير تلقائياً حسب عدد اللايكات", color = NeonCyan, fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ---------------- MODERATION ALERT BANNERS ----------------
                if (pendingOffense != null) {
                    val minutes = timerSeconds / 60
                    val seconds = timerSeconds % 60
                    val formattedTimer = String.format("%02d:%02d", minutes, seconds)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Red.copy(alpha = 0.15f))
                            .border(1.5.dp, Color.Red, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
 Text("إنذار الحظر الذكي", color = Color.Red, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Red)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("مهلة: $formattedTimer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "تم حجب تعليقك لاحتوائه الكلمة غير اللائقة: '${pendingOffense?.detectedWord}'. لديك 5 دقائق لتعديله لمنع تطبيق الحظر التلقائي لمدة 24 ساعة.",
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NeonCyan)
                                        .clickable {
                                            resolveEditText = pendingOffense?.originalText ?: ""
                                            showResolveDialog = true
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
 Text("تعديل التعليق الآن", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .clickable { viewModel.skipOffenseTimerForTesting() }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
 Text("تخطي الـ 5د (تجربة الحظر )", color = NeonAmber, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                if (isBanned) {
                    val banHours = banSeconds / 3600
                    val banMins = (banSeconds % 3600) / 60
                    val banSecs = banSeconds % 60
                    val formattedBan = String.format("%02d:%02d:%02d", banHours, banMins, banSecs)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Red.copy(alpha = 0.25f))
                            .border(2.dp, Color.Red, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
 Text("حظر التعليق مفعل (24h Ban)", color = Color.Red, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("تم حظرك تلقائياً لعدم تعديل الكلمات غير اللائقة خلال مهلة الـ 5 دقائق.", color = Color.White, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
 Text("الوقت المتبقي لرفع الحظر: $formattedBan", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                // -----------------------------------------------------------

                // Comments List
                if (sortedComments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
 Text("لا توجد تعليقات بعد. كن أول من يعلق ويتوج بالتاج الأول", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(sortedComments) { index, comment ->
                            val rank = index + 1
                            CommentCardItem(
                                comment = comment,
                                rank = rank,
                                onLikeClick = { viewModel.toggleLikeComment(post.id, comment.id) },
                                onReplyWithStory = { onReplyWithStory(comment.authorName, comment.text) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset Toxic Word Test Chip
                if (!isBanned && pendingOffense == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اختبار سريع:", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Red.copy(alpha = 0.2f))
                                .border(0.5.dp, Color.Red, RoundedCornerShape(8.dp))
                                .clickable { commentText = "هذا منتج سيء وغير مفيد" }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
 Text("تجربة كلمة غير لائقة", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Tag Selector Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedTag != null) NeonPurple.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                                .border(1.dp, if (selectedTag != null) NeonCyan else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { if (!isBanned) showTagDropdown = !showTagDropdown }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedTag ?: "ربط منتج/خدمة",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }

                        DropdownMenu(
                            expanded = showTagDropdown,
                            onDismissRequest = { showTagDropdown = false },
                            modifier = Modifier.background(BackgroundDark)
                        ) {
                            DropdownMenuItem(
                                text = { Text("بدون تاج", color = Color.Gray) },
                                onClick = {
                                    selectedTag = null
                                    showTagDropdown = false
                                }
                            )
                            availableTags.forEach { tag ->
                                DropdownMenuItem(
                                    text = { Text(tag, color = Color.White) },
                                    onClick = {
                                        selectedTag = tag
                                        showTagDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedTag != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "سيتم إرفاق التاج بالتعليق",
                            color = NeonAmber,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Write Comment Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        enabled = !isBanned,
                        placeholder = {
                            Text(
 if (isBanned)"أنت محظور من التعليق لمدة 24 ساعة" else"اكتب تعليقك واكسب تاجاً...",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledBorderColor = Color.Red.copy(alpha = 0.4f),
                            disabledTextColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank() && !isBanned) {
                                NotificationSoundManager.playPopChime(context)
                                viewModel.addCommentToPost(post.id, commentText, selectedTag)
                                commentText = ""
                                selectedTag = null
                            }
                        },
                        enabled = !isBanned,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isBanned) Color.Gray else NeonPurple)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }

    // Resolve / Edit Toxic Comment Modal Dialog
    if (showResolveDialog) {
        Dialog(onDismissRequest = { showResolveDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BackgroundDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, NeonCyan, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
 Text("تعديل المحتوى المسيء", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("قم بإزالة أي كلمات غير لائقة لتفعيل التعليق وإلغاء إنذار الحظر.", color = Color.Gray, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = resolveEditText,
                        onValueChange = {
                            resolveEditText = it
                            resolveErrorMsg = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (resolveErrorMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(resolveErrorMsg!!, color = Color.Red, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showResolveDialog = false }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("إلغاء", color = Color.Gray, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan)
                                .clickable {
                                    val success = viewModel.editAndResolveOffensiveComment(resolveEditText)
                                    if (success) {
                                        showResolveDialog = false
                                    } else {
                                        resolveErrorMsg = "ما زال النص يحتوي على كلمة غير لائقة! يرجى إزالتها."
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
 Text("حفظ ونشر التعليق", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCardItem(
    comment: PostComment,
    rank: Int,
    onLikeClick: () -> Unit,
    onReplyWithStory: () -> Unit
) {
    val authorFollowers = when {
        comment.authorName.contains("سارة") -> 1_250_000
        comment.authorName.contains("عبدالعزيز") -> 1_100_000
        comment.authorName.contains("خالد") -> 650_000
        comment.authorName.contains("فهد") -> 180_000
        rank == 1 -> 1_000_000
        else -> 85_000
    }
    val tier = CreatorBadgeTier.fromFollowers(authorFollowers)

    val (crownBadge, badgeColor, borderColor) = when (rank) {
 1 -> Triple("التعليق الأول", Color(0xFFFFD700), Color(0xFFFFD700))
 2 -> Triple("الثاني", Color(0xFFC0C0C0), Color(0xFFC0C0C0))
 3 -> Triple("الثالث", Color(0xFFCD7F32), Color(0xFFCD7F32))
        else -> Triple(null, Color.Transparent, Color.White.copy(alpha = 0.1f))
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (rank == 1) Color(0xFFFFD700).copy(alpha = 0.08f)
                else Color.White.copy(alpha = 0.05f)
            )
            .border(
                width = if (rank <= 3) 1.5.dp else 0.5.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            // Header: Author + Royal Avatar + Crown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CreatorAvatarWithAura(
                        followersCount = authorFollowers,
                        avatarUrl = comment.authorAvatarUrl,
                        authorInitial = comment.authorName,
                        size = 38.dp,
                        showBadgeChip = false
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(comment.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (tier != CreatorBadgeTier.NONE) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(tier.badgeEmoji, fontSize = 11.sp)
                            }
                        }
                        Text(comment.timestamp, color = Color.Gray, fontSize = 10.sp)
                    }
                }

                // Crown Badge & VIP Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (tier != CreatorBadgeTier.NONE) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(tier.primaryColorHex).copy(alpha = 0.2f))
                                .border(1.dp, Color(tier.primaryColorHex), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(tier.titleAr, color = Color(tier.primaryColorHex), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (crownBadge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, badgeColor, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(crownBadge, color = badgeColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Text
            Text(comment.text, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)

            // Tagged Product or Service Chip
            if (comment.taggedProductOrService != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .border(0.5.dp, NeonCyan, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "مرتبط بـ: ${comment.taggedProductOrService}",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Actions: Like count & Video Story Reply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onLikeClick() }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (comment.isLiked) NeonPink else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${comment.likesCount} إعجاب",
                        color = if (comment.isLiked) NeonPink else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Reply with Video Story Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonPurple.copy(alpha = 0.3f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable { onReplyWithStory() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
 Text("رد بستوري", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
