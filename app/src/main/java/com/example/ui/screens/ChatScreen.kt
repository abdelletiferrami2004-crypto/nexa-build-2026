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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
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
import com.example.data.model.ChatMessage
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.PinLockDialog
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

import androidx.compose.ui.platform.LocalContext
import com.example.util.NotificationSoundManager

import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import com.example.ui.components.AdMobBannerSpace

@Composable
fun ChatScreen(
    viewModel: MajarrahViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isUnlocked by viewModel.isChatUnlocked.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val selectedConvId by viewModel.selectedConversationId.collectAsState()
    val messages by viewModel.currentConversationMessages.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val isVipMember = profile?.isVipMember ?: false
    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adWatchProgress by viewModel.adWatchProgress.collectAsState()

    var showPinDialog by remember { mutableStateOf(!isUnlocked) }
    var messageText by remember { mutableStateOf("") }
    var showVipModal by remember { mutableStateOf(false) }
    var showVoiceTutorModal by remember { mutableStateOf(false) }
    var showAiToolboxModal by remember { mutableStateOf(false) }

    if (showVipModal) {
        com.example.ui.components.NexaVipSubscriptionModal(
            isCurrentlyVip = isVipMember,
            onSubscribe = { tier -> viewModel.activateVipSubscription(tier) },
            onDismiss = { showVipModal = false }
        )
    }

    if (showVoiceTutorModal) {
        com.example.ui.components.VoiceTutorModal(
            onDismiss = { showVoiceTutorModal = false }
        )
    }

    if (showAiToolboxModal) {
        com.example.ui.components.AiToolboxModal(
            onDismiss = { showAiToolboxModal = false }
        )
    }

    // If chat is locked, show PIN lock screen
    if (!isUnlocked || showPinDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            PinLockDialog(
                onDismiss = onBackClick,
                onPinSuccess = {
                    viewModel.unlockChatWithPin()
                    showPinDialog = false
                }
            )
        }
        return
    }

    // UNLOCKED ENCRYPTED CHAT VIEW
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top Encrypted Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
 text = if (isVipMember)"مساعد NEXA AI الملكي (256-bit E2EE)" else"مساعد NEXA AI المشفر",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Unlocked",
                            tint = EncryptedGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
 text = if (isVipMember)"وضع السرعة الفائقة Turbo AI نشط" else"محمية برمز PIN | تشفير تام",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isVipMember) NeonCyan else EncryptedGreen
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // AI Toolbox Hub Button
                IconButton(
                    onClick = { showAiToolboxModal = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.25f))
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "NEXA AI Toolbox Hub",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Voice Tutor Call Button
                IconButton(
                    onClick = { showVoiceTutorModal = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NeonPink.copy(alpha = 0.25f))
                        .border(1.dp, NeonPink, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "NEXA Voice Tutor",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Export Chat History Button (PDF / Text)
                val currentContext = LocalContext.current
                IconButton(
                    onClick = {
                        val exportBody = messages.joinToString("\n\n-----------------------------------\n\n") { msg ->
 val sender = if (msg.isFromUser)"المستخدم" else"ذكاء NEXA AI"
                            "$sender:\n${msg.text}"
                        }
                        com.example.util.FileExportManager.exportPdfSummary(
                            context = currentContext,
 documentTitle ="تقرير وسجل محادثة NEXA AI المشفرة",
 bodyText = exportBody.ifBlank {"لا توجد رسائل سابقة في المحادثة" }
                        )
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NeonPurple.copy(alpha = 0.25f))
                        .border(1.dp, NeonPurple, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export Chat History PDF/Text",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // VIP Badge
                if (!isVipMember) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonPink.copy(alpha = 0.2f))
                            .border(1.dp, NeonPink, RoundedCornerShape(10.dp))
                            .clickable { showVipModal = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
 Text(text ="ترقية VIP", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                IconButton(
                    onClick = {
                        viewModel.lockChat()
                        showPinDialog = true
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Chat Now",
                        tint = Color.Red
                    )
                }
            }
        }

        // AdMob Banner Space inside Chat Screen for non-VIP
        AdMobBannerSpace(
            isVipMember = isVipMember,
            isAdWatching = isAdWatching,
            adWatchProgress = adWatchProgress,
            onWatchRewardedAd = { viewModel.watchRewardedAdForCredits() },
            onGoVip = { showVipModal = true },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // AI Quick Prompts Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
 AiQuickPromptChip(text ="AI Toolbox", icon = Icons.Default.Build) {
                showAiToolboxModal = true
            }
 AiQuickPromptChip(text ="Voice Tutor", icon = Icons.Default.Mic) {
                showVoiceTutorModal = true
            }
 AiQuickPromptChip(text ="أفكار إبداعية", icon = Icons.Default.Lightbulb) {
                messageText = "اقترح لي 3 أفكار إبداعية لمشروع تقني جديد في المملكة"
            }
 AiQuickPromptChip(text ="توليد صورة", icon = Icons.Default.Image) {
                messageText = "صمم صورة مستقبلي ثلاثية الأبعاد لمدينة الرياض عام 2030"
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Encryption Notice Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EncryptedGreen.copy(alpha = 0.15f))
                        .border(1.dp, EncryptedGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Encrypted Shield",
                            tint = EncryptedGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جميع المحادثات والبيانات مشفرة 256-Bit E2EE ومحفوظة سحابياً بشكل آمن.",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            items(messages) { msg ->
                ChatMessageBubbleItem(
                    message = msg,
                    isVip = isVipMember,
                    onReactionSelect = { reaction ->
                        viewModel.toggleMessageReaction(msg, reaction)
                    }
                )
            }
        }

        // Bottom Message Input Bar
        val isListening by com.example.util.SpeechAndTtsManager.isListening.collectAsState()
        var showPhotoMenu by remember { mutableStateOf(false) }

        val targetId = selectedConvId ?: "ai_bot"

        val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
        ) { uri: android.net.Uri? ->
            uri?.let {
                try {
                    viewModel.sendImageMessage(targetId, it.toString(), messageText)
                    messageText = ""
                } catch (e: Throwable) {
                    android.util.Log.e("ChatScreen", "Error picking image", e)
                }
            }
        }

        val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
        ) { bmp: android.graphics.Bitmap? ->
            bmp?.let {
                viewModel.attachImageForAi(it)
                viewModel.sendImageMessage(targetId, "data:image/jpeg;base64,sample_captured_image", messageText)
                messageText = ""
            }
        }

        if (isListening) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonPink.copy(alpha = 0.2f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
 Text("جاري الاستماع لصوتك... تحدث الآن", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Picker Action
            Box {
                IconButton(
                    onClick = { showPhotoMenu = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Image,
                        contentDescription = "Attach Photo",
                        tint = NeonCyan
                    )
                }

                androidx.compose.material3.DropdownMenu(
                    expanded = showPhotoMenu,
                    onDismissRequest = { showPhotoMenu = false }
                ) {
                    androidx.compose.material3.DropdownMenuItem(
 text = { Text("اختر صورة من المعرض") },
                        onClick = {
                            showPhotoMenu = false
                            galleryLauncher.launch("image/*")
                        }
                    )
                    androidx.compose.material3.DropdownMenuItem(
 text = { Text("التقط صورة بالكاميرا") },
                        onClick = {
                            showPhotoMenu = false
                            cameraLauncher.launch(null)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Mic Voice Action
            IconButton(
                onClick = {
                    if (isListening) {
                        com.example.util.SpeechAndTtsManager.stopListening()
                    } else {
                        com.example.util.SpeechAndTtsManager.startListening(
                            context = context,
                            onResult = { spoken ->
                                messageText = spoken
                            }
                        )
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isListening) NeonPink else Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListening) Color.White else NeonCyan
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("اكتب أو تحدث لـ NEXA AI...", color = Color.Gray, fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EncryptedGreen,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val targetId = selectedConvId ?: conversations.firstOrNull()?.id ?: "conv_1"
                    if (messageText.isNotBlank()) {
                        NotificationSoundManager.playPopChime(context)
                        viewModel.sendChatMessage(targetId, messageText)
                        viewModel.sendAiPrompt(messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(EncryptedGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Encrypted Message",
                    tint = BackgroundDark
                )
            }
        }
    }
}

@Composable
fun AiQuickPromptChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}
@Composable
fun ChatMessageBubbleItem(
    message: ChatMessage,
    isVip: Boolean = false,
    onReactionSelect: ((String) -> Unit)? = null
) {
    val isUser = message.isFromUser
    var showReactionMenu by remember { mutableStateOf(false) }

    val reactionsList = listOf("❤️", "👍", "🔥", "😂", "😮", "👏")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        ),
                        spotColor = if (isUser) EncryptedGreen else NeonCyan
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        )
                    )
                    .background(
                        if (isUser) EncryptedGreen.copy(alpha = 0.28f) else NeonPurple.copy(alpha = 0.35f)
                    )
                    .border(
                        1.dp,
                        if (isUser) EncryptedGreen.copy(alpha = 0.6f) else NeonCyan.copy(alpha = 0.6f),
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        )
                    )
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = message.senderName,
                                color = if (isUser) EncryptedGreen else NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            if (isUser && isVip) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "VIP",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "256-bit E2EE",
                                color = Color.LightGray,
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Reaction trigger button
                            Box {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Add Reaction",
                                    tint = Color.LightGray,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clickable { showReactionMenu = true }
                                )

                                androidx.compose.material3.DropdownMenu(
                                    expanded = showReactionMenu,
                                    onDismissRequest = { showReactionMenu = false },
                                    modifier = Modifier.background(BackgroundDark)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        reactionsList.forEach { emoji ->
                                            Text(
                                                text = emoji,
                                                fontSize = 20.sp,
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        onReactionSelect?.invoke(emoji)
                                                        showReactionMenu = false
                                                    }
                                                    .padding(4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (!isUser) {
                                Spacer(modifier = Modifier.width(6.dp))
                                val context = LocalContext.current
                                IconButton(
                                    onClick = {
                                        com.example.util.FileExportManager.exportPdfSummary(
                                            context = context,
                                            documentTitle = "إجابة ذكاء NEXA AI",
                                            bodyText = message.text
                                        )
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Export Response as PDF/Text",
                                        tint = NeonPink,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        com.example.util.SpeechAndTtsManager.speak(message.text, context)
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak AI Message",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Media Image Display if present
                    if (message.mediaType == "image" || !message.mediaUrl.isNullOrBlank()) {
                        val imageUrl = message.mediaUrl ?: "https://picsum.photos/500/300"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            coil.compose.AsyncImage(
                                model = imageUrl,
                                contentDescription = "Attached Image",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = message.text,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Reaction Badge below message bubble
            if (!message.reaction.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .clickable { onReactionSelect?.invoke(message.reaction) }
                ) {
                    Text(
                        text = "${message.reaction} 1",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

