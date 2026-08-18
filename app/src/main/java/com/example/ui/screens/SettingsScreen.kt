package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import com.example.ui.components.BlueVerificationBadge
import com.example.ui.components.NexaSecurityAnd2faModal
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MajarrahViewModel,
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val profileNullable by viewModel.userProfile.collectAsState()
    val profile = profileNullable ?: com.example.data.model.UserProfile()
    val isTeenMode = profile.isTeenMode
    val isChatPinEnabled = try {
        (profile.isChatPinEnabled && profile.chatPin.isNotBlank()) || com.example.util.PinLockManager.isPinEnabled(context)
    } catch (e: Throwable) {
        false
    }
    val currentSavedPin = profile.chatPin.ifBlank { com.example.util.PinLockManager.getSavedPin(context) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(profile.isBiometricEnabled) }
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showLegalModal by remember { mutableStateOf(false) }
    var showSecurityModal by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showVerificationModal by remember { mutableStateOf(false) }
    var activeLegalTab by remember { mutableStateOf(com.example.ui.components.LegalTab.PRIVACY_POLICY) }

    if (showSecurityModal) {
        NexaSecurityAnd2faModal(
            onDismiss = { showSecurityModal = false }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حذف الحساب نهائياً (GDPR)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "تحذير: سيتم محو كافة بياناتك، محادثاتك المشفرة، منشوراتك، ومفاتيح التشفير بشكل نهائي وغير قابل للاسترداد وفقاً لمعايير الخصوصية الدولية (GDPR). هل أنت متأكد؟",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccountAndData {
                            onLogoutClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("نعم، احذف حسابي الآن", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1B1218),
            shape = RoundedCornerShape(18.dp)
        )
    }

    if (showVerificationModal) {
        AlertDialog(
            onDismissRequest = { showVerificationModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BlueVerificationBadge(size = 20.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("توثيق الحساب بالعلامة الزرقاء", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = if (profile.isVerified) "حسابك موثق حالياً بالعلامة الزرقاء الرسمية على شبكة NEXA."
                        else "العلامة الزرقاء تمنح حسابك الأولوية في النشر، الحماية من انتحال الشخصية، والظهور الموثق في المحادثات والموجز العام.",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!profile.isVerified) {
                        Text(
                            text = "حالة الطلب: مؤهل للتوثيق الفوري 🛡️",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVerificationModal = false
                        viewModel.toggleVerificationBadge(!profile.isVerified)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(
                        text = if (profile.isVerified) "إلغاء التوثيق" else "تفعيل التوثيق الآن ✨",
                        color = BackgroundDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerificationModal = false }) {
                    Text("إغلاق", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF13182B),
            shape = RoundedCornerShape(18.dp)
        )
    }

    if (showLegalModal) {
        com.example.ui.components.GooglePlayLegalModal(
            initialTab = activeLegalTab,
            onDismiss = { showLegalModal = false }
        )
    }

    if (showSetPinDialog) {
        SetChatPinModal(
            currentPin = currentSavedPin,
            onSavePin = { newPin ->
                viewModel.updateChatPin(newPin)
                showSetPinDialog = false
            },
            onDismiss = { showSetPinDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات والخصوصية",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Profile Overview Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.3f))
                            .border(1.5.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.name?.take(1)?.uppercase() ?: "م",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile?.name ?: "المستخدم",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (profile?.isVerified == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                BlueVerificationBadge(size = 18.dp)
                            }
                        }
                        Text(
                            text = profile?.phone ?: "حساب NEXA موثق",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Security & Privacy
            Text(
                text = "الأمان والحماية",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingsItemTile(
                icon = Icons.Default.Shield,
                iconTint = TeenProtectionCyan,
                title = "وضع حماية اليافعين (Teen Mode)",
                subtitle = if (isTeenMode) "مفعل: يحظر المحتوى غير المناسب" else "معطل: وصول شامل",
                trailing = {
                    Switch(
                        checked = isTeenMode,
                        onCheckedChange = { viewModel.toggleTeenMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = TeenProtectionCyan,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItemTile(
                icon = Icons.Default.Fingerprint,
                iconTint = EncryptedGreen,
                title = "قفل التطبيق بالبصمة",
                subtitle = "حماية الدخول بالبصمة الحيوية",
                trailing = {
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = {
                            biometricEnabled = it
                            viewModel.toggleBiometricAppLock(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = EncryptedGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItemTile(
                icon = Icons.Default.Lock,
                iconTint = NeonCyan,
                title = "قفل المحادثات برمز PIN",
                subtitle = if (isChatPinEnabled) "مفعل (الرمز محدد) • انقر للتعديل • ماستر 0000 متاح" else "معطل: تفتح الدردشات مباشرة بدون طلب رمز PIN",
                onClick = {
                    showSetPinDialog = true
                },
                trailing = {
                    Switch(
                        checked = isChatPinEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                showSetPinDialog = true
                            } else {
                                viewModel.toggleChatPinLock(false)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = NeonCyan,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2FA Tile
            SettingsItemTile(
                icon = Icons.Default.PhonelinkLock,
                iconTint = EncryptedGreen,
                title = "المصادقة الثنائية (2FA)",
                subtitle = if (profile.isTwoFactorEnabled) "مفعل: حماية الحساب برمز تحقق إضافي عند الدخول" else "معطل: انقر لتفعيل طبقة الأمان الإضافية",
                onClick = {
                    showSecurityModal = true
                },
                trailing = {
                    Switch(
                        checked = profile.isTwoFactorEnabled,
                        onCheckedChange = { isChecked ->
                            viewModel.toggleTwoFactorAuth(isChecked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = EncryptedGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Blue Badge Verification Tile
            SettingsItemTile(
                icon = Icons.Default.Verified,
                iconTint = NeonCyan,
                title = "توثيق الحساب (Blue Badge)",
                subtitle = if (profile.isVerified) "حسابك موثق بالعلامة الزرقاء الرسمية 🛡️" else "طلب شارة التوثيق الزرقاء لملفك ومحادثاتك",
                onClick = {
                    showVerificationModal = true
                },
                trailing = {
                    if (profile.isVerified) {
                        BlueVerificationBadge(size = 20.dp)
                    } else {
                        Text(
                            text = "طلب توثيق",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Preferences
            Text(
                text = "التفضيلات العامة",
                color = NeonPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingsItemTile(
                icon = Icons.Default.Notifications,
                iconTint = NeonPink,
                title = "الإشعارات والتنبيهات",
                subtitle = "استقبال التنبيهات الفورية والتفاعلات",
                trailing = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = NeonPink,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItemTile(
                icon = Icons.Default.PrivacyTip,
                iconTint = NeonCyan,
                title = "سياسة الخصوصية واستخدام البيانات",
                subtitle = "شرح استخدام الكاميرا والميكروفون وبيانات المستخدم (Google Play)",
                onClick = {
                    activeLegalTab = com.example.ui.components.LegalTab.PRIVACY_POLICY
                    showLegalModal = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItemTile(
                icon = Icons.Default.Security,
                iconTint = NeonPurple,
                title = "شروط الاستخدام واتفاقية المستخدم",
                subtitle = "معايير الأمان وحماية المجتمع الرقمي",
                onClick = {
                    activeLegalTab = com.example.ui.components.LegalTab.TERMS_OF_SERVICE
                    showLegalModal = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Danger Zone (GDPR Compliance & Account Actions)
            Text(
                text = "إدارة البيانات والحساب (GDPR)",
                color = Color.Red.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // GDPR Delete Account Tile
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDeleteAccountDialog = true
                    },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.Red.copy(alpha = 0.08f),
                borderColor = Color.Red.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "حذف الحساب",
                            tint = Color.Red,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "حذف الحساب وكافة البيانات نهائياً",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "محو شامل للمحادثات، المنشورات، ومفاتيح التشفير (GDPR)",
                            color = Color.Red.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logout Action Card with correct icon
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.startLoginFlow()
                        onLogoutClick()
                    },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White.copy(alpha = 0.06f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "تسجيل الخروج",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تسجيل الخروج من الحساب",
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SettingsItemTile(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {}
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            trailing()
        }
    }
}

@Composable
fun SetChatPinModal(
    currentPin: String,
    onSavePin: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf(currentPin) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun onNumberPress(num: String) {
        if (enteredPin.length < 4) {
            enteredPin += num
            errorMessage = null
        }
    }

    fun onDeletePress() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "PIN Key",
                        tint = NeonCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "تعيين رمز PIN لقفل المحادثات",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "أدخل رمز سري مكون من 4 أرقام لتأمين وقفل المحادثات",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) NeonCyan else Color.White.copy(alpha = 0.15f))
                                .border(
                                    1.dp,
                                    if (isFilled) NeonCyan else Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = NeonPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Keypad Matrix
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("clear", "0", "del")
                )

                keys.forEach { rowKeys ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowKeys.forEach { key ->
                            when (key) {
                                "clear" -> {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .clickable { enteredPin = "" },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("مسح", color = Color.LightGray, fontSize = 12.sp)
                                    }
                                }
                                "del" -> {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .clickable { onDeletePress() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = "Delete",
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.10f))
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                            .clickable { onNumberPress(key) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = key,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Developer note
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonCyan.copy(alpha = 0.1f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "وضع المطور: رمز الماستر 0000 سيفتح القفل دائماً",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إلغاء", color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            if (enteredPin.length == 4) {
                                onSavePin(enteredPin)
                            } else {
                                errorMessage = "يجب إدخال 4 أرقام كاملة"
                            }
                        },
                        modifier = Modifier.weight(1.4f),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        enabled = enteredPin.length == 4
                    ) {
                        Text("حفظ وتفعيل", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
