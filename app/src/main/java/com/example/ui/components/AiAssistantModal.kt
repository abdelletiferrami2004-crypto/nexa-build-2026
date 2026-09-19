package com.example.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.MajarrahViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.util.SpeechAndTtsManager

@Composable
fun AiAssistantModal(
    viewModel: MajarrahViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val attachedBitmap by viewModel.attachedImageBitmap.collectAsState()
    val isAutoReadTts by viewModel.isAutoReadTtsEnabled.collectAsState()

    // Model and Role State
    val selectedModel by viewModel.selectedAiModel.collectAsState()
    val selectedRole by viewModel.selectedAiRole.collectAsState()
    val isSearchGrounding by viewModel.isSearchGroundingEnabled.collectAsState()
    val isMapsGrounding by viewModel.isMapsGroundingEnabled.collectAsState()
    val isTranscribing by viewModel.isTranscribingAudio.collectAsState()

    val isSpeaking by SpeechAndTtsManager.isSpeaking.collectAsState()
    val isListening by SpeechAndTtsManager.isListening.collectAsState()

    var userPromptText by remember { mutableStateOf("") }
    var showPhotoPickerMenu by remember { mutableStateOf(false) }
    var showModelMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }
    var showVoiceTutorModal by remember { mutableStateOf(false) }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                viewModel.attachImageForAi(bitmap)
            } catch (e: Throwable) {
                Log.e("AiAssistantModal", "Error loading image", e)
                Toast.makeText(context, "تعذر تحميل الصورة المختارة", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.attachImageForAi(it)
        }
    }

    if (showVoiceTutorModal) {
        VoiceTutorModal(
            onDismiss = { showVoiceTutorModal = false }
        )
    }

    val quickPrompts = listOf(
        "⚡ تحليل صورة وسؤال متعدد الوسائط",
        "🌐 ابحث عن أحدث أخبار الذكاء الاصطناعي اليوم",
        "📍 أين أقرب مطعم قهوة مميز؟",
        "💻 اكتب كود بايثون لمعالجة البيانات",
        "🛍️ اقترح منتجات تقنية من متجر التطبيق"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = BackgroundDark.copy(alpha = 0.98f),
            modifier = Modifier
                .fillMaxWidth()
                .height(640.dp)
                .border(1.dp, NeonPurple.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
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
                                .background(NeonPurple.copy(alpha = 0.3f))
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("ذكاء NEXA AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = selectedModel,
                                        color = NeonCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text("دردشة متعددة الأدوار ووسائط متقدمة", color = Color.Gray, fontSize = 10.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Live Voice Tutor Modal Launcher
                        IconButton(
                            onClick = { showVoiceTutorModal = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Live Voice Audio",
                                tint = NeonPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // TTS Auto-Read Toggle
                        IconButton(
                            onClick = { viewModel.toggleAutoReadTts() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isAutoReadTts) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "TTS Toggle",
                                tint = if (isAutoReadTts) NeonCyan else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                SpeechAndTtsManager.stopSpeaking()
                                SpeechAndTtsManager.stopListening()
                                onDismiss()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AI Engine Configuration Bar: Model & Role & Grounding Selectors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF131A2B))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Model Dropdown Selector
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable { showModelMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (selectedModel) {
                                    "gemini-3.1-pro-preview" -> "Pro Preview"
                                    "gemini-3.1-flash-lite" -> "Flash Lite"
                                    else -> "Flash 3.5"
                                },
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showModelMenu,
                            onDismissRequest = { showModelMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("gemini-3.5-flash (افتراضي)", fontWeight = FontWeight.Bold)
                                        Text("متوازن وسريع للمهام العامة ودعم البحث والخرائط", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    viewModel.selectAiModel("gemini-3.5-flash")
                                    showModelMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("gemini-3.1-pro-preview", fontWeight = FontWeight.Bold)
                                        Text("تفكير عميق للمهام البرمجية والتحليل المعقد", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    viewModel.selectAiModel("gemini-3.1-pro-preview")
                                    showModelMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("gemini-3.1-flash-lite", fontWeight = FontWeight.Bold)
                                        Text("خفيف جداً وسرعة استجابة فائقة للدردشة السريعة", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    viewModel.selectAiModel("gemini-3.1-flash-lite")
                                    showModelMenu = false
                                }
                            )
                        }
                    }

                    // Role / System Instruction Dropdown Selector
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable { showRoleMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (selectedRole) {
                                    "developer" -> "💻 مبرمج"
                                    "shopping" -> "🛍️ تسوق"
                                    "creative" -> "✍️ إبداع"
                                    else -> "🌟 عام"
                                },
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🌟 عام • General Assistant") },
                                onClick = {
                                    viewModel.selectAiRole("general")
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("💻 خبير برمجة • Coding Architect") },
                                onClick = {
                                    viewModel.selectAiRole("developer")
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🛍️ مستشار تسوق • Smart Shopping") },
                                onClick = {
                                    viewModel.selectAiRole("shopping")
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("✍️ كاتب ومصمم • Creative Content") },
                                onClick = {
                                    viewModel.selectAiRole("creative")
                                    showRoleMenu = false
                                }
                            )
                        }
                    }

                    // Search Grounding Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSearchGrounding) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                            .border(1.dp, if (isSearchGrounding) NeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleSearchGrounding() }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = if (isSearchGrounding) NeonCyan else Color.Gray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "بحث",
                                color = if (isSearchGrounding) NeonCyan else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = if (isSearchGrounding) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Maps Grounding Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMapsGrounding) NeonPink.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                            .border(1.dp, if (isMapsGrounding) NeonPink else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleMapsGrounding() }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = if (isMapsGrounding) NeonPink else Color.Gray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "خرائط",
                                color = if (isMapsGrounding) NeonPink else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = if (isMapsGrounding) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Suggestions horizontal chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 6.dp)
                ) {
                    items(quickPrompts) { prompt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(50))
                                .clickable {
                                    viewModel.sendAiPrompt(prompt)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(prompt, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Messages Chat List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(aiMessages) { msg ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (msg.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.88f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 18.dp,
                                            topEnd = 18.dp,
                                            bottomStart = if (msg.isFromUser) 18.dp else 4.dp,
                                            bottomEnd = if (msg.isFromUser) 4.dp else 18.dp
                                        )
                                    )
                                    .background(
                                        if (msg.isFromUser) NeonPurple.copy(alpha = 0.4f)
                                        else Color.White.copy(alpha = 0.08f)
                                    )
                                    .border(
                                        1.dp,
                                        if (msg.isFromUser) NeonPurple else NeonCyan.copy(alpha = 0.3f),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = msg.senderName,
                                            color = if (msg.isFromUser) NeonCyan else NeonAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        if (!msg.isFromUser && msg.modelUsed.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color.White.copy(alpha = 0.08f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(msg.modelUsed, color = NeonCyan, fontSize = 8.sp)
                                            }
                                        }
                                    }

                                    if (!msg.isFromUser) {
                                        IconButton(
                                            onClick = {
                                                SpeechAndTtsManager.speak(msg.text, context)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                                contentDescription = "Speak Aloud",
                                                tint = NeonCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                // If attached bitmap in user message
                                msg.imageBitmap?.let { bmp ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = "User Attachment",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                // Grounding Sources display
                                if (msg.groundingSources.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.3f))
                                            .padding(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Language, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("مصادر البحث والتحقق من Google:", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        msg.groundingSources.forEach { sourceText ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        try {
                                                            val cleanUri = if (sourceText.startsWith("http")) sourceText else "https://$sourceText"
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUri))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Log.e("AiAssistantModal", "Cannot open uri: $sourceText")
                                                        }
                                                    }
                                                    .padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "• $sourceText",
                                                    color = Color(0xFF93C5FD),
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (isThinking || isTranscribing) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = NeonCyan,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTranscribing) "جاري تفريغ الصوت بنموذج gemini-3.5-transcribe..." else "NEXA AI ($selectedModel) يفكر ويحلل...",
                                    color = NeonCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Attached Image Thumbnail Bar
                attachedBitmap?.let { bmp ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Attached Thumbnail",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("صورة جاهزة للتحليل بالذكاء", color = Color.White, fontSize = 11.sp)
                        }

                        IconButton(
                            onClick = { viewModel.clearAttachedImage() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                        }
                    }
                }

                // Listening Status Indicator
                if (isListening) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonPink.copy(alpha = 0.2f))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("جاري الاستماع لصوتك الآن... تحدث لـ NEXA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Bottom Input Control Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Photo Attachment Button
                    Box {
                        IconButton(
                            onClick = { showPhotoPickerMenu = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Attach Image",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showPhotoPickerMenu,
                            onDismissRequest = { showPhotoPickerMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = NeonPurple)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("اختر من المعرض")
                                    }
                                },
                                onClick = {
                                    showPhotoPickerMenu = false
                                    galleryLauncher.launch("image/*")
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonPink)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("التقاط صورة بالكاميرا")
                                    }
                                },
                                onClick = {
                                    showPhotoPickerMenu = false
                                    cameraLauncher.launch(null)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Microphone Voice Button
                    IconButton(
                        onClick = {
                            if (isListening) {
                                SpeechAndTtsManager.stopListening()
                            } else {
                                SpeechAndTtsManager.startListening(
                                    context = context,
                                    onResult = { spoken ->
                                        userPromptText = spoken
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isListening) NeonPink else Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isListening) Color.White else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = userPromptText,
                        onValueChange = { userPromptText = it },
                        placeholder = { Text("اكتب أو تحدث لـ NEXA AI...", color = Color.Gray, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (userPromptText.isNotBlank() || attachedBitmap != null) {
                                viewModel.sendAiPrompt(userPromptText, attachedBitmap)
                                userPromptText = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = BackgroundDark, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
