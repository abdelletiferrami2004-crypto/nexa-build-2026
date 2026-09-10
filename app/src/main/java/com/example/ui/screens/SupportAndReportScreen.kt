package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.util.NotificationSoundManager
import kotlinx.coroutines.launch

enum class ReportScreenMode {
    ABUSE_REPORT,
    TECH_SUPPORT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportAndReportScreen(
    viewModel: MajarrahViewModel,
    initialMode: ReportScreenMode = ReportScreenMode.ABUSE_REPORT,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentMode by remember { mutableStateOf(initialMode) }
    val userProfile by viewModel.userProfile.collectAsState()

    // ABUSE REPORT FORM STATE
    var reportedTarget by remember { mutableStateOf("") }
    var abuseReasonCategory by remember { mutableStateOf("تحرش أو تنمر إلكتروني ⚠️") }
    var showAbuseDropdown by remember { mutableStateOf(false) }
    var abuseDetails by remember { mutableStateOf("") }
    var abuseSeverity by remember { mutableStateOf("عادي (تحقيق قياسي)") }
    var autoBlockUser by remember { mutableStateOf(true) }

    val abuseCategories = listOf(
        "تحرش أو تنمر إلكتروني ⚠️",
        "محتوى يحث على الكراهية أو العنف 🛑",
        "احتيال أو نصب مالي / روابط تصيد 💳",
        "انتحال هوية أو حساب وهمي 👤",
        "انتهاك خصوصية أو تسريب بيانات 🔒",
        "محتوى غير لائق ومخالف لمعايير المجتمع 🚫"
    )

    // TECH SUPPORT FORM STATE
    var supportTopicCategory by remember { mutableStateOf("عطل أو خطأ تقني في التطبيق 🛠️") }
    var showSupportDropdown by remember { mutableStateOf(false) }
    var supportSubject by remember { mutableStateOf("") }
    var supportDescription by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf(userProfile?.phone ?: "user@nexa.app") }

    val supportCategories = listOf(
        "عطل أو خطأ تقني في التطبيق 🛠️",
        "مشاكل في المحفظة الإلكترونية وشراء العملات 💰",
        "صعوبات في تسجيل الدخول أو التوثيق 🔑",
        "استفسار عن التشفير وأمان الحساب 🛡️",
        "اقتراح ميزة جديدة لتطوير NEXA 🚀",
        "أخرى 💬"
    )

    // SUBMISSION STATE
    var isSubmitting by remember { mutableStateOf(false) }
    var lastSubmittedTicketId by remember { mutableStateOf<String?>(null) }
    var submissionSuccessMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // TOP APP BAR
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp),
            color = Color(0xFF0F172A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "مركز البلاغات والدعم الفني",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = EncryptedGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مربوط ومحفوظ بسحابة Firebase Firestore (reports)",
                            color = EncryptedGreen,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NEXA Guard 🛡️",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // TAB SWITCHER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF131B2E))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Abuse Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (currentMode == ReportScreenMode.ABUSE_REPORT) Color(0xFFDC2626) else Color.Transparent)
                    .clickable {
                        currentMode = ReportScreenMode.ABUSE_REPORT
                        lastSubmittedTicketId = null
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "بلاغ عن محتوى / مستخدم مسيء",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Tech Support Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (currentMode == ReportScreenMode.TECH_SUPPORT) NeonCyan else Color.Transparent)
                    .clickable {
                        currentMode = ReportScreenMode.TECH_SUPPORT
                        lastSubmittedTicketId = null
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = if (currentMode == ReportScreenMode.TECH_SUPPORT) BackgroundDark else Color.LightGray,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "شكوى ودعم تقني NEXA",
                        color = if (currentMode == ReportScreenMode.TECH_SUPPORT) BackgroundDark else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (lastSubmittedTicketId != null) {
                // SUCCESS STATE CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF062319)),
                    border = BorderStroke(1.5.dp, EncryptedGreen)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(EncryptedGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EncryptedGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "تم حفظ البلاغ في السحابة بنجاح! ☁️",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "رقم البلاغ: $lastSubmittedTicketId",
                            color = EncryptedGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = submissionSuccessMessage ?: "تم إرسال بلاغك وتخزينه في قاعدة بيانات Firebase Firestore (مجموعة reports) للمراجعة واتخاذ الإجراء الفوري.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                lastSubmittedTicketId = null
                                reportedTarget = ""
                                abuseDetails = ""
                                supportSubject = ""
                                supportDescription = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("إرسال بلاغ أو شكوى جديدة", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                when (currentMode) {
                    ReportScreenMode.ABUSE_REPORT -> {
                        // ABUSE REPORT FORM
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "نموذج الإبلاغ عن محتوى أو مستخدم مسيء",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "يُحفظ البلاغ في Firestore للتدقيق والحظر الفوري",
                                            color = Color(0xFFEF4444),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Target Input
                                Text("اسم المستخدم أو رابط المحتوى المسيء:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = reportedTarget,
                                    onValueChange = { reportedTarget = it },
                                    placeholder = { Text("مثال: @username أو رابط المنشور / المحادثة", color = Color.Gray, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFEF4444),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Category Dropdown
                                Text("نوع المخالفة أو الإساءة:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.06f))
                                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable { showAbuseDropdown = true }
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(abuseReasonCategory, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    }

                                    DropdownMenu(
                                        expanded = showAbuseDropdown,
                                        onDismissRequest = { showAbuseDropdown = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .background(Color(0xFF1E1B2E))
                                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    ) {
                                        abuseCategories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat, color = Color.White, fontSize = 11.sp) },
                                                onClick = {
                                                    abuseReasonCategory = cat
                                                    showAbuseDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Details Input
                                Text("تفاصيل وشرح المخالفة:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = abuseDetails,
                                    onValueChange = { abuseDetails = it },
                                    placeholder = { Text("اكتب ما حدث بدقة، التوقيت، أو أي عبارات مسيئة تم توجيهها...", color = Color.Gray, fontSize = 11.sp) },
                                    minLines = 3,
                                    maxLines = 5,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFEF4444),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Auto Block Toggle
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.04f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("حظر هذا المستخدم تلقائياً على حسابك", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("لن يتمكن من إرسال رسائل أو رؤية حسابك", color = Color.LightGray, fontSize = 9.sp)
                                        }
                                    }
                                    Switch(
                                        checked = autoBlockUser,
                                        onCheckedChange = { autoBlockUser = it },
                                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFFEF4444))
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Submit Button
                                Button(
                                    onClick = {
                                        if (reportedTarget.isBlank() || abuseDetails.isBlank()) {
                                            Toast.makeText(context, "يرجى كتابة اسم المستخدم ووصف المخالفة", Toast.LENGTH_SHORT).show()
                                        } else {
                                            isSubmitting = true
                                            scope.launch {
                                                if (autoBlockUser && reportedTarget.isNotBlank()) {
                                                    viewModel.blockUser(reportedTarget)
                                                }
                                                val (success, repId) = viewModel.submitSupportOrAbuseReport(
                                                    reportType = "ABUSE_REPORT",
                                                    targetSubjectOrUser = reportedTarget,
                                                    category = abuseReasonCategory,
                                                    details = abuseDetails,
                                                    senderContact = userProfile?.phone ?: "Anonymous User",
                                                    severityLevel = "HIGH"
                                                )
                                                isSubmitting = false
                                                lastSubmittedTicketId = repId
                                                submissionSuccessMessage = "تم تسجيل بلاغ الإساءة وتخزينه في Firebase Firestore بمجلد reports، وتم تفعيل نظام الرقابة لحظر الحساب المخالف."
                                                try {
                                                    NotificationSoundManager.playPopChime(context)
                                                } catch (_: Throwable) {}
                                            }
                                        }
                                    },
                                    enabled = !isSubmitting,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "إرسال البلاغ وتخزينه سحابياً 🚀",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ReportScreenMode.TECH_SUPPORT -> {
                        // TECH SUPPORT FORM
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "نموذج شكاوى واستفسارات دعم NEXA",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "تواصل مباشر مع فريق التطوير والدعم الفني",
                                            color = NeonCyan,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Category Dropdown
                                Text("قسم الشكوى أو المشكلة:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.06f))
                                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable { showSupportDropdown = true }
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(supportTopicCategory, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    }

                                    DropdownMenu(
                                        expanded = showSupportDropdown,
                                        onDismissRequest = { showSupportDropdown = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .background(Color(0xFF131F37))
                                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    ) {
                                        supportCategories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat, color = Color.White, fontSize = 11.sp) },
                                                onClick = {
                                                    supportTopicCategory = cat
                                                    showSupportDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Subject Input
                                Text("عنوان المشكلة / الشكوى:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = supportSubject,
                                    onValueChange = { supportSubject = it },
                                    placeholder = { Text("مثال: توقف الصوت عند الاتصال المشفر...", color = Color.Gray, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Contact Input
                                Text("البريد الإلكتروني أو الهاتف للمتابعة:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = contactInfo,
                                    onValueChange = { contactInfo = it },
                                    placeholder = { Text("support@nexa.app", color = Color.Gray, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Description Input
                                Text("شرح المشكلة والخطوات:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = supportDescription,
                                    onValueChange = { supportDescription = it },
                                    placeholder = { Text("يرجى تزويدنا بتفاصيل العطل أو الشكوى لنتمكن من حلها سريعاً...", color = Color.Gray, fontSize = 11.sp) },
                                    minLines = 3,
                                    maxLines = 5,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Submit Support Button
                                Button(
                                    onClick = {
                                        if (supportSubject.isBlank() || supportDescription.isBlank()) {
                                            Toast.makeText(context, "يرجى كتابة عنوان الشكوى والشرح", Toast.LENGTH_SHORT).show()
                                        } else {
                                            isSubmitting = true
                                            scope.launch {
                                                val (success, repId) = viewModel.submitSupportOrAbuseReport(
                                                    reportType = "TECH_SUPPORT",
                                                    targetSubjectOrUser = supportSubject,
                                                    category = supportTopicCategory,
                                                    details = supportDescription,
                                                    senderContact = contactInfo,
                                                    severityLevel = "NORMAL"
                                                )
                                                isSubmitting = false
                                                lastSubmittedTicketId = repId
                                                submissionSuccessMessage = "تم فتح تذكرة دعم فني وحفظها في Firestore (reports) برقم تتبع $repId. سيقوم فريق الدعم بالرد خلال 24 ساعة."
                                                try {
                                                    NotificationSoundManager.playPopChime(context)
                                                } catch (_: Throwable) {}
                                            }
                                        }
                                    },
                                    enabled = !isSubmitting,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "إرسال التذكرة وحفظها في Firestore 🚀",
                                                color = BackgroundDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Info Card about Firestore reports
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827).copy(alpha = 0.7f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EncryptedGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "يتم تسجيل جميع البلاغات والشكاوى في قاعدة بيانات Firebase Firestore داخل مجلد `reports` برقم تتبع مشفر ومراجعتها عبر منظومة NEXA Guard.",
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
