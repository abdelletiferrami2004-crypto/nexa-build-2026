package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.NexaPhoneAuthManager
import com.example.data.firebase.NexaPhoneAuthManager.CountryCode
import com.example.util.SystemBiometricAuthManager
import com.example.ui.MajarrahViewModel
import com.example.ui.theme.*

@Composable
fun SignUpStep4Contact(
    phone: String,
    channel: String,
    inputAddress: String,
    onChannelChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("أدخل رقم الجوال أو البريد الإلكتروني", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("سيتم استخدام هذا العنوان لتسجيل الدخول والتحقق واسترداد الحساب.", color = Color.LightGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(3.dp)
        ) {
            listOf("phone" to "رقم الجوال", "email" to "البريد الإلكتروني").forEach { (ch, label) ->
                val isSelected = channel == ch
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonCyan else Color.Transparent)
                        .clickable {
                            onChannelChange(ch)
                            if (ch == "email" && !inputAddress.contains("@")) {
                                onAddressChange("user@gmail.com")
                            } else if (ch == "phone" && inputAddress.contains("@")) {
                                onAddressChange(phone)
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (isSelected) BackgroundDark else Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = inputAddress,
            onValueChange = onAddressChange,
            label = { Text(if (channel == "phone") "رقم الجوال" else "البريد الإلكتروني", color = NeonCyan) },
            leadingIcon = {
                Icon(
                    imageVector = if (channel == "phone") Icons.Default.Phone else Icons.Default.Email,
                    contentDescription = null,
                    tint = NeonCyan
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = if (channel == "phone") KeyboardType.Phone else KeyboardType.Email),
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
                enabled = inputAddress.isNotBlank(),
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
fun SignUpStep5Password(
    viewModel: MajarrahViewModel,
    selectedCountry: CountryCode,
    signUpChannel: String,
    signUpInputAddress: String,
    onVerificationIdGenerated: (String) -> Unit,
    onBack: () -> Unit,
    onGoToStep6: () -> Unit,
    onGoToStep7: () -> Unit
) {
    val password by viewModel.regPassword.collectAsState()
    val confirmPassword by viewModel.regConfirmPassword.collectAsState()
    var isSignUpPasswordVisible by remember { mutableStateOf(false) }
    var isSignUpConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isSendingSignUpOtp by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = remember(context) { NexaPhoneAuthManager.findActivity(context) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("إنشاء كلمة سر قوية", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("قم بإنشاء كلمة سر تتكون من 6 أرقام أو أحرف على الأقل.", color = Color.LightGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { p -> viewModel.updateRegPassword(p) },
            label = { Text("كلمة السر", color = NeonPurple) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple) },
            trailingIcon = {
                IconButton(onClick = { isSignUpPasswordVisible = !isSignUpPasswordVisible }) {
                    Icon(
                        imageVector = if (isSignUpPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Password",
                        tint = Color.LightGray
                    )
                }
            },
            visualTransformation = if (isSignUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { cp -> viewModel.updateRegConfirmPassword(cp) },
            label = { Text("تأكيد كلمة السر", color = NeonPurple) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple) },
            trailingIcon = {
                IconButton(onClick = { isSignUpConfirmPasswordVisible = !isSignUpConfirmPasswordVisible }) {
                    Icon(
                        imageVector = if (isSignUpConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Password",
                        tint = Color.LightGray
                    )
                }
            },
            visualTransformation = if (isSignUpConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = NeonCyan.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.VpnKey, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("حفظ تلقائي مع Google SmartLock & Credential Manager", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("سيتم حفظ بيانات الحساب بأمان في حساب Google للدخول التلقائي بنقرة واحدة.", color = Color.LightGray, fontSize = 10.sp)
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
                onClick = {
                    if (signUpChannel == "phone") {
                        isSendingSignUpOtp = true
                        NexaPhoneAuthManager.sendSmsOtp(
                            activity = activity,
                            phoneNumber = signUpInputAddress,
                            countryCode = selectedCountry.code,
                            onCodeSent = { verId ->
                                isSendingSignUpOtp = false
                                onVerificationIdGenerated(verId)
                                onGoToStep6()
                            },
                            onError = { _ ->
                                isSendingSignUpOtp = false
                                onGoToStep6()
                            },
                            onAutoVerified = {
                                isSendingSignUpOtp = false
                                onGoToStep7()
                            }
                        )
                    } else {
                        onGoToStep7()
                    }
                },
                enabled = password.length >= 6 && !isSendingSignUpOtp,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (isSendingSignUpOtp) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BackgroundDark, strokeWidth = 2.dp)
                } else {
                    Text("تأكيد وإرسال الرمز", color = BackgroundDark, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SignUpStep6Otp(
    selectedCountry: CountryCode,
    verificationId: String?,
    phoneNumber: String,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    var signUpOtpCode by remember { mutableStateOf("") }
    var isSendingSignUpOtp by remember { mutableStateOf(false) }
    var signUpOtpStatusMessage by remember { mutableStateOf<String?>(null) }
    var isSignUpOtpErrorBanner by remember { mutableStateOf(false) }
    var isTimerActive by remember { mutableStateOf(true) }
    var currentVerId by remember { mutableStateOf(verificationId) }
    val context = LocalContext.current
    val activity = remember(context) { NexaPhoneAuthManager.findActivity(context) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("تأكيد رمز التحقق (OTP)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("أدخل رمز التوثيق المكون من 6 أرقام الذي تم إرساله إلى حسابك.", color = Color.LightGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(14.dp))

        signUpOtpStatusMessage?.let { msg ->
            Text(
                text = msg,
                color = if (isSignUpOtpErrorBanner) NeonPink else EncryptedGreen,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = signUpOtpCode,
            onValueChange = { if (it.length <= 6) signUpOtpCode = it },
            label = { Text("أدخل رمز OTP (6 أرقام)", color = EncryptedGreen) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EncryptedGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OtpResendCountdownButton(
            isTimerActive = isTimerActive,
            isSendingOtp = isSendingSignUpOtp,
            initialSeconds = 60,
            idleText = "إعادة إرسال رمز التوثيق",
            onSendOtp = {
                isSendingSignUpOtp = true
                NexaPhoneAuthManager.sendSmsOtp(
                    activity = activity,
                    phoneNumber = phoneNumber,
                    countryCode = selectedCountry.code,
                    onCodeSent = { newVerId ->
                        isSendingSignUpOtp = false
                        currentVerId = newVerId
                        signUpOtpStatusMessage = "تمت إعادة إرسال رمز التوثيق بنجاح!"
                        isSignUpOtpErrorBanner = false
                        isTimerActive = true
                    },
                    onError = { err ->
                        isSendingSignUpOtp = false
                        isSignUpOtpErrorBanner = true
                        signUpOtpStatusMessage = err
                    },
                    onAutoVerified = {
                        isSendingSignUpOtp = false
                        onVerified()
                    }
                )
            }
        )

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
                onClick = {
                    if (signUpOtpCode.length < 6) {
                        isSignUpOtpErrorBanner = true
                        signUpOtpStatusMessage = "يرجى أدخال رمز التحقق كاملاً المكون من 6 أرقام!"
                        return@Button
                    }
                    isSendingSignUpOtp = true
                    NexaPhoneAuthManager.verifyOtpCode(
                        verificationId = currentVerId,
                        otpCode = signUpOtpCode,
                        onSuccess = {
                            isSendingSignUpOtp = false
                            isSignUpOtpErrorBanner = false
                            onVerified()
                        },
                        onError = { err ->
                            isSendingSignUpOtp = false
                            isSignUpOtpErrorBanner = true
                            signUpOtpStatusMessage = err
                        }
                    )
                },
                enabled = !isSendingSignUpOtp,
                colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (isSendingSignUpOtp) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BackgroundDark, strokeWidth = 2.dp)
                } else {
                    Text("تأكيد الرمز والتالي", color = BackgroundDark, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SignUpStep7Biometric(
    viewModel: MajarrahViewModel,
    signUpChannel: String,
    signUpInputAddress: String,
    onAuthSuccess: () -> Unit
) {
    val password by viewModel.regPassword.collectAsState()
    val firstName by viewModel.regFirstName.collectAsState()
    val lastName by viewModel.regLastName.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val context = LocalContext.current

    var biometricErrorMsg by remember { mutableStateOf<String?>(null) }
    var showSkipBiometric by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("تأكيد البصمة الحيوية للجهاز", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text("قم بإجراء مسح مطابقة بصمة أصبعك المعتمدة بالجهاز لتفعيل الدخول الآمن بلمسة واحدة.", color = Color.LightGray, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(18.dp))

        biometricErrorMsg?.let { msg ->
            Text(
                text = msg,
                color = NeonPink,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    SystemBiometricAuthManager.authenticate(
                        context = context,
                        title = "تأكيد البصمة الحيوية - NEXA",
                        subtitle = "مسح بصمة الأصبع لإكمال إعداد الحساب",
                        negativeButtonText = "إلغاء",
                        onSuccess = {
                            biometricErrorMsg = null
                            showSkipBiometric = false
                            val fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            if (fbUser != null) {
                                viewModel.completeProfileRegistration()
                                onAuthSuccess()
                            } else {
                                biometricErrorMsg = "لا يمكن إتمام التسجيل: يلزم وجود حساب موثق عبر البريد أو الهاتف أو Google"
                            }
                        },
                        onError = { err: String ->
                            biometricErrorMsg = "تعذر التحقق من البصمة الحيوية: $err"
                            showSkipBiometric = true
                        },
                        onFallbackToPassword = {
                            biometricErrorMsg = "تم اختيار المتابعة بدون بصمة"
                            showSkipBiometric = true
                        }
                    )
                },
            shape = RoundedCornerShape(20.dp),
            color = NeonPurple.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("انقر هنا لمسح البصمة الحيوية", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("طابق بصمة أصبعك الآن مع الحساس الرسمي للجهاز", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        if (showSkipBiometric) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    viewModel.toggleBiometricForAccount(false)
                    if (password.isBlank() || password.length < 6) {
                        biometricErrorMsg = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
                        return@OutlinedButton
                    }
                    if (signUpChannel == "email") {
                        if (signUpInputAddress.isBlank() || !signUpInputAddress.contains("@")) {
                            biometricErrorMsg = "يرجى إدخال بريد إلكتروني حقيقي وصالح"
                            return@OutlinedButton
                        }
                        viewModel.signUpWithEmail(
                            email = signUpInputAddress.trim(),
                            pass = password,
                            displayName = "$firstName $lastName".trim(),
                            onSuccess = {
                                if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null) {
                                    viewModel.completeProfileRegistration()
                                    onAuthSuccess()
                                } else {
                                    biometricErrorMsg = "فشل إنشاء حساب Firebase"
                                }
                            }
                        )
                    } else {
                        val fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        if (fbUser != null) {
                            viewModel.completeProfileRegistration()
                            onAuthSuccess()
                        } else {
                            biometricErrorMsg = "بدون بريد إلكتروني، يلزم إكمال التحقق برمز الهاتف أو تسجيل الدخول عبر Google"
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("تخطي البصمة", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (password.isBlank() || password.length < 6) {
                    biometricErrorMsg = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
                    return@Button
                }
                if (signUpChannel == "email") {
                    if (signUpInputAddress.isBlank() || !signUpInputAddress.contains("@")) {
                        biometricErrorMsg = "يرجى إدخال بريد إلكتروني حقيقي وصالح"
                        return@Button
                    }
                    viewModel.signUpWithEmail(
                        email = signUpInputAddress.trim(),
                        pass = password,
                        displayName = "$firstName $lastName".trim(),
                        onSuccess = {
                            if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null) {
                                viewModel.completeProfileRegistration()
                                onAuthSuccess()
                            } else {
                                biometricErrorMsg = "فشل إنشاء حساب Firebase"
                            }
                        }
                    )
                } else {
                    val fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (fbUser != null) {
                        viewModel.completeProfileRegistration()
                        onAuthSuccess()
                    } else {
                        biometricErrorMsg = "بدون بريد إلكتروني، يلزم إكمال التحقق برمز الهاتف أو تسجيل الدخول عبر Google"
                    }
                }
            },
            enabled = !isAuthLoading,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isAuthLoading) {
                CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("إنهاء التسجيل والدخول إلى المنصة", color = BackgroundDark, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
        }
    }
}
