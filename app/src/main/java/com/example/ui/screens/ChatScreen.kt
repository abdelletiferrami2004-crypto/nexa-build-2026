package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.User
import com.example.ui.MajarrahViewModel
import com.example.ui.components.AdMobBannerSpace
import com.example.ui.components.AiToolboxModal
import com.example.ui.components.BlueVerificationBadge
import com.example.ui.components.ConversationDetailsModal
import com.example.ui.components.MessengerActiveContactsBar
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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val profileNullable by viewModel.userProfile.collectAsState()
    val profile = profileNullable ?: com.example.data.model.UserProfile()
    val isAiThinking by viewModel.isAiThinking.collectAsState()

    val isVipMember = profile.isVipMember
    val isPinProtectionEnabled = try {
        (profile.isChatPinEnabled && profile.chatPin.isNotBlank()) || PinLockManager.isPinEnabled(context)
    } catch (e: Throwable) {
        false
    }
    val effectivePin = profile.chatPin.ifBlank { PinLockManager.getSavedPin(context).ifBlank { "1234" } }

    var showPinDialog by remember { mutableStateOf(false) }
    var showVipModal by remember { mutableStateOf(false) }
    var showVoiceTutorModal by remember { mutableStateOf(false) }
    var showAiToolboxModal by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var activeCallType by remember { mutableStateOf<String?>(null) } // "audio", "video", or null
    var activeCallTargetName by remember { mutableStateOf("") }
    var activeCallTargetAvatar by remember { mutableStateOf("") }

    // PIN Protection Lock Screen
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

    // VIP Subscription Modal
    if (showVipModal) {
        NexaVipSubscriptionModal(
            isCurrentlyVip = isVipMember,
            onSubscribe = { tier -> viewModel.activateVipSubscription(tier) },
            onDismiss = { showVipModal = false }
        )
    }

    // Voice Tutor Modal
    if (showVoiceTutorModal) {
        VoiceTutorModal(onDismiss = { showVoiceTutorModal = false })
    }

    // AI Toolbox Modal
    if (showAiToolboxModal) {
        AiToolboxModal(onDismiss = { showAiToolboxModal = false })
    }

    // New Chat / Contact Picker Dialog
    if (showNewChatDialog) {
        NewChatContactPickerDialog(
            viewModel = viewModel,
            onDismiss = { showNewChatDialog = false },
            onSelectUser = { selectedUser ->
                showNewChatDialog = false
                viewModel.startConversationWithUser(selectedUser)
            },
            onSelectAi = {
                showNewChatDialog = false
                viewModel.selectConversation("nexa_ai")
            }
        )
    }

    // Encrypted Call Dialog (Audio / Video)
    if (activeCallType != null) {
        EncryptedCallDialog(
            callerName = activeCallTargetName,
            callerAvatar = activeCallTargetAvatar,
            callType = activeCallType ?: "audio",
            onEndCall = { activeCallType = null }
        )
    }

    // MAIN ROUTER: Chats List vs Direct Peer-to-Peer Chat
    val activeConvId = selectedConvId
    if (activeConvId.isNullOrBlank()) {
        // 1. CHATS LIST SCREEN (قائمة المحادثات الرئيسية - WhatsApp Style)
        ChatsListScreen(
            viewModel = viewModel,
            conversations = conversations,
            isVipMember = isVipMember,
            onBackClick = onBackClick,
            onSelectConversation = { convId ->
                viewModel.selectConversation(convId)
            },
            onOpenAiChat = {
                viewModel.selectConversation("nexa_ai")
            },
            onOpenNewChat = {
                showNewChatDialog = true
            },
            onOpenPinLock = {
                viewModel.lockChat()
                showPinDialog = true
            },
            onOpenVip = {
                showVipModal = true
            }
        )
    } else {
        // 2. DIRECT PEER-TO-PEER CHAT SCREEN (دردشة الأصدقاء المباشرة)
        val currentConv = conversations.firstOrNull { it.id == activeConvId }
        val isAiChat = activeConvId == "nexa_ai" || activeConvId == "ai_bot"
        val contactName = if (isAiChat) {
            if (isVipMember) "ذكاء NEXA AI Turbo" else "ذكاء NEXA AI"
        } else {
            currentConv?.contactName ?: "محادثة مشفرة"
        }
        val contactAvatar = if (isAiChat) "" else (currentConv?.contactAvatar ?: "")

        DirectChatScreen(
            viewModel = viewModel,
            conversationId = activeConvId,
            contactName = contactName,
            contactAvatar = contactAvatar,
            isAiChat = isAiChat,
            isAiThinking = isAiThinking,
            isVipMember = isVipMember,
            messages = messages,
            onBackToChatsList = {
                viewModel.selectConversation("")
            },
            onStartCall = { type ->
                activeCallType = type
                activeCallTargetName = contactName
                activeCallTargetAvatar = contactAvatar
            },
            onOpenAiToolbox = { showAiToolboxModal = true },
            onOpenVoiceTutor = { showVoiceTutorModal = true },
            onOpenVip = { showVipModal = true }
        )
    }
}

// =====================================================================
// CHATS LIST SCREEN (قائمة المحادثات الرئيسية - مثل واتساب)
// =====================================================================
@Composable
fun ChatsListScreen(
    viewModel: MajarrahViewModel,
    conversations: List<Conversation>,
    isVipMember: Boolean,
    onBackClick: () -> Unit,
    onSelectConversation: (String) -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenNewChat: () -> Unit,
    onOpenPinLock: () -> Unit,
    onOpenVip: () -> Unit
) {
    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adWatchProgress by viewModel.adWatchProgress.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterCategory by remember { mutableStateOf("all") }
    var showMoreMenu by remember { mutableStateOf(false) }

    val pendingRequestsCount = remember(conversations, blockedUsers) {
        conversations.count { it.isMessageRequest && it.requestStatus == "pending" && !it.isBlocked && !blockedUsers.contains(it.contactName) }
    }

    val filteredConversations = remember(conversations, searchQuery, selectedFilterCategory, blockedUsers) {
        conversations.filter { conv ->
            // Filter out AI conversation from list as it is pinned at top
            if (conv.id == "ai_bot" || conv.id == "nexa_ai") return@filter false

            // Strict blocking filter
            val isBlocked = conv.isBlocked || blockedUsers.contains(conv.contactName)
            if (isBlocked && selectedFilterCategory != "blocked") return@filter false

            val matchesQuery = searchQuery.isBlank() ||
                    conv.contactName.contains(searchQuery, ignoreCase = true) ||
                    conv.lastMessage.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedFilterCategory) {
                "requests" -> conv.isMessageRequest && conv.requestStatus == "pending"
                "unread" -> conv.unreadCount > 0 && (!conv.isMessageRequest || conv.requestStatus == "accepted")
                "favorites" -> (conv.id == "conv_1" || conv.id == "conv_3") && (!conv.isMessageRequest || conv.requestStatus == "accepted")
                "groups" -> (conv.contactName.contains("فريق") || conv.contactName.contains("مجموعة")) && (!conv.isMessageRequest || conv.requestStatus == "accepted")
                else -> !conv.isMessageRequest || conv.requestStatus == "accepted" // "all" shows main accepted inbox
            }

            matchesQuery && matchesCategory
        }
    }

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
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. TOP HEADER APP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B111E).copy(alpha = 0.96f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.07f),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "الدردشات",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EncryptedGreen.copy(alpha = 0.15f))
                                    .border(1.dp, EncryptedGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = EncryptedGreen,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "E2EE",
                                        color = EncryptedGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "تواصل مشفر فائق السرعة • منصة NEXA",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                // Actions: New Chat & More Menu
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onOpenNewChat,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "New Chat",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
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
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("قفل الدردشات بـ PIN", color = Color.White, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    showMoreMenu = false
                                    onOpenPinLock()
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
                                        onOpenVip()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 2. DISCREET COMPACT ADMOB STRIP
            AdMobBannerSpace(
                isVipMember = isVipMember,
                isAdWatching = isAdWatching,
                adWatchProgress = adWatchProgress,
                onWatchRewardedAd = { viewModel.watchRewardedAdForCredits() },
                onGoVip = onOpenVip,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                compact = true
            )

            // 3. SMART SEARCH / ASK NEXA AI BAR (Meta AI style search bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131B2E))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "بحث أو اسأل ذكاء NEXA AI...",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            cursorBrush = SolidColor(NeonCyan),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Ask AI Button when query entered
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                                .clickable {
                                    viewModel.sendAiPrompt(searchQuery)
                                    onOpenAiChat()
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اسأل AI", color = BackgroundDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Glowing Meta-style AI Trigger Icon
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                                .clickable { onOpenAiChat() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = BackgroundDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // 4. MESSENGER STORIES & ACTIVE CONTACTS BAR (نشط الآن / الملاحظات)
            MessengerActiveContactsBar(
                userProfile = userProfile,
                onAddNoteOrStory = { onOpenNewChat() },
                onSelectActiveUser = { targetConvId ->
                    onSelectConversation(targetConvId)
                },
                onOpenAi = onOpenAiChat
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 5. CATEGORY TABS (الكل, طلبات المراسلة, غير المقروءة, المفضلة, المجموعات)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ChatCategoryTabChip(
                        title = "الكل",
                        isSelected = selectedFilterCategory == "all",
                        onClick = { selectedFilterCategory = "all" }
                    )
                }
                item {
                    ChatCategoryTabChip(
                        title = "طلبات المراسلة ${if (pendingRequestsCount > 0) "($pendingRequestsCount)" else ""}",
                        isSelected = selectedFilterCategory == "requests",
                        onClick = { selectedFilterCategory = "requests" }
                    )
                }
                item {
                    val unreadCount = conversations.count { it.unreadCount > 0 && (!it.isMessageRequest || it.requestStatus == "accepted") }
                    ChatCategoryTabChip(
                        title = "غير مقروءة ${if (unreadCount > 0) "($unreadCount)" else ""}",
                        isSelected = selectedFilterCategory == "unread",
                        onClick = { selectedFilterCategory = "unread" }
                    )
                }
                item {
                    ChatCategoryTabChip(
                        title = "المفضلة ⭐",
                        isSelected = selectedFilterCategory == "favorites",
                        onClick = { selectedFilterCategory = "favorites" }
                    )
                }
                item {
                    ChatCategoryTabChip(
                        title = "المجموعات 👥",
                        isSelected = selectedFilterCategory == "groups",
                        onClick = { selectedFilterCategory = "groups" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. CONVERSATIONS LIST (PINNED NEXA AI + FRIENDS CHATS)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // PINNED NEXA AI ASSISTANT CONVERSATION (Always at top)
                item {
                    PinnedNexaAiChatItem(
                        isVip = isVipMember,
                        onClick = onOpenAiChat
                    )
                }

                // Friends / Users Conversations
                if (filteredConversations.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "لا توجد محادثات تطابق البحث",
                                    color = Color.LightGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredConversations) { conv ->
                        ConversationListItem(
                            conversation = conv,
                            onClick = { onSelectConversation(conv.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }

        // FLOATING ACTION BUTTON (NEXA AI Launcher FAB)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .size(56.dp)
                .shadow(16.dp, CircleShape, spotColor = NeonCyan)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            NeonCyan,
                            NeonPurple
                        )
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                .clickable { onOpenAiChat() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "NEXA AI",
                tint = BackgroundDark,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ChatCategoryTabChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f))
            .border(
                1.dp,
                if (isSelected) NeonCyan else Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) NeonCyan else Color.LightGray,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// PINNED NEXA AI CHAT ITEM
@Composable
fun PinnedNexaAiChatItem(
    isVip: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF151833),
                        Color(0xFF1D1B44)
                    )
                )
            )
            .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glowing AI Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                .border(2.dp, NeonCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI",
                tint = BackgroundDark,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isVip) "ذكاء NEXA AI Turbo" else "ذكاء NEXA AI Assistant",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (isVip) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "VIP",
                            tint = NeonAmber,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "مثبت",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "مساعدك الذكي الفوري: اسأل عن أي شيء، تحليل الصور والبرمجة...",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// CONVERSATION LIST ITEM (Friend / User Chat)
@Composable
fun ConversationListItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    val formattedTime = remember(conversation.lastTimestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(conversation.lastTimestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF101726).copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Profile Avatar with Online indicator
        Box(modifier = Modifier.size(48.dp)) {
            if (conversation.contactAvatar.isNotBlank()) {
                AsyncImage(
                    model = conversation.contactAvatar,
                    contentDescription = conversation.contactName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF2563EB),
                                    Color(0xFF7C3AED)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.contactName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            // Green Online Dot
            Box(
                modifier = Modifier
                    .size(11.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(EncryptedGreen)
                    .border(1.5.dp, BackgroundDark, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.contactName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conversation.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        BlueVerificationBadge(size = 14.dp)
                    }
                }

                Text(
                    text = formattedTime,
                    color = if (conversation.unreadCount > 0) EncryptedGreen else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (conversation.unreadCount == 0) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = conversation.lastMessage,
                        color = if (conversation.unreadCount > 0) Color.White else Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                    )
                }

                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(19.dp)
                            .clip(CircleShape)
                            .background(EncryptedGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            color = BackgroundDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

// =====================================================================
// DIRECT PEER-TO-PEER CHAT SCREEN (دردشة الأصدقاء المباشرة)
// =====================================================================
@Composable
fun DirectChatScreen(
    viewModel: MajarrahViewModel,
    conversationId: String,
    contactName: String,
    contactAvatar: String,
    isAiChat: Boolean,
    isAiThinking: Boolean,
    isVipMember: Boolean,
    messages: List<ChatMessage>,
    onBackToChatsList: () -> Unit,
    onStartCall: (String) -> Unit,
    onOpenAiToolbox: () -> Unit,
    onOpenVoiceTutor: () -> Unit,
    onOpenVip: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isListening by com.example.util.SpeechAndTtsManager.isListening.collectAsState()
    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adWatchProgress by viewModel.adWatchProgress.collectAsState()

    // Conversations, Blocking & Message Request States
    val conversations by viewModel.conversations.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val currentConv = remember(conversations, conversationId) {
        conversations.firstOrNull { it.id == conversationId }
    }
    val isBlocked = (currentConv?.isBlocked == true) || blockedUsers.contains(contactName)
    val isMessageRequest = (currentConv?.isMessageRequest == true) && currentConv.requestStatus == "pending"
    val userSentMessagesCount = remember(messages) { messages.count { it.isFromUser } }

    // Real-Time Online & Typing Status
    val peerOnlineMap by viewModel.peerOnlineState.collectAsState()
    val peerTypingMap by viewModel.peerTypingState.collectAsState()
    val isPeerOnline = (!isBlocked) && (peerOnlineMap[conversationId] ?: true)
    val isPeerTyping = (!isBlocked) && ((peerTypingMap[conversationId] == true) || (isAiChat && isAiThinking))

    // Real Audio Recording State
    val isRecordingAudio by com.example.util.AudioRecordManager.isRecording.collectAsState()
    val recordDurationSec by com.example.util.AudioRecordManager.recordDurationSeconds.collectAsState()
    val recordAmplitude by com.example.util.AudioRecordManager.currentAmplitude.collectAsState()

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            com.example.util.AudioRecordManager.startRecording(context)
        }
    }

    var messageText by remember { mutableStateOf("") }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showPhotoMenu by remember { mutableStateOf(false) }
    var selectedImageForZoom by remember { mutableStateOf<String?>(null) }
    var showConversationDetailsModal by remember { mutableStateOf(false) }
    var currentQuickEmoji by remember { mutableStateOf("👍") }
    var customNickname by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.sendImageMessage(conversationId, it.toString(), messageText)
            messageText = ""
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        bmp?.let {
            if (isAiChat) {
                viewModel.attachImageForAi(it)
            }
            try {
                val file = java.io.File(context.cacheDir, "nexa_cam_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { out ->
                    it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                viewModel.sendImageMessage(conversationId, file.absolutePath, messageText)
            } catch (e: Throwable) {
                viewModel.sendImageMessage(conversationId, "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop", messageText)
            }
            messageText = ""
        }
    }

    if (selectedImageForZoom != null) {
        Dialog(onDismissRequest = { selectedImageForZoom = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable { selectedImageForZoom = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = selectedImageForZoom,
                    contentDescription = "Zoomed Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

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
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. MODERN MESSENGER CHAT APP BAR (Contact Details, Active Now, Calls & Info)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B111E).copy(alpha = 0.98f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button + (Avatar + Name & Active Now) - Clickable to open Profile Details Modal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBackToChatsList,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Contact Avatar + Online Dot + Info Trigger
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showConversationDetailsModal = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Box(modifier = Modifier.size(40.dp)) {
                            if (isBlocked) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color(0xFF33161C))
                                        .border(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Blocked",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else if (isAiChat) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                                        .border(1.5.dp, NeonCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = BackgroundDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else if (contactAvatar.isNotBlank()) {
                                AsyncImage(
                                    model = contactAvatar,
                                    contentDescription = contactName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(1.5.dp, NeonCyan.copy(alpha = 0.6f), CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    Color(0xFF2563EB),
                                                    Color(0xFF7C3AED)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contactName.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            // Green Active Now badge (Hidden if Blocked)
                            if (!isBlocked && (isPeerOnline || isAiChat)) {
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(EncryptedGreen)
                                        .border(1.5.dp, BackgroundDark, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            val displayName = if (customNickname.isNotBlank()) customNickname else contactName
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBlocked) "$displayName (محظور)" else displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isBlocked) Color.Gray else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (isAiChat && isVipMember) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = "VIP",
                                        tint = NeonAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                } else if (!isAiChat && !isBlocked) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    BlueVerificationBadge(size = 14.dp)
                                }
                            }
                            if (isBlocked) {
                                Text(
                                    text = "حساب محظور • غير متاح",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFEF4444).copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            } else if (isPeerTyping) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "يكتب الآن...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (isAiChat) {
                                Text(
                                    text = "نشط الآن • NEXA AI 🔒",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            } else if (isPeerOnline) {
                                Text(
                                    text = "نشط الآن • مشفر 🔒",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EncryptedGreen,
                                    fontSize = 10.sp
                                )
                            } else {
                                Text(
                                    text = "نشط منذ قليل",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Call & Conversation Info / Settings Action Buttons (REMOVED completely from NEXA AI & Blocked Users)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (!isAiChat && !isBlocked) {
                        // Voice Call Action (Phone icon)
                        IconButton(
                            onClick = { onStartCall("audio") },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Voice Call",
                                tint = NeonPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Video Call Action (Camcorder icon)
                        IconButton(
                            onClick = { onStartCall("video") },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video Call",
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Conversation Info & Details Modal (Info / Settings icon)
                    IconButton(
                        onClick = { showConversationDetailsModal = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Conversation Details",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
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
                                        Text("مشاركة رابط الغرفة", color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    showMoreMenu = false
                                    clipboardManager.setText(AnnotatedString("https://nexa.chat/room/$conversationId"))
                                }
                            )
                            if (isBlocked) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("إلغاء الحظر", color = NeonCyan, fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.unblockUser(contactName, conversationId)
                                    }
                                )
                            } else if (!isAiChat) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("حظر المستخدم", color = Color(0xFFEF4444), fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.blockUser(contactName, conversationId)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Details Modal
            if (showConversationDetailsModal) {
                ConversationDetailsModal(
                    contactName = if (customNickname.isNotBlank()) customNickname else contactName,
                    contactAvatar = contactAvatar,
                    isOnline = isPeerOnline,
                    isAiChat = isAiChat,
                    currentQuickEmoji = currentQuickEmoji,
                    currentNickname = customNickname,
                    onDismiss = { showConversationDetailsModal = false },
                    onStartAudioCall = {
                        showConversationDetailsModal = false
                        onStartCall("audio")
                    },
                    onStartVideoCall = {
                        showConversationDetailsModal = false
                        onStartCall("video")
                    },
                    onQuickEmojiChanged = { newEmoji ->
                        currentQuickEmoji = newEmoji
                    },
                    onNicknameChanged = { newNickname ->
                        customNickname = newNickname
                    },
                    onDisappearingMessagesToggle = { mode ->
                        // Disappearing messages set
                    },
                    onBlockUser = {
                        viewModel.blockUser(contactName, conversationId)
                        showConversationDetailsModal = false
                    },
                    onReportUser = {
                        viewModel.reportContent("chat_$conversationId", "إبلاغ عن محادثة")
                        showConversationDetailsModal = false
                    },
                    onClearChat = {
                        // Clear chat
                    }
                )
            }

            // 2. COMPACT ADMOB STRIP (Completely REMOVED from NEXA AI & Blocked chats)
            if (!isAiChat && !isBlocked) {
                AdMobBannerSpace(
                    isVipMember = isVipMember,
                    isAdWatching = isAdWatching,
                    adWatchProgress = adWatchProgress,
                    onWatchRewardedAd = { viewModel.watchRewardedAdForCredits() },
                    onGoVip = onOpenVip,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                    compact = true
                )
            }

            // 3. AI QUICK PROMPTS (Only in AI Chat)
            if (isAiChat) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        AiQuickPromptChip(text = "💡 أفكار إبداعية", icon = Icons.Default.Lightbulb) {
                            messageText = "اقترح لي 3 أفكار إبداعية لمشروع تقني جديد"
                        }
                    }
                    item {
                        AiQuickPromptChip(text = "🎨 توليد صورة", icon = Icons.Default.Image) {
                            messageText = "صمم صورة ثلاثية الأبعاد خيالية لمدينة ذكية عام 2030"
                        }
                    }
                    item {
                        AiQuickPromptChip(text = "🧠 تحليل ذكي", icon = Icons.Default.Psychology) {
                            messageText = "اشرح لي باختصار كيف تعمل خوارزميات التشفير E2EE"
                        }
                    }
                    item {
                        AiQuickPromptChip(text = "🛠️ أدوات AI", icon = Icons.Default.Build) {
                            onOpenAiToolbox()
                        }
                    }
                }
            }

            // 4. CHAT MESSAGES CANVAS (MAXIMIZED AREA)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isAiChat) NeonCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAiChat) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isAiChat) NeonCyan else EncryptedGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isAiChat) "مرحباً بك في دردشة NEXA AI" else "بدء المحادثة المشفرة مع $contactName",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "جميع الرسائل والوسائط في هذه الغرفة محمية بنظام التشفير التام 256-bit E2EE.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 6.dp, bottom = 8.dp)
                    ) {
                        // Incoming Message Request Banner
                        if (isMessageRequest) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.MarkEmailUnread,
                                                contentDescription = null,
                                                tint = NeonCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "طلب مراسلة وارد",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "هذا المستخدم ليس في قائمة أصدقائك. يُسمح لغير الأصدقاء بإرسال رسالة واحدة فقط حتى تقبل الطلب.",
                                            color = Color.LightGray,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.acceptMessageRequest(conversationId) },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("قبول", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.ignoreMessageRequest(conversationId)
                                                    onBackToChatsList()
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("تجاهل", color = Color.White, fontSize = 12.sp)
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.blockUser(contactName, conversationId)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("حظر", color = Color.White, fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // E2EE Security Badge Divider
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
                                        text = "الرسائل مشفرة تماماً من طرف إلى طرف (E2EE)",
                                        color = Color.LightGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        items(messages) { msg ->
                            DirectChatMessageBubble(
                                message = msg,
                                isAiChat = isAiChat,
                                isVip = isVipMember,
                                onReactionSelect = { reaction ->
                                    viewModel.toggleMessageReaction(msg, reaction)
                                },
                                onCopyText = { text ->
                                    clipboardManager.setText(AnnotatedString(text))
                                },
                                onImageClick = { imgUrl ->
                                    selectedImageForZoom = imgUrl
                                }
                            )
                        }

                        if (isAiThinking) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFF131A33))
                                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = NeonCyan,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "NEXA AI يفكر في الإجابة...",
                                                color = NeonCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. LIVE VOICE LISTENING PILL
            AnimatedVisibility(
                visible = isListening,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonPink.copy(alpha = 0.2f))
                        .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("جاري الاستماع لصوتك... تحدث الآن", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 6. MODERN FLOATING INPUT DOCK WITH RESTRICTION CARDS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                if (isBlocked) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1218)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تم حظر هذا الحساب بالكامل 🚫",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "لا يمكن تبادل الرسائل أو الاتصال أو رؤية حالة الاتصال والصورة الشخصية.",
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.unblockUser(contactName, conversationId) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, NeonCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("إلغاء الحظر", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                } else if (isMessageRequest && userSentMessagesCount >= 1) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF191F30)),
                        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تم إرسال رسالتك الأولى 📩. بانتظار قبول الطرف الآخر لطلب المراسلة لتتمكن من إرسال المزيد من الرسائل والمكالمات.",
                                color = NeonAmber,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else if (isRecordingAudio) {
                    // LIVE REAL AUDIO RECORDING BAR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = NeonPink.copy(alpha = 0.4f))
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color(0xFF161026).copy(alpha = 0.98f))
                            .border(1.dp, NeonPink.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Cancel Button
                        IconButton(
                            onClick = {
                                com.example.util.AudioRecordManager.cancelRecording()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Cancel Recording",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Pulsing Red Dot + Timer + Animated Amplitude Waveform
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )

                            val minutes = recordDurationSec / 60
                            val seconds = recordDurationSec % 60
                            Text(
                                text = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            // Live oscillating waveform bars
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val normalizedAmp = (recordAmplitude / 5000f).coerceIn(0.1f, 1f)
                                listOf(10, 18, 26, 14, 22, 28, 16, 20, 12).forEachIndexed { i, baseH ->
                                    val dynamicHeight = (baseH * (0.6f + normalizedAmp * 0.8f)).coerceIn(6f, 30f)
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(dynamicHeight.dp)
                                            .clip(RoundedCornerShape(1.5.dp))
                                            .background(if (i % 2 == 0) NeonPink else NeonCyan)
                                    )
                                }
                            }
                        }

                        // Send Voice Note Button
                        IconButton(
                            onClick = {
                                val (audioFile, duration) = com.example.util.AudioRecordManager.stopRecording()
                                viewModel.sendVoiceMessage(conversationId, audioFile?.absolutePath, duration)
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Voice Note",
                                tint = BackgroundDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = NeonCyan.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color(0xFF111726).copy(alpha = 0.96f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(26.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Attachment Action (Gallery / Camera)
                        Box {
                            IconButton(
                                onClick = { showPhotoMenu = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Attach Media",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
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

                        Spacer(modifier = Modifier.width(2.dp))

                        // Voice Input / Record Voice Note Button
                        IconButton(
                            onClick = {
                                if (androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.RECORD_AUDIO
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                ) {
                                    com.example.util.AudioRecordManager.startRecording(context)
                                } else {
                                    audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Record Audio",
                                tint = NeonPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Text Input Field
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (messageText.isEmpty()) {
                                Text(
                                    text = if (isAiChat) "اكتب رسالتك لـ NEXA AI..." else "اكتب رسالتك لـ $contactName...",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                            BasicTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                cursorBrush = SolidColor(NeonCyan),
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Send / Quick Emoji Action Button (NEXA AI has NO quick emoji button, pure clean input)
                        if (messageText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    viewModel.sendChatMessage(conversationId, messageText)
                                    messageText = ""
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(NeonCyan, Color(0xFF0084FF)))
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else if (!isAiChat) {
                            // Messenger 1-Tap Quick Emoji Send Button (Only for human chats, NOT AI)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF0084FF).copy(alpha = 0.25f),
                                                Color(0xFF00C6FF).copy(alpha = 0.25f)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color(0xFF00C6FF).copy(alpha = 0.4f), CircleShape)
                                    .clickable {
                                        viewModel.sendChatMessage(conversationId, currentQuickEmoji)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentQuickEmoji,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiQuickPromptChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131B2E).copy(alpha = 0.85f))
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

// DIRECT CHAT MESSAGE BUBBLE ITEM
@Composable
fun DirectChatMessageBubble(
    message: ChatMessage,
    isAiChat: Boolean,
    isVip: Boolean = false,
    onReactionSelect: ((String) -> Unit)? = null,
    onCopyText: ((String) -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val isUser = message.isFromUser
    var showReactionMenu by remember { mutableStateOf(false) }

    val playingMessageId by com.example.util.AudioPlaybackManager.currentPlayingMessageId.collectAsState()
    val isPlayingAudio by com.example.util.AudioPlaybackManager.isPlaying.collectAsState()
    val playbackProgress by com.example.util.AudioPlaybackManager.playbackProgress.collectAsState()
    val currentPosSec by com.example.util.AudioPlaybackManager.currentPositionSec.collectAsState()
    val totalDurSec by com.example.util.AudioPlaybackManager.totalDurationSec.collectAsState()
    val isThisPlaying = (playingMessageId == message.id && isPlayingAudio)

    val reactionsList = listOf("❤️", "👍", "🔥", "😂", "😮", "👏")

    val formattedTime = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Small Sender Avatar next to incoming messages (Messenger Style)
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(end = 6.dp, bottom = 2.dp)
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAiChat) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = BackgroundDark,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                } else if (message.senderAvatar.isNotBlank()) {
                    AsyncImage(
                        model = message.senderAvatar,
                        contentDescription = message.senderName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF2563EB),
                                        Color(0xFF7C3AED)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.senderName.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .shadow(
                        elevation = if (isUser) 4.dp else 2.dp,
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        spotColor = if (isUser) Color(0xFF0084FF).copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.4f)
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .background(
                        if (isUser) {
                            // Vibrant Messenger Blue/Cyan Gradient
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF0078FF),
                                    Color(0xFF00B4D8)
                                )
                            )
                        } else {
                            // Soft Neutral Dark Gray / Slate Glassmorphic
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1E293B).copy(alpha = 0.95f),
                                    Color(0xFF242C3D).copy(alpha = 0.95f)
                                )
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isUser) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Bubble Header: Sender & Action icons
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
                            // Reaction button
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

                            // AI-specific Speech Synthesis
                            if (!isUser && isAiChat) {
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
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // MEDIA: Image Attached
                    if (message.mediaType == "image" || !message.mediaUrl.isNullOrBlank()) {
                        val imageUrl = message.mediaUrl ?: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .clickable { onImageClick?.invoke(imageUrl) }
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Attached Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // MEDIA: Voice Note
                    if (message.mediaType == "voice") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.25f))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    com.example.util.AudioPlaybackManager.togglePlay(
                                        context = context,
                                        messageId = message.id,
                                        audioPathOrUrl = message.mediaUrl,
                                        fallbackDurationSec = 14
                                    )
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isUser) EncryptedGreen else NeonCyan)
                            ) {
                                Icon(
                                    imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Voice",
                                    tint = BackgroundDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Waveform visualizer effect with progress coloring
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(8, 14, 22, 10, 18, 24, 16, 12, 20, 26, 14, 18, 10, 22, 16, 12)
                                heights.forEachIndexed { index, h ->
                                    val barFraction = index.toFloat() / heights.size
                                    val isPassed = isThisPlaying && barFraction <= playbackProgress
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(h.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                if (isPassed) NeonPink else Color.White.copy(alpha = 0.7f)
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            val displaySec = if (isThisPlaying) currentPosSec else (if (totalDurSec > 0) totalDurSec else 14)
                            val displayMin = displaySec / 60
                            val displayS = displaySec % 60
                            Text(
                                text = String.format(java.util.Locale.getDefault(), "%d:%02d", displayMin, displayS),
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
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

                    Spacer(modifier = Modifier.height(4.dp))

                    // Timestamp & Delivery Checkmarks (✓✓)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            color = Color.LightGray.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                        if (isUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            when (message.deliveryStatus) {
                                "sending" -> {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Sending",
                                        tint = Color.LightGray.copy(alpha = 0.6f),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                "sent" -> {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Sent",
                                        tint = Color.LightGray.copy(alpha = 0.8f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                "delivered" -> {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Delivered",
                                        tint = Color.LightGray.copy(alpha = 0.85f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                else -> { // "read"
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Read",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Emoji Reaction Badge
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

// =====================================================================
// NEW CHAT CONTACT PICKER DIALOG (بدء محادثة جديدة)
// =====================================================================
@Composable
fun NewChatContactPickerDialog(
    viewModel: MajarrahViewModel,
    onDismiss: () -> Unit,
    onSelectUser: (User) -> Unit,
    onSelectAi: () -> Unit
) {
    var searchInput by remember { mutableStateOf("") }

    val sampleContacts = remember {
        listOf(
            User(id = 201, name = "سارة النمر", username = "sara_alnemer", bio = "مصممة جرافيك ونيون سينمائي في NEXA", followersCount = 45000, avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop"),
            User(id = 202, name = "عبدالعزيز الماجد", username = "abdulaziz_majed", bio = "مطور ومبتكر تطبيقات مجرة", followersCount = 1250000, avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop"),
            User(id = 203, name = "فيصل العتيبي", username = "faisal_otaibi", bio = "مهندس برمجيات ومهتم بـ Jetpack Compose", followersCount = 89000, avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop"),
            User(id = 204, name = "نورا القحطاني", username = "noura_qahtani", bio = "رائدة أعمال وصانعة محتوى رقمي", followersCount = 320000, avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop"),
            User(id = 205, name = "ريم الشمري", username = "reem_alshammari", bio = "مهتمة بالواقع المعزز وتطبيقات الذكاء", followersCount = 67000, avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop"),
            User(id = 206, name = "عبدالله الشهري", username = "abdullah_shehri", bio = "متخصص أمن سيبراني وتشفير E2EE", followersCount = 110000, avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150&auto=format&fit=crop")
        )
    }

    val filteredContacts = remember(searchInput, sampleContacts) {
        if (searchInput.isBlank()) sampleContacts
        else sampleContacts.filter {
            it.name.contains(searchInput, ignoreCase = true) ||
                    it.username.contains(searchInput, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF10172A),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "محادثة جديدة 💬",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search user bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF172036))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchInput.isEmpty()) {
                                Text("ابحث بالاسم أو اسم المستخدم...", color = Color.Gray, fontSize = 12.sp)
                            }
                            BasicTextField(
                                value = searchInput,
                                onValueChange = {
                                    searchInput = it
                                    viewModel.searchUserByName(it)
                                },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                cursorBrush = SolidColor(NeonCyan),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fast AI Assistant Start Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.15f), NeonPurple.copy(alpha = 0.15f))))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onSelectAi() }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("ذكاء NEXA AI Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("مساعد الذكاء الاصطناعي الفوري", color = NeonCyan, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("جهات الاتصال والأصدقاء:", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredContacts) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .clickable { onSelectUser(user) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!user.avatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = user.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(user.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("@${user.username}", color = Color.Gray, fontSize = 10.sp)
                            }

                            Icon(
                                imageVector = Icons.Default.AddComment,
                                contentDescription = "Chat",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// ENCRYPTED ACTIVE CALL DIALOG (مكالمة صوتية / فيديو مشفرة E2EE)
// =====================================================================
@Composable
fun EncryptedCallDialog(
    callerName: String,
    callerAvatar: String,
    callType: String,
    onEndCall: () -> Unit
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    val formattedDuration = remember(callSeconds) {
        val mins = callSeconds / 60
        val secs = callSeconds % 60
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }

    Dialog(onDismissRequest = onEndCall) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0B1020),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, if (callType == "video") NeonCyan else NeonPink, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Call E2EE Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(EncryptedGreen.copy(alpha = 0.15f))
                        .border(1.dp, EncryptedGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مكالمة مشفرة 256-bit E2EE", color = EncryptedGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Caller Avatar / Video Preview
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(2.dp, if (callType == "video") NeonCyan else NeonPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (callerAvatar.isNotBlank()) {
                        AsyncImage(
                            model = callerAvatar,
                            contentDescription = callerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = if (callType == "video") Icons.Default.Videocam else Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = callerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (callType == "video") "مكالمة فيديو مباشرة • $formattedDuration" else "مكالمة صوتية عالية النقاوة • $formattedDuration",
                    color = NeonCyan,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Call Controls: Mute, Camera toggle, End Call
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) NeonPink else Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // End Call Button (Big Red)
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Video Toggle Button
                    if (callType == "video") {
                        IconButton(
                            onClick = { isVideoOff = !isVideoOff },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isVideoOff) NeonAmber else Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Speaker toggle
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speaker",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
