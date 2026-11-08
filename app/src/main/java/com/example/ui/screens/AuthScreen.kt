package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Surface
import com.example.ui.theme.NeonPink
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import com.example.data.firebase.NexaPhoneAuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.util.SystemBiometricAuthManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.LoginStep
import com.example.ui.MajarrahViewModel
import com.example.ui.components.BiometricDialog
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

data class SuggestedContact(
    val id: String,
    val name: String,
    val username: String,
    val isAppUser: Boolean,
    val mutualFriends: Int = 0
)

@Composable
fun AuthScreen(
    viewModel: MajarrahViewModel,
    onAuthSuccess: () -> Unit
) {
    val step by viewModel.loginStep.collectAsState()

    // Form states
    val firstName by viewModel.regFirstName.collectAsState()
    val lastName by viewModel.regLastName.collectAsState()
    val birthDay by viewModel.regBirthDay.collectAsState()
    val birthMonth by viewModel.regBirthMonth.collectAsState()
    val birthYear by viewModel.regBirthYear.collectAsState()
    val calculatedAge by viewModel.userAge.collectAsState()
    val credentialType by viewModel.regCredentialType.collectAsState()
    val username by viewModel.regUsername.collectAsState()
    val phone by viewModel.phoneNumber.collectAsState()
    val password by viewModel.regPassword.collectAsState()
    val confirmPassword by viewModel.regConfirmPassword.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabledForAccount.collectAsState()
    val bio by viewModel.regBio.collectAsState()
    val selectedAvatarIndex by viewModel.regAvatarIndex.collectAsState()
    val isContactsSynced by viewModel.isContactsSynced.collectAsState()

    val otpCode by viewModel.otpCode.collectAsState()

    var showBiometricModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        Color(0xFF140D2F),
                        BackgroundDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Header (Always visible unless in deep step)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonPurple.copy(alpha = 0.25f))
                        .border(1.5.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nexa_launcher_icon_1786810216912),
                        contentDescription = "NEXA App Logo",
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "NEXA",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Progress Bar (for onboarding steps 1 to 5)
            val currentProgress = when (step) {
                is LoginStep.Step1FullName -> 0.2f
                is LoginStep.Step2DateOfBirth -> 0.4f
                is LoginStep.Step3Credentials -> 0.6f
                is LoginStep.Step4Password -> 0.8f
                is LoginStep.Step5Biometrics -> 0.95f
                is LoginStep.ProfileAndSocialDiscovery -> 1.0f
                else -> 0.0f
            }

            if (currentProgress > 0f) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                when (step) {
                                    is LoginStep.Step1FullName -> viewModel.navigateToStep(LoginStep.Welcome)
                                    is LoginStep.Step2DateOfBirth -> viewModel.navigateToStep(LoginStep.Step1FullName)
                                    is LoginStep.Step3Credentials -> viewModel.navigateToStep(LoginStep.Step2DateOfBirth)
                                    is LoginStep.Step4Password -> viewModel.navigateToStep(LoginStep.Step3Credentials)
                                    is LoginStep.Step5Biometrics -> viewModel.navigateToStep(LoginStep.Step4Password)
                                    is LoginStep.ProfileAndSocialDiscovery -> viewModel.navigateToStep(LoginStep.Step5Biometrics)
                                    else -> viewModel.navigateToStep(LoginStep.Welcome)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Back Step",
                                tint = NeonCyan
                            )
                        }

                        val stepNumberText = when (step) {
                            is LoginStep.Step1FullName -> "الخطوة 1 من 5 (الاسم الشخصي)"
                            is LoginStep.Step2DateOfBirth -> "الخطوة 2 من 5 (العمر والبداية)"
                            is LoginStep.Step3Credentials -> "الخطوة 3 من 5 (بيانات الدخول)"
                            is LoginStep.Step4Password -> "الخطوة 4 من 5 (كلمة السر)"
                            is LoginStep.Step5Biometrics -> "الخطوة 5 من 5 (الحماية البايومترية)"
 is LoginStep.ProfileAndSocialDiscovery ->"إعداد البروفايل والأصدقاء"
                            else -> ""
                        }

                        Text(
                            text = stepNumberText,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = currentProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonCyan,
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Main Content Body Switcher
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                shape = RoundedCornerShape(26.dp)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    when (step) {
                        is LoginStep.TwoFactorAuth -> {
                            TwoFactorAuthStepView(
                                viewModel = viewModel,
                                onSuccess = onAuthSuccess,
                                onBack = { viewModel.navigateToStep(LoginStep.ExistingLogin) }
                            )
                        }
                        is LoginStep.Welcome, is LoginStep.Completed, is LoginStep.ExistingLogin -> {
                            ModernTabbedAuthView(
                                viewModel = viewModel,
                                onAuthSuccess = onAuthSuccess
                            )
                        }
                        else -> {
                            WelcomeOnboardingView(
                                onStartRegistration = { viewModel.navigateToStep(LoginStep.Step1FullName) },
                                onStartLogin = { viewModel.navigateToStep(LoginStep.ExistingLogin) }
                            )
                        }
                    }
                }
            }
        }

        // Biometric & OTP Verification Overlay
        if (showBiometricModal) {
            BiometricDialog(
                defaultPhoneOrEmail = phone,
                onDismiss = { showBiometricModal = false },
                onSuccess = {
                    showBiometricModal = false
                    if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null) {
                        viewModel.completeProfileRegistration()
                        onAuthSuccess()
                    }
                }
            )
        }
    }
}

// =========================================================
// 1⃣ Welcome & Onboarding View
// =========================================================
@Composable
fun WelcomeOnboardingView(
    onStartRegistration: () -> Unit,
    onStartLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "مرحباً بك في عالم NEXA المستقبلي",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "تواصل اجتماعي نيون، تسوق ذكي بخصومات فورا، وحماية تلقائية كاملة للجميع",
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Onboarding Carousel Highlights
        val highlights = listOf(
            Triple("مجتمعات حية", "دردشة مشفرة وقصص تفاعلية نيون", NeonPurple),
            Triple("حماية الناشئة", "وضع حماية أبوي آلي لأقل من 18 سنة", TeenProtectionCyan),
            Triple("ذكاء NEXA AI", "تسوق مباشر ومساعد محادثات فائق", NeonCyan)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(highlights) { item ->
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(item.third.copy(alpha = 0.15f))
                        .border(1.dp, item.third.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(text = item.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = item.second, color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Action Buttons
        Button(
            onClick = onStartRegistration,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "إنشاء حساب جديد",
                color = BackgroundDark,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onStartLogin,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
        ) {
            Text(
                text = "تسجيل الدخول لحسابك الحالي",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// =========================================================
// 2⃣ Step 1: Full Name View
// =========================================================
@Composable
fun Step1FullNameView(
    firstName: String,
    lastName: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val isValid = firstName.trim().isNotBlank() && lastName.trim().isNotBlank()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
 text ="الخطوة 1: أدخل اسمك الكامل",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "سيظهر هذا الاسم في ملفك الشخصي وفي تفاعلات أصدقائك عبر NEXA.",
            color = Color.LightGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("الاسم الأول", color = NeonCyan) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("اسم العائلة", color = NeonCyan) },
            leadingIcon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            enabled = isValid,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
 Text("التالي (تاريخ الميلاد)", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =========================================================
// 3⃣ Step 2: Date of Birth & Dynamic Age Check View
// =========================================================
@Composable
fun Step2DateOfBirthView(
    birthDay: Int,
    birthMonth: Int,
    birthYear: Int,
    calculatedAge: Int,
    onDateChange: (Int, Int, Int) -> Unit,
    onAgeSliderChange: (Int) -> Unit,
    onNext: () -> Unit
) {
    val isUnder18 = calculatedAge < 18

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
 text ="الخطوة 2: تاريخ الميلاد وتحديد العمر",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "يتم التحقق آلياً لتفعيل قيود الحماية والأمان الأبوي تلقائياً لمن هم دون 18 سنة.",
            color = Color.LightGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Age Display Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isUnder18) TeenProtectionCyan.copy(alpha = 0.2f) else NeonPurple.copy(alpha = 0.2f))
                .border(1.dp, if (isUnder18) TeenProtectionCyan else NeonPurple, RoundedCornerShape(16.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "العمر المحسوب: $calculatedAge سنة",
                    color = if (isUnder18) TeenProtectionCyan else NeonPurple,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "تاريخ الميلاد: $birthYear / $birthMonth / $birthDay",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "سحب شريط تحديد السنة/العمر السريع:", color = Color.Gray, fontSize = 11.sp)
        Slider(
            value = calculatedAge.toFloat(),
            onValueChange = { onAgeSliderChange(it.toInt()) },
            valueRange = 10f..60f,
            steps = 50,
            colors = SliderDefaults.colors(
                thumbColor = if (isUnder18) TeenProtectionCyan else NeonPurple,
                activeTrackColor = if (isUnder18) TeenProtectionCyan else NeonPurple,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // DYNAMIC AGE CHECK LOGIC: PERSISTENT UNDER-18 MODE BANNER
        if (isUnder18) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(TeenProtectionCyan.copy(alpha = 0.22f))
                    .border(1.5.dp, TeenProtectionCyan, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Parental Shield",
                        tint = TeenProtectionCyan,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
 text ="وضع الناشئة والأمان الأبوي مفعل تلقائياً",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "لأن عمرك أقل من 18 سنة. حظر تام للمحتوى غير المناسب، وتفعيل تصفية صارمة للسماح بالمواد التعليمية والترفيهية والدينية العائلية فقط.",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NeonPurple.copy(alpha = 0.2f))
                    .border(1.dp, NeonPurple, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Adult Full Mode",
                        tint = NeonPurple,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
 text ="وضع البالغين والمنصة الكاملة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "تمتلك صلاحية الوصول إلى جميع الميزات والمجتمعات في NEXA.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isUnder18) TeenProtectionCyan else NeonPurple
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
 Text("التالي (وسائل تسجيل الدخول)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =========================================================
// 4⃣ Step 3: Credentials View (Username / Phone / Google)
// =========================================================
@Composable
fun Step3CredentialsView(
    credentialType: String,
    username: String,
    phone: String,
    otpCode: String,
    onCredentialTypeChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = remember(context) { NexaPhoneAuthManager.findActivity(context) }

    var selectedCountryCode by remember { mutableStateOf(NexaPhoneAuthManager.supportedCountryCodes.first()) }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var isErrorBanner by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    var resendTimerSeconds by remember { mutableIntStateOf(60) }
    var isTimerActive by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerActive, resendTimerSeconds) {
        if (isTimerActive && resendTimerSeconds > 0) {
            delay(1000L)
            resendTimerSeconds -= 1
        } else if (resendTimerSeconds == 0) {
            isTimerActive = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "الخطوة 3: اختر وسيلة الدخول وتأكيد الحساب",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "اختر الطريقة الأنسب لك لربط حسابك وتأمينه عبر Firebase Phone Auth.",
            color = Color.LightGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Credential Type Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val tabs = listOf("email" to "البريد الإلكتروني", "phone" to "رقم الجوال", "google" to "Google")
            tabs.forEach { (type, label) ->
                val selected = credentialType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) NeonCyan else Color.Transparent)
                        .clickable { onCredentialTypeChange(type) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) BackgroundDark else Color.LightGray,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Status Toast Banner (Red for error, Green for success)
        statusMessage?.let { msg ->
            val bannerBg = if (isErrorBanner) NeonPink.copy(alpha = 0.2f) else EncryptedGreen.copy(alpha = 0.2f)
            val bannerBorder = if (isErrorBanner) NeonPink else EncryptedGreen
            val iconTint = if (isErrorBanner) NeonPink else EncryptedGreen

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(12.dp),
                color = bannerBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isErrorBanner) Icons.Default.Error else Icons.Default.Check,
                        contentDescription = null,
                        tint = iconTint
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        when (credentialType) {
            "email" -> {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("البريد الإلكتروني", color = NeonCyan) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "سيتم استخدام البريد الإلكتروني لتأكيد وتوثيق الحساب", color = EncryptedGreen, fontSize = 11.sp)
                }
            }
            "phone" -> {
                // Country Code Selection Row
                Text("اختر كود الدولة ورقم الهاتف:", color = NeonCyan, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = NexaPhoneAuthManager.supportedCountryCodes) { cc ->
                        val selected = selectedCountryCode.code == cc.code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) NeonCyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                                .border(1.dp, if (selected) NeonCyan else Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .clickable { selectedCountryCode = cc }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${cc.flag} ${cc.code}",
                                color = if (selected) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = onPhoneChange,
                        label = { Text("رقم الجوال (${selectedCountryCode.code})", color = NeonCyan) },
                        leadingIcon = { Text(selectedCountryCode.flag, fontSize = 16.sp, modifier = Modifier.padding(start = 10.dp)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            statusMessage = null
                            isErrorBanner = false

                            NexaPhoneAuthManager.sendSmsOtp(
                                activity = activity,
                                phoneNumber = phone,
                                countryCode = selectedCountryCode.code,
                                onCodeSent = { verId ->
                                    isLoading = false
                                    verificationId = verId
                                    isErrorBanner = false
                                    statusMessage = "تم إرسال رمز SMS OTP عبر Firebase بنجاح إلى ${selectedCountryCode.code} $phone!"
                                    resendTimerSeconds = 60
                                    isTimerActive = true
                                },
                                onError = { err ->
                                    isLoading = false
                                    isErrorBanner = true
                                    statusMessage = err
                                },
                                onAutoVerified = {
                                    isLoading = false
                                    isErrorBanner = false
                                    statusMessage = "تم التحقق التلقائي بـ Firebase Auth!"
                                    onNext()
                                }
                            )
                        },
                        enabled = !isLoading && phone.trim().length >= 7,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sms, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إرسال OTP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) onOtpChange(it) },
                    label = { Text("رمز التحقق OTP (6 أرقام)", color = NeonPurple) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonPurple) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Resend OTP Row with Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTimerActive) "إعادة إرسال الرمز خلال: ${resendTimerSeconds}s" else "لم تتلقَ رمز SMS؟",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )

                    OutlinedButton(
                        onClick = {
                            if (!isTimerActive && !isLoading) {
                                isLoading = true
                                statusMessage = null
                                isErrorBanner = false

                                NexaPhoneAuthManager.sendSmsOtp(
                                    activity = activity,
                                    phoneNumber = phone,
                                    countryCode = selectedCountryCode.code,
                                    onCodeSent = { verId ->
                                        isLoading = false
                                        verificationId = verId
                                        isErrorBanner = false
                                        statusMessage = "تم إعادة إرسال رمز OTP بنجاح!"
                                        resendTimerSeconds = 60
                                        isTimerActive = true
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        isErrorBanner = true
                                        statusMessage = err
                                    },
                                    onAutoVerified = {
                                        isLoading = false
                                        isErrorBanner = false
                                        statusMessage = "تم التحقق التلقائي!"
                                        onNext()
                                    }
                                )
                            }
                        },
                        enabled = !isTimerActive && !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (!isTimerActive) NeonCyan else Color.Gray)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = if (!isTimerActive) NeonCyan else Color.Gray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isTimerActive) "انتظر (${resendTimerSeconds}s)" else "إعادة إرسال",
                                color = if (!isTimerActive) NeonCyan else Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            "google" -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "تم اختيار Google One-Tap Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "حسابك المرتبط: user@gmail.com", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (credentialType == "phone") {
                    isLoading = true
                    statusMessage = null
                    isErrorBanner = false

                    NexaPhoneAuthManager.verifyOtpCode(
                        verificationId = verificationId,
                        otpCode = otpCode,
                        onSuccess = {
                            isLoading = false
                            isErrorBanner = false
                            onNext()
                        },
                        onError = { err ->
                            isLoading = false
                            isErrorBanner = true
                            statusMessage = err
                        }
                    )
                } else {
                    onNext()
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
            } else {
                Text("التالي (كلمة السر والخصوصية)", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// =========================================================
// 5⃣ Step 4: Password Creation & Autofill Integration View
// =========================================================
@Composable
fun Step4PasswordView(
    password: String,
    confirmPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onNext: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val isLengthOk = password.length >= 6
    val isMatch = password == confirmPassword && password.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
 text ="الالخطوة 4: كلمة السر ومدير الكلمات",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "يدعم التطبيق التعبئة التلقائية وحفظ الحساب في Google Password Manager.",
            color = Color.LightGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field with Autofill & Show/Hide
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("كلمة السر الجدية", color = NeonCyan) },
            leadingIcon = { Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility",
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("تأكيد كلمة السر", color = NeonCyan) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Google Autofill / Password Manager Card Indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(NeonPurple.copy(alpha = 0.18f))
                .border(1.dp, NeonPurple.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
 text ="سيتم طلب حفظ الحساب تلقائياً في Google Password Manager",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            enabled = isLengthOk && isMatch,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
 Text("التالي (تفعيل البصمة)", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =========================================================
// 6⃣ Step 5: Biometric Authentication Setup View
// =========================================================
@Composable
fun Step5BiometricsView(
    isBiometricEnabled: Boolean,
    onToggleBiometric: (Boolean) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
 text ="الخطوة 5: حماية بصمة الأصبع",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "تتيح لك البصمة دخول حسابك واسترجاعه بأمان تام ودون كشف كلمة السر.",
            color = Color.LightGray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        val context = LocalContext.current
        fun testAndToggleBiometrics(enable: Boolean) {
            if (enable) {
                SystemBiometricAuthManager.authenticate(
                    context = context,
                    title = "ربط وتفعيل البصمة الحيوية - NEXA",
                    subtitle = "التحقق من مستشعر الجهاز (البصمة/الوجه/قفل الشاشة)",
                    negativeButtonText = "إلغاء",
                    onSuccess = {
                        onToggleBiometric(true)
                    },
                    onError = {
                        onToggleBiometric(false)
                    },
                    onFallbackToPassword = {
                        onToggleBiometric(false)
                    }
                )
            } else {
                onToggleBiometric(false)
            }
        }

        // Interactive Fingerprint Scan Visual Node
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(if (isBiometricEnabled) NeonCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f))
                .border(2.dp, if (isBiometricEnabled) NeonCyan else Color.Gray, CircleShape)
                .clickable { testAndToggleBiometrics(!isBiometricEnabled) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Scan Fingerprint",
                tint = if (isBiometricEnabled) NeonCyan else Color.Gray,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "تفعيل بصمة الأصبع للحساب",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
 text = if (isBiometricEnabled)"البصمة مفعلة ومتصلة بمستشعر الجهاز" else"البصمة غير مفعلة حالياً",
                    color = if (isBiometricEnabled) EncryptedGreen else Color.Gray,
                    fontSize = 11.sp
                )
            }
            Switch(
                checked = isBiometricEnabled,
                onCheckedChange = { testAndToggleBiometrics(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = NeonCyan)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
 Text("تأكيد البصمة والذهاب للبروفايل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSkip,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("تخطي هذه الخطوة الآن", color = Color.LightGray, fontSize = 12.sp)
        }
    }
}

// =========================================================
// 7⃣ Profile Setup & Social Discovery View (Contact Sync / Friends)
// =========================================================
@Composable
fun ProfileAndSocialDiscoveryView(
    bio: String,
    selectedAvatarIndex: Int,
    isContactsSynced: Boolean,
    onBioChange: (String) -> Unit,
    onAvatarSelect: (Int) -> Unit,
    onContactsSyncedChange: (Boolean) -> Unit,
    onCompleteAll: () -> Unit
) {
    val suggestedContacts = remember {
        listOf(
            SuggestedContact("1", "سارة النمر", "@sara_alnimr", isAppUser = true, mutualFriends = 12),
            SuggestedContact("2", "خالد الحربي", "@khalid_h", isAppUser = true, mutualFriends = 8),
            SuggestedContact("3", "نورا القحطاني", "@nora_q", isAppUser = true, mutualFriends = 19),
            SuggestedContact("4", "فيصل العتيبي", "@faisal_u", isAppUser = false, mutualFriends = 0)
        )
    }

    var followedSet by remember { mutableStateOf<Set<String>>(setOf("1")) }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Text(
 text ="إعداد الملف الشخصي والاكتشاف الاجتماعي",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "اختر صورتك الشخصية، اكتب نبذة، واكتشف أصدقاءك في مجتمع NEXA الاجتماعي.",
                color = Color.LightGray,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Bio Field
        item {
            OutlinedTextField(
                value = bio,
                onValueChange = onBioChange,
                label = { Text("نبذة عنك (Bio)", color = NeonCyan) },
                singleLine = false,
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Contact Sync Permission Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NeonPurple.copy(alpha = 0.2f))
                    .border(1.dp, NeonPurple, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.Contacts, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
 Text(text ="مزامنة جهات الاتصال", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "اكتشف أصدقاءك في جهازك واقترحهم للتواصل", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = isContactsSynced,
                        onCheckedChange = onContactsSyncedChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = NeonCyan)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Suggested Friends List
        if (isContactsSynced) {
            item {
                Text(text = "أصدقاء اقترحهم لك النظام بناءً على جهات الاتصال:", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            itemsIndexed(suggestedContacts) { _, contact ->
                val isFollowed = followedSet.contains(contact.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = contact.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = contact.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = if (contact.isAppUser) "${contact.username} • ${contact.mutualFriends} صديق مشترك" else "غير مسجل في التطبيق", color = Color.Gray, fontSize = 10.sp)
                        }
                    }

                    if (contact.isAppUser) {
                        Button(
                            onClick = {
                                followedSet = if (isFollowed) followedSet - contact.id else followedSet + contact.id
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFollowed) Color.Gray else NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
 Text(text = if (isFollowed)"تمت المتابعة" else"متابعة +", color = BackgroundDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { /* Invite action */ },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
 Text(text ="دعوة عبر WhatsApp", color = NeonCyan, fontSize = 10.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Final Finish Button
        item {
            Button(
                onClick = onCompleteAll,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
 text ="إكمال التسجيل والدخول للموجز الرئيسي",
                    color = BackgroundDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun OtpResendCountdownButton(
    isTimerActive: Boolean,
    isSendingOtp: Boolean,
    initialSeconds: Int = 60,
    idleText: String = "إعادة إرسال رمز التوثيق",
    onSendOtp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timerSeconds by remember { mutableIntStateOf(initialSeconds) }
    var active by remember { mutableStateOf(isTimerActive) }

    LaunchedEffect(isTimerActive) {
        active = isTimerActive
        if (isTimerActive) {
            timerSeconds = initialSeconds
        }
    }

    LaunchedEffect(active, timerSeconds) {
        if (active && timerSeconds > 0) {
            delay(1000L)
            timerSeconds -= 1
        } else if (timerSeconds == 0) {
            active = false
        }
    }

    OutlinedButton(
        onClick = {
            if (!active && !isSendingOtp) {
                onSendOtp()
            }
        },
        enabled = !active && !isSendingOtp,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = if (active) "إعادة الإرسال بعد (${timerSeconds}ث)" else idleText,
            color = if (!active) NeonCyan else Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// =========================================================
// 8⃣ Modern Tabbed Auth View (Facebook-Style Step-By-Step Onboarding)
// =========================================================
@Composable
fun ModernTabbedAuthView(
    viewModel: MajarrahViewModel,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedCountry by remember { mutableStateOf(NexaPhoneAuthManager.getDefaultCountry(context)) }
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Sign Up
    var showForgotPasswordModal by remember { mutableStateOf(false) }

    if (showForgotPasswordModal) {
        com.example.ui.components.NexaForgotPasswordModal(
            onDismiss = { showForgotPasswordModal = false },
            onSuccessReset = {
                showForgotPasswordModal = false
                onAuthSuccess()
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Modern Segmented Tab Bar (Telegram / WhatsApp / Facebook Style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val tabs = listOf("تسجيل الدخول", "إنشاء حساب جديد")
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan else Color.Transparent)
                        .clickable { selectedTabIndex = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) BackgroundDark else Color.LightGray,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedTabIndex == 0) {
            TabbedLoginSection(
                viewModel = viewModel,
                onAuthSuccess = onAuthSuccess,
                selectedCountry = selectedCountry,
                onShowForgotPassword = { showForgotPasswordModal = true },
                onNavigateToTwoFactor = { viewModel.navigateToStep(LoginStep.TwoFactorAuth) }
            )
        } else {
            TabbedSignUpFlowSection(
                viewModel = viewModel,
                onAuthSuccess = onAuthSuccess,
                selectedCountry = selectedCountry
            )
        }
    }
}

// =========================================================
// 9⃣ Two-Factor Authentication (2FA) Step View
// =========================================================
@Composable
fun TwoFactorAuthStepView(
    viewModel: MajarrahViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(EncryptedGreen.copy(alpha = 0.2f))
                .border(1.5.dp, EncryptedGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = EncryptedGreen,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "المصادقة الثنائية (2FA)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "تم تفعيل حماية 2FA لحسابك. أدخل رمز الأمان المكون من 6 أرقام لتأكيد الهوية.",
            color = Color.LightGray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = codeInput,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    codeInput = it
                    errorMessage = null
                }
            },
            label = { Text("رمز التحقق (6 أرقام)", color = EncryptedGreen) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EncryptedGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

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

        Button(
            onClick = {
                if (codeInput.length < 6) {
                    errorMessage = "يرجى إدخال رمز التحقق كاملاً (6 أرقام)"
                    return@Button
                }
                isVerifying = true
                val isValid = viewModel.verifyTwoFactorCode(codeInput)
                isVerifying = false
                if (isValid) {
                    onSuccess()
                } else {
                    errorMessage = "رمز التحقق غير صحيح، يرجى المحاولة مجدداً"
                }
            },
            enabled = codeInput.length == 6 && !isVerifying,
            colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = BackgroundDark,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "تأكيد الدخول الآمن",
                    color = BackgroundDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onBack) {
            Text("العودة لتسجيل الدخول", color = Color.Gray, fontSize = 12.sp)
        }
    }
}



