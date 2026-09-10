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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

enum class SupportPolicySection {
    ENCRYPTION_PRIVACY,
    TERMS_AND_MODERATION,
    SUPPORT_TICKETS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyAndSupportScreen(
    viewModel: MajarrahViewModel,
    initialSection: SupportPolicySection = SupportPolicySection.ENCRYPTION_PRIVACY,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var activeSection by remember { mutableStateOf(initialSection) }
    val userProfile by viewModel.userProfile.collectAsState()

    // Support Form State
    var reportCategory by remember { mutableStateOf("بلاغ عن محتوى أو مستخدم مسيء ⚠️") }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var reportSubject by remember { mutableStateOf("") }
    var reportMessage by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf(userProfile?.phone ?: "user@nexa.app") }
    var attachedFileUri by remember { mutableStateOf<String?>(null) }
    var isSubmittedSuccess by remember { mutableStateOf(false) }
    var generatedTicketId by remember { mutableStateOf("") }

    val reportCategories = listOf(
        "بلاغ عن محتوى أو مستخدم مسيء ⚠️",
        "مشكلة فنية أو عطل في التطبيق 🛠️",
        "استفسار عن حماية وتشفير البيانات 🔒",
        "شكوى بخصوص المتجر أو الدفع الإلكتروني 💳",
        "طلب استعادة حساب أو إزالة بيانات 🗑️",
        "اقتراح لتطوير وتحديث NEXA 🚀"
    )

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
                .shadow(8.dp),
            color = Color(0xFF0F172A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "سياسة الخصوصية والدعم الفني",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "حماية البيانات المشفرة • معايير الأمان 2026 • مساعدة فورية",
                        color = Color.LightGray,
                        fontSize = 10.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(EncryptedGreen.copy(alpha = 0.15f))
                        .border(1.dp, EncryptedGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GppGood,
                            contentDescription = null,
                            tint = EncryptedGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تشفير E2E",
                            color = EncryptedGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // NAVIGATION TAB PILLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF13192B))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tab 1: Encryption & Privacy
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSection == SupportPolicySection.ENCRYPTION_PRIVACY) NeonCyan else Color.Transparent)
                    .clickable { activeSection = SupportPolicySection.ENCRYPTION_PRIVACY }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = if (activeSection == SupportPolicySection.ENCRYPTION_PRIVACY) BackgroundDark else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "التشفير والخصوصية",
                        color = if (activeSection == SupportPolicySection.ENCRYPTION_PRIVACY) BackgroundDark else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            // Tab 2: Terms & Moderation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSection == SupportPolicySection.TERMS_AND_MODERATION) NeonPurple else Color.Transparent)
                    .clickable { activeSection = SupportPolicySection.TERMS_AND_MODERATION }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = if (activeSection == SupportPolicySection.TERMS_AND_MODERATION) Color.White else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "شروط وسياسة الحظر",
                        color = if (activeSection == SupportPolicySection.TERMS_AND_MODERATION) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            // Tab 3: Support Form
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSection == SupportPolicySection.SUPPORT_TICKETS) NeonPink else Color.Transparent)
                    .clickable { activeSection = SupportPolicySection.SUPPORT_TICKETS }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = if (activeSection == SupportPolicySection.SUPPORT_TICKETS) Color.White else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الدعم والبلاغات",
                        color = if (activeSection == SupportPolicySection.SUPPORT_TICKETS) Color.White else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // SCROLLABLE CONTENT BODY
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (activeSection) {
                SupportPolicySection.ENCRYPTION_PRIVACY -> {
                    // SECTION 1: END-TO-END ENCRYPTION & PRIVACY
                    EncryptionAndPrivacyContent()
                }

                SupportPolicySection.TERMS_AND_MODERATION -> {
                    // SECTION 2: TERMS OF SERVICE & ANTI-ABUSE POLICIES
                    TermsAndAntiAbuseContent()
                }

                SupportPolicySection.SUPPORT_TICKETS -> {
                    // SECTION 3: INTERACTIVE SUPPORT & COMPLAINT FORM
                    SupportComplaintFormContent(
                        reportCategory = reportCategory,
                        showCategoryDropdown = showCategoryDropdown,
                        onShowCategoryDropdownChange = { showCategoryDropdown = it },
                        reportCategories = reportCategories,
                        onCategorySelect = { reportCategory = it },
                        reportSubject = reportSubject,
                        onSubjectChange = { reportSubject = it },
                        reportMessage = reportMessage,
                        onMessageChange = { reportMessage = it },
                        contactEmail = contactEmail,
                        onEmailChange = { contactEmail = it },
                        isSubmittedSuccess = isSubmittedSuccess,
                        generatedTicketId = generatedTicketId,
                        onSubmit = {
                            if (reportSubject.isBlank() || reportMessage.isBlank()) {
                                Toast.makeText(context, "يرجى تعبئة عنوان البلاغ والوصف بالتفصيل", Toast.LENGTH_SHORT).show()
                            } else {
                                scope.launch {
                                    val (success, ticketNum) = viewModel.submitSupportOrAbuseReport(
                                        reportType = if (reportCategory.contains("مسيء")) "ABUSE_REPORT" else "TECH_SUPPORT",
                                        targetSubjectOrUser = reportSubject,
                                        category = reportCategory,
                                        details = reportMessage,
                                        senderContact = contactEmail,
                                        severityLevel = "NORMAL"
                                    )
                                    generatedTicketId = ticketNum
                                    isSubmittedSuccess = true
                                    try {
                                        NotificationSoundManager.playPopChime(context)
                                    } catch (_: Throwable) {}
                                    Toast.makeText(context, "تم حفظ بلاغك سحابياً في Firestore (reports) برقم: $ticketNum", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onResetForm = {
                            reportSubject = ""
                            reportMessage = ""
                            isSubmittedSuccess = false
                            generatedTicketId = ""
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EncryptionAndPrivacyContent() {
    // Top Hero Badge
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EncryptedGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = EncryptedGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "حماية وتشفير البيانات (End-to-End)",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "بروتوكول أمان متقدم لا يتيح لأي طرف ثالث قراءة بياناتك",
                        color = EncryptedGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "في تطبيق NEXA، نعتبر خصوصيتك وأمانك حقاً أساسياً غير قابل للمساومة. تم تصميم معمارية التطبيق على مبدأ (Zero-Knowledge Architecture)، بحيث يتم تشفير جميع المحادثات والملفات محلياً على جهازك قبل إرسالها عبر خوادم السحابة.",
                color = Color(0xFFE2E8F0),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }

    // Encryption Highlights Grid
    Text(
        text = "ركائز الأمان والتشفير في NEXA 🔐",
        color = NeonCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 4.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Point 1
        SecurityFeatureCard(
            icon = Icons.Default.Lock,
            iconTint = NeonCyan,
            title = "1. تشفير المحادثات والمكالمات من طرف لطرف",
            description = "يتم تشفير جميع الرسائل النصية، الصوتية، الصور، ومكالمات الفيديو بتقنيات AES-256 و RSA 4096-bit العسكرية. المفاتيح مشفرة ومحفوظة حصراً على هاتفك وهاتف المستلم فقط."
        )

        // Point 2
        SecurityFeatureCard(
            icon = Icons.Default.Fingerprint,
            iconTint = EncryptedGreen,
            title = "2. الحماية البيومترية والمصادقة المزدوجة",
            description = "إمكانية قفل التطبيق أو المحادثات الفردية الحساسة ببصمة الإصبع أو تقنية التعرف على الوجه، مع تفعيل نظام كشف الأنشطة المشبوهة (AI Anomaly Security) لتنبيهك عند أي محاولة اختراق."
        )

        // Point 3
        SecurityFeatureCard(
            icon = Icons.Default.GppGood,
            iconTint = NeonPurple,
            title = "3. عدم بيع أو مشاركة البيانات الشخصية",
            description = "نلتزم بشكل قاطع بعدم بيع أو مشاركة أرقام الهواتف أو سجلات المحادثات أو تفضيلات التسوق مع أي شبكات إعلانية أو جهات خارجية. التطبيق متوافق مع معايير GDPR و Google Play Safety 2026."
        )

        // Point 4
        SecurityFeatureCard(
            icon = Icons.Default.PrivacyTip,
            iconTint = NeonAmber,
            title = "4. الحذف الفوري وتصفير الحساب بضغطة واحدة",
            description = "يحق لك في أي وقت حذف حسابك ومسح كافة الوسائط والرسائل المخزنة محلياً وسحابياً من خلال الإعدادات، وسيتم إتلاف كافة السجلات المشفرة بشكل نهائي لا يمكن استرجاعه."
        )
    }
}

@Composable
private fun TermsAndAntiAbuseContent() {
    // Hero Card
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NeonPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "شروط الاستخدام وسياسة الحظر",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "سياسة صارمة تضمن بيئة آمنة وخالية من الإساءة للجميع",
                        color = NeonPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "باستخدامك لتطبيق NEXA، فإنك توافق على الالتزام بقواعد السلوك المجتمعي والامتناع التام عن نشر أي محتوى ينتهك القوانين أو يمس حقوق الآخرين.",
                color = Color(0xFFE2E8F0),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }

    Text(
        text = "المحظورات وإجراءات الحظر الفوري 🚫",
        color = Color(0xFFEF4444),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 4.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Prohibition 1
        SecurityFeatureCard(
            icon = Icons.Default.Block,
            iconTint = Color(0xFFEF4444),
            title = "1. المحتوى المسيء والتنمر الإلكتروني (Zero Tolerance)",
            description = "يُحظر منعاً باتاً إرسال أو نشر أي رسائل أو منشورات تحض على الكراهية، العنف، التمييز، التهديد، أو التحرش اللفظي. الحسابات المخالفة تخضع للإيقاف الفوري والحظر النهائي."
        )

        // Prohibition 2
        SecurityFeatureCard(
            icon = Icons.Default.Warning,
            iconTint = NeonAmber,
            title = "2. الاحتيال والروابط المضللة (Phishing & Spam)",
            description = "يقوم نظام NEXA Guard بفحص الروابط المشبوهة تلقائياً وتجميد أي حساب يحاول ترويج عمليات احتيالية أو بيع منتجات مقلدة أو تضليل المستخدمين."
        )

        // Prohibition 3
        SecurityFeatureCard(
            icon = Icons.Default.Shield,
            iconTint = NeonCyan,
            title = "3. حماية الناشئة والقُصّر (Teen Protection Mode)",
            description = "يمنع تداول أي محتوى غير لائق للفئات العمرية تحت سن 18 عاماً. يقوم نظام الذكاء الاصطناعي بفلترة المقاطع والمنشورات الحساسة تلقائياً."
        )

        // Prohibition 4
        SecurityFeatureCard(
            icon = Icons.Default.Gavel,
            iconTint = NeonPurple,
            title = "4. آلية الإبلاغ وحظر المستخدمين بنقرة واحدة",
            description = "يمكن لأي مستخدم حظر أي شخص مسيء أو تقديم بلاغ فوري من داخل أي محادثة أو منشور، ويتم التعامل مع البلاغات ومراجعتها خلال أقل من 24 ساعة."
        )
    }
}

@Composable
private fun SupportComplaintFormContent(
    reportCategory: String,
    showCategoryDropdown: Boolean,
    onShowCategoryDropdownChange: (Boolean) -> Unit,
    reportCategories: List<String>,
    onCategorySelect: (String) -> Unit,
    reportSubject: String,
    onSubjectChange: (String) -> Unit,
    reportMessage: String,
    onMessageChange: (String) -> Unit,
    contactEmail: String,
    onEmailChange: (String) -> Unit,
    isSubmittedSuccess: Boolean,
    generatedTicketId: String,
    onSubmit: () -> Unit,
    onResetForm: () -> Unit
) {
    if (isSubmittedSuccess) {
        // SUCCESS CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F231D)),
            border = BorderStroke(1.5.dp, EncryptedGreen),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(EncryptedGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = EncryptedGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "تم استلام تذكرتك بنجاح! 🎉",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "رقم التذكرة: $generatedTicketId",
                    color = EncryptedGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "يقوم فريق الدعم الفني والأمان في NEXA بمراجعة البلاغ واتخاذ الإجراءات اللازمة خلال 24 ساعة كحد أقصى. سيتم إشعارك داخل التطبيق بالنتيجة.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onResetForm,
                    colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("إرسال بلاغ أو استفسار آخر", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        // FORM
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeonPink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = NeonPink,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نموذج التواصل والدعم الفني",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "أرسل استفسارك أو أبلغ عن أي مشكلة أو محتوى مخالف",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selector
                Text("نوع البلاغ أو الطلب:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { onShowCategoryDropdownChange(true) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(reportCategory, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Icon(Icons.Default.Report, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                    }

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { onShowCategoryDropdownChange(false) },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Color(0xFF1E1B2E))
                            .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    ) {
                        reportCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Color.White, fontSize = 12.sp) },
                                onClick = {
                                    onCategorySelect(cat)
                                    onShowCategoryDropdownChange(false)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subject Field
                Text("عنوان المشكلة أو البلاغ:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = reportSubject,
                    onValueChange = onSubjectChange,
                    placeholder = { Text("مثال: بلاغ عن حساب ينشر رسائل احتيالية...", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Contact Email/Phone
                Text("البريد الإلكتروني أو الهاتف للمتابعة:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = onEmailChange,
                    placeholder = { Text("example@domain.com", color = Color.Gray, fontSize = 12.sp) },
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

                // Description Field
                Text("التفاصيل الدقيقة والوصف:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = reportMessage,
                    onValueChange = onMessageChange,
                    placeholder = { Text("يرجى كتابة تفاصيل ما حدث، أسماء الحسابات المعنية، أو نوع الخطأ التقني...", color = Color.Gray, fontSize = 12.sp) },
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إرسال البلاغ إلى فريق الدعم 🚀",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Direct Channels Quick Bar
    Spacer(modifier = Modifier.height(6.dp))
    Text("قنوات الدعم الفوري المباشرة 🌐", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Bot Support
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("المساعد الذكي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("رد فوري 24/7", color = NeonCyan, fontSize = 9.sp)
            }
        }

        // Email Support
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
            border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Email, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("البريد الرسمي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("support@nexa.app", color = Color.LightGray, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun SecurityFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827).copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, iconTint.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
