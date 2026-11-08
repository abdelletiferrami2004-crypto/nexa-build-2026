package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.NexaPhoneAuthManager
import com.example.data.firebase.NexaPhoneAuthManager.CountryCode
import com.example.util.SystemBiometricAuthManager
import com.example.ui.MajarrahViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TabbedSignUpFlowSection(
    viewModel: MajarrahViewModel,
    onAuthSuccess: () -> Unit,
    selectedCountry: CountryCode
) {
    var signUpStep by remember { mutableIntStateOf(1) } // 1 to 7

    val progressFraction = signUpStep / 7.0f
    val stepTitle = when (signUpStep) {
        1 -> "الخطوة 1 من 7: الاسم الشخصي"
        2 -> "الخطوة 2 من 7: تاريخ الميلاد"
        3 -> "الخطوة 3 من 7: تحديد الجنس"
        4 -> "الخطوة 4 من 7: بيانات التواصل"
        5 -> "الخطوة 5 من 7: كلمة السر والحماية"
        6 -> "الخطوة 6 من 7: تأكيد رمز التحقق"
        7 -> "الخطوة 7 من 7: البصمة الحيوية"
        else -> ""
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stepTitle,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "${(progressFraction * 100).toInt()}%",
                color = Color.LightGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = progressFraction,
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = NeonCyan,
            trackColor = Color.White.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // State shared across steps
        val phone by viewModel.phoneNumber.collectAsState()
        var signUpChannel by remember { mutableStateOf("phone") }
        var signUpInputAddress by remember { mutableStateOf(phone) }
        var signUpVerificationId by remember { mutableStateOf<String?>(null) }

        when (signUpStep) {
            1 -> SignUpStep1Name(viewModel = viewModel, onAuthSuccess = onAuthSuccess, onNext = { signUpStep = 2 })
            2 -> SignUpStep2Birthdate(viewModel = viewModel, onBack = { signUpStep = 1 }, onNext = { signUpStep = 3 })
            3 -> SignUpStep3Gender(viewModel = viewModel, onBack = { signUpStep = 2 }, onNext = { signUpStep = 4 })
            4 -> SignUpStep4Contact(
                phone = phone,
                channel = signUpChannel,
                inputAddress = signUpInputAddress,
                onChannelChange = { signUpChannel = it },
                onAddressChange = {
                    signUpInputAddress = it
                    viewModel.setPhone(it)
                },
                onBack = { signUpStep = 3 },
                onNext = { signUpStep = 5 }
            )
            5 -> SignUpStep5Password(
                viewModel = viewModel,
                selectedCountry = selectedCountry,
                signUpChannel = signUpChannel,
                signUpInputAddress = signUpInputAddress,
                onVerificationIdGenerated = { signUpVerificationId = it },
                onBack = { signUpStep = 4 },
                onGoToStep6 = { signUpStep = 6 },
                onGoToStep7 = { signUpStep = 7 }
            )
            6 -> SignUpStep6Otp(
                selectedCountry = selectedCountry,
                verificationId = signUpVerificationId,
                phoneNumber = signUpInputAddress,
                onBack = { signUpStep = 5 },
                onVerified = { signUpStep = 7 }
            )
            7 -> SignUpStep7Biometric(
                viewModel = viewModel,
                signUpChannel = signUpChannel,
                signUpInputAddress = signUpInputAddress,
                onAuthSuccess = onAuthSuccess
            )
        }
    }
}

@Composable
fun SignUpStep1Name(
    viewModel: MajarrahViewModel,
    onAuthSuccess: () -> Unit,
    onNext: () -> Unit
) {
    val firstName by viewModel.regFirstName.collectAsState()
    val lastName by viewModel.regLastName.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authErrorMsg by viewModel.authErrorMessage.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        if (com.example.data.firebase.NexaGoogleAuthManager.isGoogleAuthConfigured) {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.clearAuthErrorMessage()
                        com.example.data.firebase.NexaGoogleAuthManager.signInWithGoogle(
                            context = context,
                            onSuccess = { firebaseUser ->
                                viewModel.onGoogleSignInSuccess(firebaseUser, onAuthSuccess)
                            },
                            onError = { errorMsg ->
                                viewModel.setAuthErrorMessage(errorMsg)
                            }
                        )
                    }
                },
                enabled = !isAuthLoading,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Google Fast Sign-Up",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "التسجيل الفوري بحساب Google (Firebase Auth)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "تسجيل الدخول بـ Google غير مُعد",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (authErrorMsg != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = NeonPink.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
            ) {
                Text(
                    text = authErrorMsg ?: "",
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.15f)))
            Text(
                text = " أو إدخال البيانات يدوياً ",
                color = Color.LightGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.15f)))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("ما اسمك؟", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("أدخل الاسم الذي تستخدمه في حياتك اليومية.", color = Color.LightGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { f -> viewModel.updateRegFullName(f, lastName) },
            label = { Text("الاسم الأول", color = NeonCyan) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { l -> viewModel.updateRegFullName(firstName, l) },
            label = { Text("اسم العائلة", color = NeonCyan) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            enabled = firstName.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("التالي", color = BackgroundDark, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun SignUpStep2Birthdate(
    viewModel: MajarrahViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val birthDay by viewModel.regBirthDay.collectAsState()
    val birthMonth by viewModel.regBirthMonth.collectAsState()
    val birthYear by viewModel.regBirthYear.collectAsState()
    val calculatedAge by viewModel.userAge.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("ما تاريخ ميلادك؟", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("اختر تاريخ ميلادك لتأكيد فئة الحساب وتفعيل خيارات الأمان المناسبة.", color = Color.LightGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("تاريخ الميلاد المختار", color = Color.LightGray, fontSize = 11.sp)
                    Text("$birthDay / $birthMonth / $birthYear", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        viewModel.updateRegDateOfBirth(dayOfMonth, month + 1, year)
                    },
                    birthYear,
                    birthMonth - 1,
                    birthDay
                )
                datePickerDialog.show()
            },
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("اختيار تاريخ الميلاد من التقويم", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (calculatedAge < 18) TeenProtectionCyan.copy(alpha = 0.15f) else NeonPurple.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (calculatedAge < 18) TeenProtectionCyan else NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (calculatedAge < 18) "العمر: $calculatedAge سنة (وضع حماية الناشئة مفعّل تلقائياً)" else "العمر: $calculatedAge سنة (حساب عام بالغين)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("السابق", color = Color.LightGray, fontSize = 13.sp)
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("التالي", color = BackgroundDark, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun SignUpStep3Gender(
    viewModel: MajarrahViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val gender by viewModel.regGender.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("ما جنسك؟", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("اختر الجنس لعرض المحتوى والخبرات المناسبة لك في منصة NEXA.", color = Color.LightGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(16.dp))

        val genderOptions = listOf("ذكر", "أنثى", "تخصيص / عدم التحديد")
        genderOptions.forEach { option ->
            val isSelected = gender == option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel.updateRegGender(option) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = option, color = Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("السابق", color = Color.LightGray, fontSize = 13.sp)
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("التالي", color = BackgroundDark, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
        }
    }
}
