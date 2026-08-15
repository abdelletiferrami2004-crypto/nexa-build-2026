package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.util.SpeechAndTtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class VoiceCompanionPersona(
    val id: String,
    val nameArabic: String,
    val titleArabic: String,
    val color: Color
)

@Composable
fun NexaVoiceCompanionModal(
    onDismiss: () -> Unit,
    onVoiceInteractionSuccess: (expGained: Int) -> Unit = {},
    onNavigateToReels: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    onOpenVault: () -> Unit = {},
    onSearchTopic: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val personas = listOf(
        VoiceCompanionPersona("nexa_core", "نيكا الذكية", "المساعد الرئيسي والمبتكر", NeonCyan),
        VoiceCompanionPersona("andromeda", "أندروميدا", "خبير التكنولوجيا والبرمجة", NeonPurple),
        VoiceCompanionPersona("aria", "أريا", "رفيقة الإبداع وصناعة المحتوى", NeonPink)
    )

    var selectedPersona by remember { mutableStateOf(personas[0]) }
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var assistantResponse by remember {
        mutableStateOf("أهلاً بك! أنا رفيقك الصوتي الذكي في NEXA. اضغط على الميكروفون وتحدث إلي، أو اختر من الأوامر السريعة أدناه.")
    }
    var speechSpeed by remember { mutableStateOf(1.0f) }
    var activeMode by remember { mutableStateOf("chat") } // "chat", "translate", "summarize", "creative"

    LaunchedEffect(Unit) {
        SpeechAndTtsManager.initTts(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            SpeechAndTtsManager.stopSpeaking()
        }
    }

    // Orb Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = if (isListening || isSpeaking) 0.95f else 0.98f,
        targetValue = if (isListening || isSpeaking) 1.22f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening || isSpeaking) 750 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )

    val orbRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbRotation"
    )

    fun handleVoiceCommand(input: String) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        val text = input.trim()
        if (text.isBlank()) return

        recognizedText = text
        isListening = false
        isSpeaking = true

        val lower = text.lowercase()
        val response = when {
            lower.contains("ترجمة") || activeMode == "translate" -> {
                "الترجمة الذكية عبر محرك NEXA: '$text' -> 'Smart dynamic translation powered by NEXA AI Multi-modal System.'"
            }
            lower.contains("ريلز") || lower.contains("فيديو") || lower.contains("مقاطع") -> {
                onNavigateToReels()
                "جاري نقلك إلى خلاصة مقاطع الريلز المباشرة الآن..."
            }
            lower.contains("شات") || lower.contains("محادثة") || lower.contains("رسائل") -> {
                onOpenChat()
                "جاري فتح شاشة المحادثات الفورية المشفرة..."
            }
            lower.contains("خزنة") || lower.contains("سرية") || lower.contains("vault") -> {
                onOpenVault()
                "جاري فتح الخزنة المشفرة بمصادقة PIN الآمنة..."
            }
            lower.contains("ترند") || lower.contains("شائع") || activeMode == "summarize" -> {
                "أبرز ترندات مجرة اليوم: 1. تحديثات الذكاء الاصطناعي 2026. 2. إطلاق الغرف الصوتية النيون. 3. بطولات نقاط EXP والمكافآت."
            }
            lower.contains("نصيحة") || lower.contains("فكرة") || activeMode == "creative" -> {
                "إليك فكرة مبتكرة: قم بإنشاء ستوري نيون بتقنية 3D تشرح فيها تجربتك مع المساعد الصوتي لتحصل على 200 نقطة EXP إضافية!"
            }
            else -> {
                "بصفتي ${selectedPersona.nameArabic}، قمت بتحليل طلبك: '$text'. نظام NEXA جاهز لخدمتك وتنفيذ المهام التفاعلية بأقصى سرعة."
            }
        }

        assistantResponse = response
        SpeechAndTtsManager.speak(response, context)
        onVoiceInteractionSuccess(35) // +35 EXP for voice companion interaction
    }

    fun startListeningSimulation(sampleQuery: String? = null) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        SpeechAndTtsManager.stopSpeaking()
        isSpeaking = false
        isListening = true
        recognizedText = "جاري الاستماع لصوتك..."

        scope.launch {
            delay(1600)
            val query = sampleQuery ?: listOf(
                "ما هي أبرز ميزات الذكاء الاصطناعي في NEXA؟",
                "لخص لي أحداث وترندات اليوم",
                "اقترح خطة لزيادة نقاط خبرتي وشاراتي",
                "ترجم: أهلاً بك في منصة المستقبل",
                "افتح مقاطع الريلز الشائعة"
            ).random()
            handleVoiceCommand(query)
        }
    }

    Dialog(
        onDismissRequest = {
            SpeechAndTtsManager.stopSpeaking()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(32.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            selectedPersona.color,
                            NeonPurple,
                            NeonPink
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                ),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(selectedPersona.color.copy(alpha = 0.2f))
                                .border(1.5.dp, selectedPersona.color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = selectedPersona.color,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "الرفيق الصوتي الذكي (Voice Companion)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "تفاعل صوتي حي مع الذكاء الاصطناعي",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = {
                        SpeechAndTtsManager.stopSpeaking()
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Persona Selector Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(personas) { persona ->
                        val isSelected = persona.id == selectedPersona.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) persona.color.copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) persona.color else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedPersona = persona }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(persona.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = persona.nameArabic,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) persona.color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Central Holographic Glowing Orb
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(orbScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow layers
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        selectedPersona.color.copy(alpha = 0.4f),
                                        NeonPurple.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Inner glowing orb
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        selectedPersona.color,
                                        NeonPurple,
                                        NeonPink
                                    )
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Sound Waves Visualizer Canvas
                AnimatedSoundWaveVisualizer(
                    isActive = isListening || isSpeaking,
                    primaryColor = selectedPersona.color,
                    secondaryColor = NeonPurple,
                    accentColor = NeonPink,
                    height = 65.dp,
                    barCount = 32
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Recognized Text & Assistant Response Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    borderColor = selectedPersona.color.copy(alpha = 0.35f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (recognizedText.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "صوتك: \"$recognizedText\"",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeonAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Text(
                            text = assistantResponse,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 21.sp,
                            textAlign = TextAlign.Start
                        )

                        if (isSpeaking) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "جاري قراءة الرد الصوتي...",
                                    fontSize = 11.sp,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Push-to-Talk / Mic Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (isListening) {
                                isListening = false
                            } else {
                                startListeningSimulation()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) NeonPink else selectedPersona.color,
                            contentColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .fillMaxWidth(0.75f),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isListening) "جاري الاستماع... (اضغط للإيقاف)" else "اضغط وتحدث مع ${selectedPersona.nameArabic}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isSpeaking) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                SpeechAndTtsManager.stopSpeaking()
                                isSpeaking = false
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonPink.copy(alpha = 0.2f))
                                .border(1.dp, NeonPink, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "إيقاف الصوت",
                                tint = NeonPink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick Prompt Shortcuts (Chips)
                Text(
                    text = "أوامر صوتية سريعة ومقترحات:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val quickPrompts = listOf(
                    "ما هو ملخص ترند اليوم؟",
                    "ترجم لي: صباح الخير والتفاؤل",
                    "اقترح خطة لزيادة نقاط EXP والشارات",
                    "افتح مقاطع الريلز المباشرة",
                    "افتح الخزنة المشفرة بالـ PIN"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickPrompts) { prompt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                .clickable {
                                    startListeningSimulation(prompt)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
