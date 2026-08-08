package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
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

enum class AiToolType(val title: String, val icon: String, val description: String) {
 SUMMARIZER("تلخيص وتدقيق","","تلخيص النصوص واستخراج النقاط وتدقيق القواعد"),
 DRAFT_GENERATOR("مولد المحتوى","","صياغة منشورات السوشيال ميديا والإيميلات الاحترافية"),
 IDEAS_GENERATOR("مولد المشاريع","","ابتكار 5 أفكار أعمال تجارية ومشاريع واعدة")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiToolboxModal(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var activeTool by remember { mutableStateOf(AiToolType.SUMMARIZER) }
    var isLoading by remember { mutableStateOf(false) }
    var generatedOutput by remember { mutableStateOf("") }

    // Tool 1 inputs
    var textToSummarize by remember { mutableStateOf("") }

    // Tool 2 inputs
    var draftTopic by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("احترافي") }
    var selectedPlatform by remember { mutableStateOf("LinkedIn") }
 val tones = listOf("احترافي","حماسي","رسمي","عفوي وتسويقي")
 val platforms = listOf("LinkedIn","Twitter/X","بريد إلكتروني","Instagram")

    // Tool 3 inputs
    var businessNiche by remember { mutableStateOf("") }
    var selectedTargetAudience by remember { mutableStateOf("رواد الأعمال والشباب") }
 val audienceOptions = listOf("رواد الأعمال والشباب","الشركات والمؤسسات","المستهلك الفردي")

    val handleGenerate = {
        if (!isLoading) {
            isLoading = true
            generatedOutput = ""

            scope.launch {
                val systemPrompt: String
                val promptText: String

                when (activeTool) {
                    AiToolType.SUMMARIZER -> {
                        systemPrompt = """
                            أنت محرر خبير ومدقق لغوي باللغة العربية والانجليزية.
                            المطلوب من النص المقدم:
                            1. تقديم ملخص تنفيذي مركز في نقاط بولت بولتس (Bullet points).
                            2. استخراج أهم المفاهيم الكبرى.
                            3. مراجعة القواعد وصياغة نسخة محسنة وأكثر بلاغة للنص.
                        """.trimIndent()
                        promptText = textToSummarize
                    }
                    AiToolType.DRAFT_GENERATOR -> {
                        systemPrompt = """
                            أنت خبير كاتب محتوى وحملات تسويقية (Copywriter).
                            قم بصياغة منشور احترافي أو مسودة إيميل متميزة بناءً على المدخلات.
                            النبرة المطلوب استخدامها: $selectedTone.
                            المنصة أو القالب المستهدف: $selectedPlatform.
                            اجعل النص جذاباً ومصحوباً بعنوان رئيسي وقوي وهاشتاجات مناسبة إذا كانت منصة تواصل.
                        """.trimIndent()
                        promptText = "الموضوع: $draftTopic"
                    }
                    AiToolType.IDEAS_GENERATOR -> {
                        systemPrompt = """
                            أنت مستشار حاضنات أعمال وابتكار تقني.
                            قدم 5 أفكار مشاريع تجارية مبتكرة وقابلة للتطبيق فوراً في مجال: $businessNiche.
                            الجمهور المستهدف: $selectedTargetAudience.
                            لكل فكرة ضع:
                            • اسم المشروع وشعار مختار.
                            • القيمة المضافة والميزة التنافسية.
                            • 3 خطوات عمل جانبية للتنفيذ السريع في المملكة والخليج.
                        """.trimIndent()
                        promptText = "مجال الاستثمار والابتكار: $businessNiche"
                    }
                }

                val result = GeminiRepository.generateContent(
                    prompt = promptText,
                    systemInstruction = systemPrompt
                )
                isLoading = false
                generatedOutput = result
                NotificationSoundManager.playPopChime(context)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = BackgroundDark,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .border(1.5.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple, NeonPink)), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Modal Header
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
                                .background(NeonCyan.copy(alpha = 0.25f))
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
 Text("NEXA AI Toolbox", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonPurple.copy(alpha = 0.3f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Gemini 3.5", color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("أدوات الإنتاجية والابتكار الذكية", color = Color.Gray, fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tools Switcher Cards / Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AiToolType.values().forEach { tool ->
                        val isSelected = activeTool == tool
                        val bgModifier = if (isSelected) {
                            Modifier.background(Brush.linearGradient(listOf(NeonPurple.copy(alpha = 0.7f), BackgroundDark)))
                        } else {
                            Modifier.background(Color.White.copy(alpha = 0.06f))
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .then(bgModifier)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    activeTool = tool
                                    generatedOutput = ""
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tool.icon, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tool.title,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Tool Content Area (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Active Tool Description Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NeonPurple.copy(alpha = 0.15f))
                            .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(activeTool.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(activeTool.title, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(activeTool.description, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tool Specific Inputs
                    when (activeTool) {
                        AiToolType.SUMMARIZER -> {
 Text("الصق النص المراد تلخيصه وتدقيقه :", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = textToSummarize,
                                onValueChange = { textToSummarize = it },
                                placeholder = { Text("الصق هنا مقالاً، أو تقريراً، أو مسودة بريد، أو محضر اجتماع...", color = Color.Gray, fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        AiToolType.DRAFT_GENERATOR -> {
 Text("الموضوع أو الفكرة الرئيسية :", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = draftTopic,
                                onValueChange = { draftTopic = it },
                                placeholder = { Text("مثال: إطلاق خدمة شحن جديدة في الرياض مع خصم 20% لجميع العملاء الجدد...", color = Color.Gray, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

 Text("نبرة وصوت المحتوى :", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                tones.forEach { tone ->
                                    val isToneSelected = selectedTone == tone.split(" ")[0]
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isToneSelected) NeonPink.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                                            .border(1.dp, if (isToneSelected) NeonPink else Color.Transparent, RoundedCornerShape(50))
                                            .clickable { selectedTone = tone.split(" ")[0] }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(tone, color = if (isToneSelected) Color.White else Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

 Text("المنصة / قالب المحتوى :", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                platforms.forEach { plat ->
                                    val isPlatSelected = selectedPlatform == plat.split(" ")[0]
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isPlatSelected) NeonCyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                                            .border(1.dp, if (isPlatSelected) NeonCyan else Color.Transparent, RoundedCornerShape(50))
                                            .clickable { selectedPlatform = plat.split(" ")[0] }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(plat, color = if (isPlatSelected) Color.White else Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        AiToolType.IDEAS_GENERATOR -> {
 Text("مجال الاستثمار أو التقنية المستهدفة :", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = businessNiche,
                                onValueChange = { businessNiche = it },
                                placeholder = { Text("مثال: الذكاء الاصطناعي في إدارة المطاعم، أو حلول الطاقة المتجددة للمنازل...", color = Color.Gray, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

 Text("الجمهور المستهدف :", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                audienceOptions.forEach { opt ->
                                    val isOptSelected = selectedTargetAudience == opt
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isOptSelected) NeonAmber.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                                            .border(1.dp, if (isOptSelected) NeonAmber else Color.Transparent, RoundedCornerShape(50))
                                            .clickable { selectedTargetAudience = opt }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(opt, color = if (isOptSelected) Color.White else Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Execution Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonCyan, NeonPurple)
                                )
                            )
                            .clickable {
                                when (activeTool) {
                                    AiToolType.SUMMARIZER -> {
                                        if (textToSummarize.isNotBlank()) handleGenerate()
                                        else Toast.makeText(context, "يرجى كتابة أو لصق النص أولاً", Toast.LENGTH_SHORT).show()
                                    }
                                    AiToolType.DRAFT_GENERATOR -> {
                                        if (draftTopic.isNotBlank()) handleGenerate()
                                        else Toast.makeText(context, "يرجى أدخال موضوع أو فكرة المنشور أولاً", Toast.LENGTH_SHORT).show()
                                    }
                                    AiToolType.IDEAS_GENERATOR -> {
                                        if (businessNiche.isNotBlank()) handleGenerate()
                                        else Toast.makeText(context, "يرجى إدخال مجال الأعمال أولاً", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("NEXA Gemini يحلل ويولّد النتائج...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (activeTool) {
 AiToolType.SUMMARIZER ->"تلخيص وتدقيق بـ Gemini"
 AiToolType.DRAFT_GENERATOR ->"توليد المحتوى بـ Gemini"
 AiToolType.IDEAS_GENERATOR ->"ابتكار 5 مشاريع بـ Gemini"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Result Output Display Area
                    if (generatedOutput.isNotBlank()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
 Text("نتيجة الذكاء الاصطناعي", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Export PDF / Text File Button
                                    IconButton(
                                        onClick = {
                                            com.example.util.FileExportManager.exportPdfSummary(
                                                context = context,
                                                documentTitle = activeTool.title,
                                                bodyText = generatedOutput
                                            )
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Export File",
                                            tint = NeonPink
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // TTS Speak Button
                                    IconButton(
                                        onClick = { SpeechAndTtsManager.speak(generatedOutput, context) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Read Aloud", tint = Color.White)
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Copy Output Button
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedOutput))
 Toast.makeText(context,"تم نسخ النتيجة إلى الحافظة بنجاح", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Output", tint = NeonCyan)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = generatedOutput,
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
