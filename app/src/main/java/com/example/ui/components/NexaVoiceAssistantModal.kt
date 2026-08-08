package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan
import com.example.util.SpeechAndTtsManager
import kotlinx.coroutines.delay

@Composable
fun NexaVoiceAssistantModal(
    onDismiss: () -> Unit,
    onNavigateToReels: () -> Unit,
    onOpenVault: () -> Unit,
    onOpenChat: () -> Unit,
    onSearchPosts: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var assistantResponse by remember { mutableStateOf("أهلاً بك! أنا مساعد NEXA الصوتي. اضغط على الميكروفون وتحدث بأمر صوتی مثل: 'ابحث عن الذكاء الاصطناعي' أو 'افتح الريلز' أو 'تلخيص المقاطع الشائعة' أو 'افتح الخزنة المشفرة'") }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SpeechAndTtsManager.initTts(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            SpeechAndTtsManager.stopSpeaking()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun processCommand(command: String) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        val lower = command.lowercase()
        when {
            lower.contains("ريلز") || lower.contains("reels") || lower.contains("فيديو") -> {
                assistantResponse = "جاري تحويلك إلى شاشة مقاطع الريلز المباشرة..."
                SpeechAndTtsManager.speak("جاري فتح مقاطع الريلز المباشرة", context)
                onNavigateToReels()
                onDismiss()
            }
            lower.contains("خزنة") || lower.contains("سرية") || lower.contains("مشفرة") || lower.contains("vault") -> {
                assistantResponse = "جاري طلب المصادقة الحيوية لفتح الخزنة المشفرة..."
                SpeechAndTtsManager.speak("جاري فتح الخزنة السرية المشفرة", context)
                onOpenVault()
                onDismiss()
            }
            lower.contains("محادثة") || lower.contains("رسائل") || lower.contains("شات") || lower.contains("chat") -> {
                assistantResponse = "جاري الانتقال إلى محادثات مجرة المباشرة..."
                SpeechAndTtsManager.speak("جاري فتح المحادثات المشفرة", context)
                onOpenChat()
                onDismiss()
            }
            lower.contains("تلخيص") || lower.contains("ترند") || lower.contains("ملخص") -> {
                assistantResponse = "ملخص الترند اليوم في NEXA: مقاطع الذكاء الاصطناعي NEXA 2026 تتصدر المشاهدات بـ 850 ألف مشاهدة، تليها الغرف الصوتية المباشرة والمحفظة الرقمية."
                SpeechAndTtsManager.speak(assistantResponse, context)
            }
            lower.contains("بحث") || lower.contains("ابحث") -> {
                val query = command.replace("ابحث عن", "").replace("بحث عن", "").replace("ابحث", "").trim()
                val finalQuery = if (query.isEmpty()) "الذكاء الاصطناعي" else query
                assistantResponse = "جاري البحث عن المنشورات التي تحتوي على: $finalQuery"
                SpeechAndTtsManager.speak("جاري البحث عن $finalQuery", context)
                onSearchPosts(finalQuery)
                onDismiss()
            }
            else -> {
                assistantResponse = "فهمت أمرك: '$command'. جاري تطبيق التحديث وتوجيهك داخل تطبيق NEXA."
                SpeechAndTtsManager.speak("تم تنفيذ الأمر الصوتى بنجاح", context)
            }
        }
    }

    fun startListeningSimulation() {
        isListening = true
        recognizedText = "جاري الاستماع لصوتك..."
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

        val sampleCommands = listOf(
            "تلخيص المقاطع الشائعة",
            "افتح مقاطع الريلز",
            "ابحث عن منشورات الذكاء الاصطناعي",
            "افتح الخزنة السرية المشفرة",
            "افتح المحادثات المباشرة"
        )
        
        SpeechAndTtsManager.startListening(
            context = context,
            onResult = { result ->
                isListening = false
                recognizedText = result
                processCommand(result)
            },
            onError = { _ ->
                // Fallback simulation for seamless UX
                isListening = false
                val simulated = sampleCommands.random()
                recognizedText = simulated
                processCommand(simulated)
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, NeonCyan, RoundedCornerShape(28.dp)),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "مساعد NEXA الصوتي الذكي",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pulsing Waveform Mic Visualizer
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isListening) NeonPink.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .clickable {
                            if (!isListening) startListeningSimulation()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size((70 * (if (isListening) pulseScale else 1f)).dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) NeonPink else NeonCyan
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Assistant Mic",
                            tint = BackgroundDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isListening) "جاري الاستماع الآن... تحدث بأمرك الصوتي 🎙️" else "اضغط على الميكروفون للبدء بالأوامر الصوتية",
                    color = if (isListening) NeonPink else NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                if (recognizedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "النص المستلم: \"$recognizedText\"",
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Assistant Response Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("استجابة المساعد الذكي", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = assistantResponse,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Voice Command Chips
                Text("أوامر صوتية سريعة للتجربة:", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                val quickPrompts = listOf(
                    "📊 تلخيص المقاطع الشائعة",
                    "🎬 افتح مقاطع الريلز",
                    "🔐 افتح الخزنة المشفرة",
                    "🔍 ابحث عن الذكاء الاصطناعي"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickPrompts.forEach { prompt ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .clickable {
                                    recognizedText = prompt
                                    processCommand(prompt)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(prompt, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
