package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.MajarrahViewModel
import com.example.ui.components.AdMobBannerSpace
import com.example.ui.components.AiToolboxModal
import com.example.ui.components.NexaVipSubscriptionModal
import com.example.ui.components.PinLockDialog
import com.example.ui.components.VoiceTutorModal
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.util.NotificationSoundManager
import com.example.util.PinLockManager

@Composable
fun ChatScreen(
    viewModel: MajarrahViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isUnlocked by viewModel.isChatUnlocked.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val selectedConvId by viewModel.selectedConversationId.collectAsState()
    val messages by viewModel.currentConversationMessages.collectAsState()
    val profileNullable by viewModel.userProfile.collectAsState()
    val profile = profileNullable ?: com.example.data.model.UserProfile()

    val isVipMember = profile.isVipMember
    val isPinProtectionEnabled = try {
        (profile.isChatPinEnabled && profile.chatPin.isNotBlank()) || PinLockManager.isPinEnabled(context)
    } catch (e: Throwable) {
        false
    }
    val effectivePin = profile.chatPin.ifBlank { PinLockManager.getSavedPin(context).ifBlank { "1234" } }
    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adWatchProgress by viewModel.adWatchProgress.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var showVipModal by remember { mutableStateOf(false) }
    var showVoiceTutorModal by remember { mutableStateOf(false) }
    var showAiToolboxModal by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showPhotoMenu by remember { mutableStateOf(false) }

    if (showVipModal) {
        NexaVipSubscriptionModal(
            isCurrentlyVip = isVipMember,
            onSubscribe = { tier -> viewModel.activateVipSubscription(tier) },
            onDismiss = { showVipModal = false }
        )
    }

    if (showVoiceTutorModal) {
        VoiceTutorModal(
            onDismiss = { showVoiceTutorModal = false }
        )
    }

    if (showAiToolboxModal) {
        AiToolboxModal(
            onDismiss = { showAiToolboxModal = false }
        )
    }

    // If PIN protection is enabled by user and chat is currently locked, show PIN lock screen
    if (isPinProtectionEnabled && (!isUnlocked || showPinDialog)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            PinLockDialog(
                correctPin = effectivePin,
                onDismiss = onBackClick,
                onPinSuccess = {
                    viewModel.unlockChatWithPin()
                    showPinDialog = false
                }
            )
        }
        return
    }

    val isListening by com.example.util.SpeechAndTtsManager.isListening.collectAsState()
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

    // MAIN UNLOCKED ENCRYPTED CHAT VIEW
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF090D16),
                        Color(0xFF0F172A),
                        Color(0xFF080C14)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. SLEEK, UNCLUTTERED TOP APP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B111E).copy(alpha = 0.95f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.07f),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button + AI Avatar & Identity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // AI Glowing Avatar
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        NeonCyan.copy(alpha = 0.85f),
                                        NeonPurple.copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .border(1.5.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Bot",
                            tint = BackgroundDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isVipMember) "NEXA AI Turbo" else "NEXA AI",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (isVipMember) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "VIP",
                                    tint = NeonAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EncryptedGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "متصل • تشفير 256-bit",
                                style = MaterialTheme.typography.bodySmall,
                                color = EncryptedGreen,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Compact Right Action Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // AI Toolbox Quick Action
                    IconButton(
                        onClick = { showAiToolboxModal = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "AI Toolbox",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Voice Tutor Call Action
                    IconButton(
                        onClick = { showVoiceTutorModal = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonPink.copy(alpha = 0.15f))
                            .border(1.dp, NeonPink.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Tutor",
                            tint = NeonPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // More Options Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier
                                .background(Color(0xFF13192B))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("تصدير المحادثة (PDF)", color = Color.White, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    showMoreMenu = false
                                    val exportBody = messages.joinToString("\n\n-----------------------------------\n\n") { msg ->
                                        val sender = if (msg.isFromUser) "المستخدم" else "ذكاء NEXA AI"
                                        "$sender:\n${msg.text}"
                                    }
                                    com.example.util.FileExportManager.exportPdfSummary(
                                        context = context,
                                        documentTitle = "تقرير وسجل محادثة NEXA AI المشفرة",
                                        bodyText = exportBody.ifBlank { "لا توجد رسائل سابقة في المحادثة" }
                                    )
                                }
                            )

                            if (!isVipMember) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Diamond, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("ترقية VIP الفائقة", color = NeonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        showVipModal = true
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("قفل الدردشة بـ PIN", color = Color.White, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.lockChat()
                                    showPinDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // 2. SLIM ADMOB BANNER (Non-intrusive micro strip)
            AdMobBannerSpace(
                isVipMember = isVipMember,
                isAdWatching = isAdWatching,
                adWatchProgress = adWatchProgress,
                onWatchRewardedAd = { viewModel.watchRewardedAdForCredits() },
                onGoVip = { showVipModal = true },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                compact = true
            )

            // 3. AI QUICK PROMPTS CHIPS BAR (Compact Horizontal Scroll)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                item {
                    AiQuickPromptChip(text = "💡 أفكار إبداعية", icon = Icons.Default.Lightbulb) {
                        messageText = "اقترح لي 3 أفكار إبداعية لمشروع تقني جديد وواعد"
                    }
                }
                item {
                    AiQuickPromptChip(text = "🎨 توليد صورة", icon = Icons.Default.Image) {
                        messageText = "صمم صورة ثلاثية الأبعاد خيالية لمدينة ذكية عام 2030"
                    }
                }
                item {
                    AiQuickPromptChip(text = "🧠 تحليل ذكي", icon = Icons.Default.Psychology) {
                        messageText = "اشرح لي باختصار كيف تعمل شبكات الذكاء الاصطناعي والتشفير"
                    }
                }
                item {
                    AiQuickPromptChip(text = "🛠️ أدوات AI", icon = Icons.Default.Build) {
                        showAiToolboxModal = true
                    }
                }
                item {
                    AiQuickPromptChip(text = "🎙️ درس صوتي", icon = Icons.Default.Mic) {
                        showVoiceTutorModal = true
                    }
                }
            }

            // 4. EXPANDED MESSAGES CHAT AREA (Maximized Canvas)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    // Empty State: Clean & Welcoming
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f))
                                .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "NEXA AI",
                                tint = NeonCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "مرحباً بك في NEXA AI",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "مساعدك الذكي المشفر بتشفير تام 256-bit E2EE.\nاكتب سؤالك أو اختر موضوعاً من المقترحات أدناه.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 6.dp, bottom = 8.dp)
                    ) {
                        // Subtle Security Divider
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = EncryptedGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "محادثة مشفرة E2EE ومحمية بنظام أمان NEXA",
                                        color = Color.LightGray,
                                        fontSize = 10.sp,
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
                                },
                                onCopyText = { text ->
                                    clipboardManager.setText(AnnotatedString(text))
                                }
                            )
                        }
                    }
                }
            }

            // 5. LIVE VOICE LISTENING PILL (Shows above input when recording)
            AnimatedVisibility(
                visible = isListening,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonPink.copy(alpha = 0.2f))
                        .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "جاري الاستماع لصوتك... تحدث الآن",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 6. MODERN FLOATING BOTTOM INPUT BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = NeonCyan.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color(0xFF111726).copy(alpha = 0.95f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment Action (Gallery / Camera)
                    Box {
                        IconButton(
                            onClick = { showPhotoMenu = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Attach Media",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showPhotoMenu,
                            onDismissRequest = { showPhotoMenu = false },
                            modifier = Modifier
                                .background(Color(0xFF13192B))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("معرض الصور", color = Color.White, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    showPhotoMenu = false
                                    galleryLauncher.launch("image/*")
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("التقاط صورة", color = Color.White, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    showPhotoMenu = false
                                    cameraLauncher.launch(null)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Voice Input Button
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isListening) NeonPink else Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isListening) Color.White else NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Text Input Field (Clean, uncluttered, smooth)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (messageText.isEmpty()) {
                            Text(
                                text = "اكتب رسالتك لـ NEXA AI...",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                        BasicTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 13.sp
                            ),
                            cursorBrush = SolidColor(NeonCyan),
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Circular Gradient Send Button
                    IconButton(
                        onClick = {
                            val targetConvId = selectedConvId ?: conversations.firstOrNull()?.id ?: "conv_1"
                            if (messageText.isNotBlank()) {
                                NotificationSoundManager.playPopChime(context)
                                viewModel.sendChatMessage(targetConvId, messageText)
                                viewModel.sendAiPrompt(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (messageText.isNotBlank()) {
                                    Brush.linearGradient(listOf(NeonCyan, NeonPurple))
                                } else {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.15f)))
                                }
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = if (messageText.isNotBlank()) BackgroundDark else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiQuickPromptChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131B2E).copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ChatMessageBubbleItem(
    message: ChatMessage,
    isVip: Boolean = false,
    onReactionSelect: ((String) -> Unit)? = null,
    onCopyText: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
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
                        elevation = 6.dp,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        spotColor = if (isUser) EncryptedGreen.copy(alpha = 0.4f) else NeonCyan.copy(alpha = 0.4f)
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF065F46).copy(alpha = 0.85f),
                                    Color(0xFF047857).copy(alpha = 0.85f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1A1B3A).copy(alpha = 0.9f),
                                    Color(0xFF131A33).copy(alpha = 0.9f)
                                )
                            )
                        }
                    )
                    .border(
                        1.dp,
                        if (isUser) EncryptedGreen.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.35f),
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header inside bubble: Sender & mini actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUser) "أنت" else "NEXA AI",
                                color = if (isUser) EncryptedGreen else NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            if (isUser && isVip) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "VIP",
                                    tint = NeonAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Emoji reaction trigger
                            Box {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Add Reaction",
                                    tint = Color.LightGray.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { showReactionMenu = true }
                                )

                                DropdownMenu(
                                    expanded = showReactionMenu,
                                    onDismissRequest = { showReactionMenu = false },
                                    modifier = Modifier
                                        .background(Color(0xFF13192B))
                                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        reactionsList.forEach { emoji ->
                                            Text(
                                                text = emoji,
                                                fontSize = 18.sp,
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        onReactionSelect?.invoke(emoji)
                                                        showReactionMenu = false
                                                    }
                                                    .padding(3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Copy message
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Text",
                                tint = Color.LightGray.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { onCopyText?.invoke(message.text) }
                            )

                            // AI-specific actions (Speak / PDF export)
                            if (!isUser) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Text",
                                    tint = NeonCyan.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            com.example.util.SpeechAndTtsManager.speak(message.text, context)
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share PDF",
                                    tint = NeonPink.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            com.example.util.FileExportManager.exportPdfSummary(
                                                context = context,
                                                documentTitle = "إجابة ذكاء NEXA AI",
                                                bodyText = message.text
                                            )
                                        }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Media Image Display if present
                    if (message.mediaType == "image" || !message.mediaUrl.isNullOrBlank()) {
                        val imageUrl = message.mediaUrl ?: "https://picsum.photos/500/300"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            coil.compose.AsyncImage(
                                model = imageUrl,
                                contentDescription = "Attached Image",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Message text
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
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clickable { onReactionSelect?.invoke(message.reaction) }
                ) {
                    Text(
                        text = "${message.reaction} 1",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
