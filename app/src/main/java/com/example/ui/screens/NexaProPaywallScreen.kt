package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MajarrahViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexaProPaywallScreen(
    viewModel: MajarrahViewModel,
    onBackClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isProSubscriber by viewModel.isNexaProSubscriber.collectAsState()
    val dailyGenerationsUsed by viewModel.dailyAiGenerationsUsed.collectAsState()
    val remainingFreeGenerations by viewModel.remainingFreeGenerations.collectAsState()
    val context = LocalContext.current

    var selectedPlan by remember { mutableStateOf("monthly") } // "monthly" ($9.99) or "annual" ($79.99)
    var showGooglePlayBillingSheet by remember { mutableStateOf(false) }
    var isPurchasing by remember { mutableStateOf(false) }
    var purchaseSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showTermsModal by remember { mutableStateOf(false) }
    var showPrivacyModal by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pro_aura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraScale"
    )

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0D18),
                        Color(0xFF0F172A),
                        Color(0xFF060913)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Navigation Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("paywall_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonAmber.copy(alpha = 0.2f), NeonPurple.copy(alpha = 0.2f))
                                )
                            )
                            .border(1.dp, NeonAmber.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isProSubscriber) "عضوية PRO مفعّلة 👑" else "اشتراك NEXA AI PRO",
                                color = NeonAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            // 2. Hero Badge & Glow Banner
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Pulsing Glowing 3D Pro Icon
                    Box(
                        modifier = Modifier
                            .scale(auraScale)
                            .size(80.dp)
                            .shadow(24.dp, CircleShape, spotColor = NeonCyan)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        NeonCyan.copy(alpha = 0.4f),
                                        NeonPurple.copy(alpha = 0.8f),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .border(2.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Nexa Pro",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Nexa AI Pro",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ارتقِ إلى أقصى طاقات الذكاء الاصطناعي مع Nexa AI Pro",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "أطلق العنان للإبداع اللامحدود مع توليد صور 4K، فيديوهات Veo سينمائية، وسرعة استجابة فائقة فورية.",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // 3. Free Usage Status Bar (if not pro)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isProSubscriber) Color(0xFF132A1C) else Color(0xFF161B2E)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isProSubscriber) EncryptedGreen.copy(alpha = 0.6f) else NeonCyan.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isProSubscriber) Icons.Default.CheckCircle else Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = if (isProSubscriber) EncryptedGreen else NeonAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isProSubscriber) "توليد وسائط غير محدود (Nexa Pro Active)" else "الحد اليومي للتوليد المجاني",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isProSubscriber) "أنت تستمتع بكامل ميزات السرعة والجودة الفائقة 4K"
                                    else "تم استخدام $dailyGenerationsUsed من أصل 3 توليدات مجانية اليوم",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (!isProSubscriber) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (remainingFreeGenerations == 0) Color(0xFFEF4444).copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.15f))
                                    .border(1.dp, if (remainingFreeGenerations == 0) Color(0xFFEF4444) else NeonCyan, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (remainingFreeGenerations == 0) "نفد الحد اليومي" else "متبقي $remainingFreeGenerations",
                                    color = if (remainingFreeGenerations == 0) Color(0xFFEF4444) else NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 4. Features List Section (Requested Specification)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10162A)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "المزايا الحصرية لاشتراك Nexa AI Pro",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        ProFeatureItem(
                            icon = Icons.Default.AutoAwesome,
                            iconTint = NeonCyan,
                            title = "Unlimited AI Chat",
                            titleAr = "محادثات ذكاء اصطناعي غير محدودة",
                            description = "تواصل فوري مع أحدث نماذج الذكاء الاصطناعي التوليدي دون قيود على عدد الرسائل أو طول النص."
                        )

                        ProFeatureItem(
                            icon = Icons.Default.Image,
                            iconTint = NeonPink,
                            title = "High-Quality Images",
                            titleAr = "توليد صور فائقة الجودة بدقة 4K",
                            description = "إنشاء وتصميم صور احترافية سينمائية بنماذج Imagen 3 و Gemini AI بدقة فائقة وبجميع الأبعاد."
                        )

                        ProFeatureItem(
                            icon = Icons.Default.Videocam,
                            iconTint = NeonAmber,
                            title = "HD AI Video Creation",
                            titleAr = "إنشاء فيديوهات سينمائية بالذكاء الاصطناعي (Veo)",
                            description = "تحريك وتصيير مقاطع فيديو ومؤثرات بصرية سينمائية متحركة بدقة 60fps من الأوامر النصية."
                        )

                        ProFeatureItem(
                            icon = Icons.Default.FlashOn,
                            iconTint = EncryptedGreen,
                            title = "Ultra-Fast Speed",
                            titleAr = "سرعة استجابة فائقة ومعالجة فورية",
                            description = "أولوية معالجة قصوى على خوادم المعالجة السحابية الفائقة بدون أي طوابير انتظار."
                        )

                        ProFeatureItem(
                            icon = Icons.Default.Shield,
                            iconTint = NeonPurple,
                            title = "Encrypted Cloud & VIP Perks",
                            titleAr = "تشفير سحابي وشارة VIP الماسية",
                            description = "حفظ السجل المشفر سحابياً، تجربة خالية تماماً من الإعلانات، والشارة الماسية المضيئة."
                        )
                    }
                }
            }

            // 5. Subscription Plan Selector
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "اختر خطة الاشتراك",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Monthly Plan ($9.99/Month)
                    PlanSelectionCard(
                        title = "الاشتراك الشهري",
                        subtitle = "الأكثر شعبية ومرونة",
                        price = "$9.99",
                        period = "شهرياً",
                        badge = "خيار موصى به",
                        isSelected = selectedPlan == "monthly",
                        onClick = { selectedPlan = "monthly" }
                    )

                    // Annual Plan ($79.99/Year - Save 33%)
                    PlanSelectionCard(
                        title = "الاشتراك السنوي",
                        subtitle = "وفر 33% + تجربة 3 أيام مجانية",
                        price = "$79.99",
                        period = "سنوياً ($6.66/شهر)",
                        badge = "توفير 33%",
                        isSelected = selectedPlan == "annual",
                        onClick = { selectedPlan = "annual" }
                    )
                }
            }

            // 6. Primary Subscribe CTA Button
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (isProSubscriber) {
                                // Already pro, open management
                                showGooglePlayBillingSheet = true
                            } else {
                                showGooglePlayBillingSheet = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = NeonCyan)
                            .testTag("subscribe_pro_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            NeonCyan,
                                            NeonPurple,
                                            NeonPink
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isProSubscriber) Icons.Default.Check else Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = BackgroundDark,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isProSubscriber) "إدارة الاشتراك في Google Play"
                                    else if (selectedPlan == "monthly") "متابعة الاشتراك - $9.99 / شهرياً"
                                    else "متابعة الاشتراك - $79.99 / سنوياً",
                                    color = BackgroundDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // English Alternative Display
                    Text(
                        text = if (selectedPlan == "monthly") "Continue - $9.99/Month" else "Continue - $79.99/Year",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 7. Bottom Disclaimer (Terms & Privacy)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إلغاء في أي وقت",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = " • ",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "الشروط والأحكام",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showTermsModal = true }
                                .testTag("terms_link")
                        )
                        Text(
                            text = " • ",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "سياسة الخصوصية",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showPrivacyModal = true }
                                .testTag("privacy_link")
                        )
                    }

                    Text(
                        text = "Cancel anytime • Terms & Privacy",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GppGood,
                            contentDescription = null,
                            tint = EncryptedGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "نظام الفوترة والدفع الآمن عبر متجر Google Play معتمد ومشفر بالكامل",
                            color = EncryptedGreen.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Google Play In-App Purchase Flow Bottom Sheet Simulation
    if (showGooglePlayBillingSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                if (!isPurchasing) showGooglePlayBillingSheet = false
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF1E293B),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Google Play Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = "Google Play",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Google Play Billing System",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "الدفع الفوري داخل التطبيق (In-App Purchases)",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { showGooglePlayBillingSheet = false },
                        enabled = !isPurchasing
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Product details row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "اشتراك Nexa AI Pro (${if (selectedPlan == "monthly") "شهري" else "سنوي"})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "منصة NEXA للتواصل والتسوق الذكي",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = if (selectedPlan == "monthly") "$9.99/شهر" else "$79.99/سنة",
                        color = NeonCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                // Payment Method
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "بطاقة مدى / Visa •••• 8842",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Google Pay • ${userProfile?.name ?: "المستخدم"}",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EncryptedGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Success or In-Progress Indicator
                if (isPurchasing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "جاري تأكيد عملية الشراء عبر متجر Google Play...",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                // Action Confirm Button
                Button(
                    onClick = {
                        isPurchasing = true
                        coroutineScope.launch {
                            delay(1200)
                            viewModel.subscribeToNexaPro(
                                planId = if (selectedPlan == "monthly") "nexa_pro_monthly" else "nexa_pro_annual",
                                activity = context as? Activity
                            )
                            isPurchasing = false
                            showGooglePlayBillingSheet = false
                            onBackClick()
                        }
                    },
                    enabled = !isPurchasing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_google_play_purchase"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isPurchasing) "جاري المعالجة..." else "تأكيد الاشتراك بنقرة واحدة (1-Tap Buy)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "سيتم تجديد الاشتراك تلقائياً ويمكنك إلغاؤه في أي وقت عبر إعدادات اشتراكات Google Play.",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Terms of Service Modal
    if (showTermsModal) {
        TermsAndPrivacyDialog(
            title = "الشروط والأحكام (Terms of Service)",
            content = "1. يقدم اشتراك Nexa AI Pro ميزات توليد وسائط متقدمة وروبوتات ذكاء اصطناعي فائقة السرعة.\n2. يتم الدفع والفوترة بشكل دوري عبر حساب Google Play الخاص بك.\n3. يمكنك إلغاء التجديد التلقائي في أي وقت قبل نهاية الفترة الحالية بـ 24 ساعة.\n4. تخضع الأوامر المولدة للسياسات الإرشادية لمنع المحتوى الضار أو المخالف.",
            onDismiss = { showTermsModal = false }
        )
    }

    // Privacy Policy Modal
    if (showPrivacyModal) {
        TermsAndPrivacyDialog(
            title = "سياسة الخصوصية (Privacy Policy)",
            content = "1. يتم تشفير كافة المحادثات والأوامر النصية بتقنية 256-bit End-to-End Encryption.\n2. تلتزم NEXA بالامتثال الصارم لمعايير GDPR العالمية وقوانين حماية البيانات الشخصية.\n3. لا يتم بيع بياناتك أو استخدام وسائطك الخاصة لتدريب النماذج دون موافقة صريحة.\n4. يحق لك طلب حذف حسابك وبياناتك بالكامل في أي وقت من إعدادات الملف الشخصي.",
            onDismiss = { showPrivacyModal = false }
        )
    }
}

@Composable
fun ProFeatureItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    titleAr: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.15f))
                .border(1.dp, iconTint.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = titleAr,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "($title)",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = Color.LightGray,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun PlanSelectionCard(
    title: String,
    subtitle: String,
    price: String,
    period: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("plan_card_${if (isSelected) "selected" else "unselected"}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF16213E) else Color(0xFF0F172A)
        ),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = NeonCyan,
                        unselectedColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                color = if (isSelected) NeonCyan else Color.LightGray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = price,
                    color = if (isSelected) NeonCyan else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = period,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun TermsAndPrivacyDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Text(
                        text = content,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("موافق", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
