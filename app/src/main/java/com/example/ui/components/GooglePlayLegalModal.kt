package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

enum class LegalTab {
    PRIVACY_POLICY,
    TERMS_OF_SERVICE,
    ACCOUNT_DELETION
}

@Composable
fun GooglePlayLegalModal(
    initialTab: LegalTab = LegalTab.PRIVACY_POLICY,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(initialTab) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, NeonCyan, RoundedCornerShape(28.dp)),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = NeonCyan,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "وثائق NEXA القانونية (Google Play 2026)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == LegalTab.PRIVACY_POLICY) NeonCyan else Color.Transparent)
                            .clickable { activeTab = LegalTab.PRIVACY_POLICY }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = if (activeTab == LegalTab.PRIVACY_POLICY) BackgroundDark else Color.LightGray,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "الخصوصية",
                                color = if (activeTab == LegalTab.PRIVACY_POLICY) BackgroundDark else Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == LegalTab.TERMS_OF_SERVICE) NeonPurple else Color.Transparent)
                            .clickable { activeTab = LegalTab.TERMS_OF_SERVICE }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = if (activeTab == LegalTab.TERMS_OF_SERVICE) Color.White else Color.LightGray,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "الشروط",
                                color = if (activeTab == LegalTab.TERMS_OF_SERVICE) Color.White else Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == LegalTab.ACCOUNT_DELETION) Color.Red else Color.Transparent)
                            .clickable { activeTab = LegalTab.ACCOUNT_DELETION }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "حذف الحساب",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Document Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        when (activeTab) {
                            LegalTab.PRIVACY_POLICY -> PrivacyPolicyTextContent()
                            LegalTab.TERMS_OF_SERVICE -> TermsOfServiceTextContent()
                            LegalTab.ACCOUNT_DELETION -> AccountDeletionTextContent()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
 text ="موافق وفهمت جميع الشروط والقوانين",
                        color = BackgroundDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyPolicyTextContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "سياسة الخصوصية والأمان - منصة NEXA (تحديث Google Play 2026)",
            color = NeonCyan,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
        )

        Text(
            text = "تلتزم منصة NEXA بحماية خصوصية مستخدميها بأعلى معايير الأمان العالمية المتوافقة التامة مع إرشادات سلامة البيانات لمتجر Google Play Store لعام 2026 وقوانين حماية البيانات العامة (GDPR).",
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        SectionHeader("1. البيانات التي نجمعها والأذونات المطلوبة (Permissions & Data Usage)")
        Text(
            text = "• الكاميرا (Camera): تُستخدم لالتقاط الصور ومقاطع الفيديو ونشر القصص والريلز والمشاركة في المحادثات.\n• الميكروفون (Microphone): يُستخدم لتسجيل الرسائل الصوتية الحقيقية والتحدث مع المساعد الصوتي الذكي NEXA AI.\n• معرض الصور والوسائط (Media & Storage): للوصول إلى الصور المختارة لحفظها أو مشاركتها بأمان.\n• الإشعارات (Push Notifications): لتنبيهك فوراً بالرسائل الجديدة والتفاعلات مع المحتوى وتحديثات الأمان.\n• معلومات الحساب: الاسم ورقم الهاتف والعمر لتفعيل ميزات الحماية والتخصيص.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        SectionHeader("2. حماية بيانات الأطفال والناشئة (Children & Teen Safety)")
        Text(
            text = "تعتمد منصة NEXA وضع حماية الناشئة (Teen Protection Mode) تلقائياً للمستخدمين تحت سن 18 عاماً، حيث تمنع عرض المحتوى الحساس، وتقوم بفلترة المنتجات غير المناسبة، وتتيح إشرافاً شاملاً للوالدين.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        SectionHeader("3. التشفير والحماية 100% (End-to-End Encryption)")
        Text(
            text = "تتم عملية تشفير جميع المحادثات والدردشات الخاصة برمز PIN وشفرات عسكرية غير قابلة للاختراق، ولا يتم مشاركة أي بيانات مع أطراف ثالثة أو بيعها لأغراض إعلانية.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        SectionHeader("4. حق حذف الحساب والبيانات (Account & Data Deletion)")
        Text(
            text = "يحق لك في أي وقت حذف حسابك وجميع بياناتك نهائياً بلمسة واحدة من داخل إعدادات التطبيق، حيث سيتم إزالة كافة مقاطع الفيديو، التعليقات، الرسائل، وسجل المحفظة فوراً دون إمكانية استرجاعها.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun TermsOfServiceTextContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "شروط وأحكام الاستخدام - منصة NEXA (Google Play UGC Policy 2026)",
            color = NeonPurple,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
        )

        Text(
            text = "باستخدامك لتطبيق NEXA، فإنك توافق التزامك التام بكافة الشروط والأحكام وسياسات المحتوى المنشأ بواسطة المستخدم (UGC) لحماية المجتمع الرقمي.",
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        SectionHeader("1. سياسة منع المحتوى المسيء والتبليغ (Zero Tolerance for Abuse)")
        Text(
            text = "تطبيق NEXA يطبق سياسة الصارم الصفرية (Zero Tolerance) ضد أي خطابات كراهية، تنمر، محتوى غير لائق، أو إساءة. يتاح للمستخدمين زران فوربان للتبليغ والحظر على أي فيديو أو تعليق، وسيتم إيقاف الحسابات المخالفة فوراً.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        SectionHeader("2. شروط صندوق الربح لصناع المحتوى (Creator Monetization)")
        Text(
            text = "• يتطلب التأهل لصندوق الأرباح الوصول لـ 1,000,000 مشاهدة حقيقية على مقاطع NEXA Reels.\n• يجب أن يكون كافة المحتوى أصلياً وغير منقول أو مخترق لحقوق الملكية الفكرية.\n• يحق للمنصة تجميد أرباح الحسابات التي تستخدم وسائل وهمية لرفع المشاهدات.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        SectionHeader("3. نظام الأوسمة الملكية (Royal Badges System)")
        Text(
            text = "تُمنح الأوسمة الفضية (100K) والذهبية الكريستالية 3D (500K) والماسي VIP Diamond Aura (1M) بناءً على عدد المتابعين الحقيقيين وتظهر تلقائياً حول صورة البروفايل والتعليقات.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun AccountDeletionTextContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "سياسة حذف الحساب والبيانات (Google Play Data Deletion Policy 2026)",
            color = Color.Red,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
        )

        Text(
            text = "تلتزم منصة NEXA بالشفافية التامة وتوفير خيار الحذف الكامل والمباشر للحساب وكافة البيانات المرتبطة به وفقاً لمتطلبات متجر Google Play.",
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        SectionHeader("1. كيفية تقديم طلب حذف الحساب")
        Text(
            text = "يمكنك طلب حذف حسابك فوراً بزيارة شاشة الملف الشخصي (Profile) -> ثم اختيار الإعدادات والأمان -> والضغط على زر \"طلب حذف الحساب وجميع البيانات فوراً\".",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        SectionHeader("2. البيانات التي يتم إزالتها نهائياً")
        Text(
            text = "• بيانات الملف الشخصي والرمز التعريفي المشفر.\n• كافة منشوراتك، مقاطع الريلز، والقصص اليومية.\n• كافة الرسائل والمحادثات المشفرة والتعليقات.\n• سجل الأرباح والمعاملات في محفظة NEXA.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        SectionHeader("3. المهل الزمنية والإلغاء")
        Text(
            text = "يتم تنفيذ عملية المسح الفوري فور تأكيدك للطلب دون تسجيل أي نسخ احتياطية.",
            color = Color.LightGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}
