package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.remote.GeminiRepository
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.util.NotificationSoundManager
import com.example.util.SpeechAndTtsManager
import kotlinx.coroutines.launch

data class LanguageOption(
    val id: String,
    val name: String,
    val flag: String,
    val languageTag: String
)

data class RoleOption(
    val id: String,
    val title: String,
    val icon: String,
    val description: String
)

val availableLanguages = listOf(
 LanguageOption("en","English","","en-US"),
 LanguageOption("fr","Français","","fr-FR"),
 LanguageOption("es","Español","","es-ES"),
 LanguageOption("ar","العربية","","ar-SA")
)

val availableRoles = listOf(
 RoleOption("tutor","معلم لغة (Tutor)","‍","معلم مشجع يصحح القواعد برفق ويسأل أسئلة تفاعلية."),
 RoleOption("interviewer","مقابل عمل (Interviewer)","","مسؤول توظيف احترافي يطرح أسئلة مقابلة عمل تقنية وقيادية."),
 RoleOption("friend","صديق مقرب (Casual Friend)","","صديق ودردشة عفوية عن الحياة واليوميات والتقنية.")
)

@Composable
fun VoiceTutorModal(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedLanguage by remember { mutableStateOf(availableLanguages[0]) } // English default
    var selectedRole by remember { mutableStateOf(availableRoles[0]) } // Tutor default

    var showLanguageMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    val isSpeaking by SpeechAndTtsManager.isSpeaking.collectAsState()
    val isListening by SpeechAndTtsManager.isListening.collectAsState()

    var isAiThinking by remember { mutableStateOf(false) }
    var lastUserSpeech by remember { mutableStateOf("") }
    var lastAiResponse by remember { mutableStateOf("") }
    var isMuted by remember { mutableStateOf(false) }

    // Init TTS
    LaunchedEffect(Unit) {
        SpeechAndTtsManager.initTts(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            SpeechAndTtsManager.stopSpeaking()
            SpeechAndTtsManager.stopListening()
        }
    }

    val processVoiceInput = { spokenText: String ->
        lastUserSpeech = spokenText
        isAiThinking = true
        lastAiResponse = ""

        val systemInstruction = """
            You are NEXA AI, acting as a ${selectedRole.title} for a user practicing ${selectedLanguage.name}.
            Role Instructions: ${selectedRole.description}
            
            Strict Guidelines:
            1. Respond EXCLUSIVELY in ${selectedLanguage.name}.
            2. Keep your response very concise (1 to 3 short natural conversational sentences maximum) so it sounds natural in audio Text-To-Speech.
            3. If acting as Language Tutor, briefly correct any major grammatical mistake in a friendly tone, then ask a simple follow-up question.
            4. Do NOT use markdown symbols like *, #, or bullets because this will be spoken out loud.
        """.trimIndent()

        scope.launch {
            val responseText = GeminiRepository.generateContent(
                prompt = spokenText,
                systemInstruction = systemInstruction
            )
            isAiThinking = false
            lastAiResponse = responseText
            NotificationSoundManager.playPopChime(context)

            if (!isMuted) {
                SpeechAndTtsManager.speak(responseText, context, selectedLanguage.languageTag)
            }
        }
    }

    // Waveform Animation Values
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Dialog(onDismissRequest = {
        SpeechAndTtsManager.stopSpeaking()
        SpeechAndTtsManager.stopListening()
        onDismiss()
    }) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = BackgroundDark,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .border(1.5.dp, Brush.linearGradient(listOf(NeonPink, NeonCyan, NeonPurple)), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.3f))
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
 Text("NEXA Voice Tutor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("المحاكي الصوتي الذكي للغات", color = Color.Gray, fontSize = 11.sp)
                        }
                    }

                    Row {
                        IconButton(onClick = { isMuted = !isMuted }) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Mute",
                                tint = if (isMuted) Color.Red else NeonCyan
                            )
                        }
                        IconButton(onClick = {
                            SpeechAndTtsManager.stopSpeaking()
                            SpeechAndTtsManager.stopListening()
                            onDismiss()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selectors Row (Language + Role)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable { showLanguageMenu = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(selectedLanguage.flag, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(selectedLanguage.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.Language, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        }

                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            availableLanguages.forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(lang.flag, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(lang.name, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    onClick = {
                                        selectedLanguage = lang
                                        showLanguageMenu = false
                                        Toast.makeText(context, "تم تغيير لغة التمرين إلى ${lang.name}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }

                    // Role Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable { showRoleMenu = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(selectedRole.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = selectedRole.title.split(" ")[0],
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            availableRoles.forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(role.icon, fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(role.title, fontWeight = FontWeight.Bold, color = NeonPurple)
                                            }
                                            Text(role.description, fontSize = 10.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        selectedRole = role
                                        showRoleMenu = false
                                        Toast.makeText(context, "تم التبديل إلى دور: ${role.title}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Call Visualizer Center Area
                Box(
                    modifier = Modifier
                        .size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Pulsating Rings
                    val ringColor = when {
                        isSpeaking -> NeonCyan
                        isListening -> NeonPink
                        isAiThinking -> NeonAmber
                        else -> NeonPurple
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2.2f

                        if (isSpeaking || isListening) {
                            drawCircle(
                                color = ringColor.copy(alpha = 0.2f * waveAlpha),
                                radius = radius * pulseScale,
                                center = center
                            )
                            drawCircle(
                                color = ringColor.copy(alpha = 0.4f * waveAlpha),
                                radius = radius * (pulseScale * 0.85f),
                                center = center
                            )
                        } else {
                            drawCircle(
                                color = ringColor.copy(alpha = 0.15f),
                                radius = radius,
                                center = center
                            )
                        }
                    }

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        NeonPurple.copy(alpha = 0.8f),
                                        NeonPink.copy(alpha = 0.8f),
                                        BackgroundDark
                                    )
                                )
                            )
                            .border(2.5.dp, ringColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "NEXA AI",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Audio Waveform Visualizer Bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(36.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val barHeights = listOf(0.4f, 0.8f, 1.0f, 0.6f, 0.9f, 0.5f, 0.7f)
                    barHeights.forEachIndexed { index, baseHeight ->
                        val animatedH = if (isSpeaking || isListening) {
                            (baseHeight * pulseScale * 0.7f).coerceIn(0.2f, 1.0f)
                        } else 0.2f

                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight(animatedH)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSpeaking) NeonCyan
                                    else if (isListening) NeonPink
                                    else Color.White.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                // Call Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            when {
                                isSpeaking -> NeonCyan.copy(alpha = 0.2f)
                                isListening -> NeonPink.copy(alpha = 0.2f)
                                isAiThinking -> NeonAmber.copy(alpha = 0.2f)
                                else -> Color.White.copy(alpha = 0.08f)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isSpeaking -> NeonCyan
                                isListening -> NeonPink
                                isAiThinking -> NeonAmber
                                else -> Color.Transparent
                            },
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isAiThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = NeonAmber,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
 Text("جاري التفكير وصياغة الرد ...", color = NeonAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = when {
 isSpeaking ->"NEXA يتحدث معك الآن (${selectedLanguage.name})"
 isListening ->"جاري الاستماع لصوتك (${selectedLanguage.name})..."
 else ->"اضغط زر المايك للتحدث مع NEXA"
                                },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Realtime Subtitles / Log Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
 Text(" الترجمة والمحادثة الحية:", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))

                    if (lastUserSpeech.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("أنت: ", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(lastUserSpeech, color = Color.White, fontSize = 12.sp)
                        }
                    }

                    if (lastAiResponse.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text("NEXA: ", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(lastAiResponse, color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }

                    if (lastUserSpeech.isBlank() && lastAiResponse.isBlank()) {
                        Text(
 text ="ابدأ المحادثة الصوتية الآن بممارسة لغة ${selectedLanguage.name} بصفتي ${selectedRole.title}",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Call Control Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // End Call Button
                    IconButton(
                        onClick = {
                            SpeechAndTtsManager.stopSpeaking()
                            SpeechAndTtsManager.stopListening()
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.85f))
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(26.dp))
                    }

                    // Main Microphone Push-To-Talk Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) Brush.linearGradient(listOf(NeonPink, NeonPurple))
                                else Brush.linearGradient(listOf(NeonCyan, NeonPurple))
                            )
                            .clickable {
                                if (isListening) {
                                    SpeechAndTtsManager.stopListening()
                                } else {
                                    SpeechAndTtsManager.stopSpeaking()
                                    SpeechAndTtsManager.startListening(
                                        context = context,
                                        languageTag = selectedLanguage.languageTag,
                                        onResult = { spoken ->
                                            processVoiceInput(spoken)
                                        },
                                        onError = { err ->
                                            Toast
                                                .makeText(context, err, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Speak to NEXA",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Test Prompt Quick Action Button
                    IconButton(
                        onClick = {
                            val sample = when (selectedLanguage.id) {
                                "fr" -> "Bonjour! Comment allez-vous aujourd'hui?"
                                "es" -> "¡Hola! ¿Cómo estás hoy?"
                                "ar" -> "مرحباً! كيف حالك اليوم؟"
                                else -> "Hello! How are you doing today?"
                            }
                            processVoiceInput(sample)
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Quick Sample Prompt", tint = NeonAmber, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
