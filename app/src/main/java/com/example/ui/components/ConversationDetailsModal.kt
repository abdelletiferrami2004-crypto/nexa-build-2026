package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

/**
 * Modern Facebook/Messenger style Conversation Info & Profile Details Screen
 */
@Composable
fun ConversationDetailsModal(
    contactName: String,
    contactAvatar: String,
    isOnline: Boolean,
    isAiChat: Boolean,
    currentQuickEmoji: String = "👍",
    currentNickname: String = "",
    onDismiss: () -> Unit,
    onStartAudioCall: () -> Unit,
    onStartVideoCall: () -> Unit,
    onQuickEmojiChanged: (String) -> Unit = {},
    onNicknameChanged: (String) -> Unit = {},
    onDisappearingMessagesToggle: (String) -> Unit = {},
    onBlockUser: () -> Unit = {},
    onReportUser: () -> Unit = {},
    onClearChat: () -> Unit = {}
) {
    var isMuted by remember { mutableStateOf(false) }
    var showEmojiPickerDialog by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showDisappearingDialog by remember { mutableStateOf(false) }
    var showBlockConfirmDialog by remember { mutableStateOf(false) }
    var showReportConfirmDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var nicknameInput by remember { mutableStateOf(currentNickname) }
    var activeDisappearingTime by remember { mutableStateOf("متوقف") }

    val emojis = listOf("👍", "❤️", "🔥", "😂", "⚡", "👏", "🎉", "💯", "🚀", "🌟")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090D16))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B111E).copy(alpha = 0.98f))
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "معلومات المحادثة",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                // Scrollable Details Body
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Centered Profile Header
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Large Avatar with Online Green Dot
                            Box(
                                modifier = Modifier.size(92.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isAiChat) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                                            .border(2.5.dp, NeonCyan, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "AI",
                                            tint = BackgroundDark,
                                            modifier = Modifier.size(44.dp)
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
                                            .border(2.5.dp, NeonCyan.copy(alpha = 0.6f), CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = contactName.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 36.sp
                                        )
                                    }
                                }

                                // Online Status Dot
                                if (isOnline || isAiChat) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(EncryptedGreen)
                                            .border(2.dp, Color(0xFF090D16), CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Contact Name
                            Text(
                                text = if (currentNickname.isNotBlank()) "$contactName ($currentNickname)" else contactName,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            // Online Subtitle
                            Text(
                                text = if (isAiChat) "مساعد ذكي مدعوم بـ Gemini • نشط دائماً" else if (isOnline) "نشط الآن • متصل بالشبكة" else "غير متصل حالياً",
                                color = if (isOnline || isAiChat) EncryptedGreen else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // E2EE End-to-End Encryption Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(EncryptedGreen.copy(alpha = 0.12f))
                                    .border(1.dp, EncryptedGreen.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = EncryptedGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مشفرة تماماً بين الطرفين (E2EE 256-bit) 🔒",
                                    color = EncryptedGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // 2. Quick Circular Action Buttons Row (Messenger Style)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            QuickCircularActionButton(
                                icon = Icons.Default.Call,
                                label = "اتصال صوتي",
                                tint = NeonCyan,
                                onClick = {
                                    onDismiss()
                                    onStartAudioCall()
                                }
                            )

                            QuickCircularActionButton(
                                icon = Icons.Default.Videocam,
                                label = "فيديو",
                                tint = NeonPink,
                                onClick = {
                                    onDismiss()
                                    onStartVideoCall()
                                }
                            )

                            QuickCircularActionButton(
                                icon = if (isMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                                label = if (isMuted) "إلغاء الكتم" else "كتم",
                                tint = if (isMuted) NeonAmber else Color.White,
                                isHighlighted = isMuted,
                                onClick = { isMuted = !isMuted }
                            )

                            QuickCircularActionButton(
                                icon = Icons.Default.Search,
                                label = "بحث",
                                tint = Color.LightGray,
                                onClick = { onDismiss() }
                            )
                        }
                    }

                    // 3. Customization Section
                    item {
                        MessengerSectionHeader(title = "التخصيص")
                        Spacer(modifier = Modifier.height(6.dp))

                        MessengerActionCard {
                            MessengerActionRowItem(
                                icon = Icons.Default.ThumbUp,
                                iconTint = NeonCyan,
                                title = "الرمز التعبيري السريع",
                                trailingContent = {
                                    Text(
                                        text = currentQuickEmoji,
                                        fontSize = 20.sp
                                    )
                                },
                                onClick = { showEmojiPickerDialog = true }
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.Edit,
                                iconTint = NeonPurple,
                                title = "الأسماء المستعارة (Nicknames)",
                                subtitle = if (currentNickname.isNotBlank()) currentNickname else "تحديد كنية للمحادثة",
                                onClick = { showNicknameDialog = true }
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.ColorLens,
                                iconTint = NeonPink,
                                title = "سمة الدردشة والألوان",
                                subtitle = "النيون السيبراني الافتراضي (NEXA Glow)",
                                onClick = {}
                            )
                        }
                    }

                    // 4. Actions Section
                    item {
                        MessengerSectionHeader(title = "الإجراءات والمشاركة")
                        Spacer(modifier = Modifier.height(6.dp))

                        MessengerActionCard {
                            MessengerActionRowItem(
                                icon = Icons.Default.Image,
                                iconTint = NeonCyan,
                                title = "الوسائط والملفات المشتركة",
                                subtitle = "عرض الصور والروابط والمقاطع الصوتية",
                                onClick = {}
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.GroupAdd,
                                iconTint = NeonAmber,
                                title = "إنشاء محادثة جماعية مع $contactName",
                                onClick = {}
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.Share,
                                iconTint = Color.LightGray,
                                title = "مشاركة جهة الاتصال",
                                onClick = {}
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.MarkEmailUnread,
                                iconTint = EncryptedGreen,
                                title = "تعيين كغير مقروءة",
                                onClick = { onDismiss() }
                            )
                        }
                    }

                    // 5. Privacy & Support Section
                    item {
                        MessengerSectionHeader(title = "الخصوصية والأمان")
                        Spacer(modifier = Modifier.height(6.dp))

                        MessengerActionCard {
                            MessengerActionRowItem(
                                icon = Icons.Default.Timer,
                                iconTint = NeonAmber,
                                title = "الرسائل ذاتية الاختفاء",
                                subtitle = "الحالة الحالية: $activeDisappearingTime",
                                onClick = { showDisappearingDialog = true }
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.Shield,
                                iconTint = EncryptedGreen,
                                title = "التحقق من التشفير التام",
                                subtitle = "مفاتيح التشفير متطابقة وآمنة",
                                onClick = {}
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.Delete,
                                iconTint = Color(0xFFEF4444),
                                title = "مسح سجل الرسائل",
                                titleColor = Color(0xFFEF4444),
                                onClick = { showClearConfirmDialog = true }
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.Block,
                                iconTint = Color(0xFFEF4444),
                                title = "حظر $contactName",
                                titleColor = Color(0xFFEF4444),
                                onClick = { showBlockConfirmDialog = true }
                            )

                            Divider(color = Color.White.copy(alpha = 0.07f), thickness = 0.8.dp)

                            MessengerActionRowItem(
                                icon = Icons.Default.Report,
                                iconTint = Color(0xFFF97316),
                                title = "إبلاغ عن محادثة أو إساءة",
                                titleColor = Color(0xFFF97316),
                                onClick = { showReportConfirmDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // Emoji Picker Dialog
    if (showEmojiPickerDialog) {
        AlertDialog(
            onDismissRequest = { showEmojiPickerDialog = false },
            title = {
                Text(
                    text = "اختر الرمز التعبيري السريع",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    emojis.take(5).forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onQuickEmojiChanged(emoji)
                                    showEmojiPickerDialog = false
                                }
                                .padding(6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEmojiPickerDialog = false }) {
                    Text("إلغاء", color = NeonCyan)
                }
            },
            containerColor = Color(0xFF13192B),
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Nickname Dialog
    if (showNicknameDialog) {
        AlertDialog(
            onDismissRequest = { showNicknameDialog = false },
            title = {
                Text(
                    text = "تعيين الاسم المستعار",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "الاسم المستعار يظهر لك فقط في هذه المحادثة.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = nicknameInput,
                        onValueChange = { nicknameInput = it },
                        placeholder = { Text("أدخل الاسم المستعار...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onNicknameChanged(nicknameInput.trim())
                        showNicknameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("حفظ", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNicknameDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF13192B),
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Disappearing Messages Dialog
    if (showDisappearingDialog) {
        val options = listOf("متوقف", "24 ساعة", "7 أيام", "90 يوماً")
        AlertDialog(
            onDismissRequest = { showDisappearingDialog = false },
            title = {
                Text(
                    text = "الرسائل ذاتية الاختفاء ⏱️",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "عند التفعيل، ستختفي الرسائل الجديدة تلقائياً بعد المدة المحددة.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    options.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (activeDisappearingTime == opt) NeonAmber.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                                .clickable {
                                    activeDisappearingTime = opt
                                    onDisappearingMessagesToggle(opt)
                                    showDisappearingDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opt,
                                color = if (activeDisappearingTime == opt) NeonAmber else Color.White,
                                fontWeight = if (activeDisappearingTime == opt) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                            if (activeDisappearingTime == opt) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDisappearingDialog = false }) {
                    Text("إغلاق", color = NeonCyan)
                }
            },
            containerColor = Color(0xFF13192B),
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Clear Chat Confirm Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("مسح المحادثة", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في حذف جميع الرسائل في هذه المحادثة؟", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearChat()
                        showClearConfirmDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("مسح", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF13192B),
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Block User Dialog
    if (showBlockConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBlockConfirmDialog = false },
            title = { Text("حظر الحساب", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("لن يتمكن هذا المستخدم من مراسلتك أو الاتصال بك بعد الآن.", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        onBlockUser()
                        showBlockConfirmDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("حظر", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockConfirmDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF13192B),
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Report Dialog
    if (showReportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showReportConfirmDialog = false },
            title = { Text("إبلاغ عن محتوى", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("سيتم إرسال تقرير أمني لفريق الدعم لمراجعة السلوك وحماية المجتمع الرقمي.", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        onReportUser()
                        showReportConfirmDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
                ) {
                    Text("إرسال الإبلاغ", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportConfirmDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF13192B),
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun QuickCircularActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isHighlighted) tint.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                .border(1.dp, if (isHighlighted) tint else Color.White.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MessengerSectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF94A3B8),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
fun MessengerActionCard(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111726).copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
fun MessengerActionRowItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    titleColor: Color = Color.White,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (trailingContent != null) {
            trailingContent()
        }
    }
}
