package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
fun TabbedLoginSection(
    viewModel: MajarrahViewModel,
    onAuthSuccess: () -> Unit,
    selectedCountry: CountryCode,
    onShowForgotPassword: () -> Unit,
    onNavigateToTwoFactor: () -> Unit
) {
    val phone by viewModel.phoneNumber.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authErrorMsg by viewModel.authErrorMessage.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activity = remember(context) { NexaPhoneAuthManager.findActivity(context) }

    var loginInput by remember { mutableStateOf(phone) }
    var loginPassword by remember { mutableStateOf("") }
    var isLoginPasswordVisible by remember { mutableStateOf(false) }
    var localLoginErrorMsg by remember { mutableStateOf<String?>(null) }

    var failedBiometricAttempts by remember { mutableIntStateOf(0) }
    var isBiometricLocked by remember { mutableStateOf(false) }
    var biometricStatusMessage by remember { mutableStateOf<String?>(null) }
    var isBiometricErrorBanner by remember { mutableStateOf(false) }

    var showOtpFallbackSection by remember { mutableStateOf(false) }

    fun triggerInlineBiometricScan() {
        if (isBiometricLocked || failedBiometricAttempts >= 3) {
            showOtpFallbackSection = true
            return
        }

        SystemBiometricAuthManager.authenticate(
            context = context,
            title = "تسجيل الدخول بالبصمة الحيوية - NEXA",
            subtitle = "طابق بصمة الأصبع أو قفل الشاشة للدخول الفوري",
            negativeButtonText = "إلغاء",
            onSuccess = {
                if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null) {
                    biometricStatusMessage = "تم التحقق بالبصمة بنجاح!"
                    isBiometricErrorBanner = false
                    viewModel.completeProfileRegistration()
                    onAuthSuccess()
                } else {
                    biometricStatusMessage = "لا يوجد حساب مسجل نشط. يرجى تسجيل الدخول أولاً"
                    isBiometricErrorBanner = true
                }
            },
            onError = { err: String ->
                failedBiometricAttempts += 1
                isBiometricErrorBanner = true
                if (failedBiometricAttempts >= 3) {
                    isBiometricLocked = true
                    showOtpFallbackSection = true
                    biometricStatusMessage = "تم قفل البصمة مؤقتاً بعد 3 محاولات خاطئة"
                } else {
                    val remaining = 3 - failedBiometricAttempts
                    biometricStatusMessage = "فشل مسح البصمة (متبقي $remaining محاولات)"
                }
            },
            onFallbackToPassword = {
                failedBiometricAttempts += 1
                isBiometricErrorBanner = true
                if (failedBiometricAttempts >= 3) {
                    isBiometricLocked = true
                    showOtpFallbackSection = true
                    biometricStatusMessage = "تم قفل البصمة مؤقتاً بعد 3 محاولات خاطئة"
                } else {
                    val remaining = 3 - failedBiometricAttempts
                    biometricStatusMessage = "تم الإلغاء (متبقي $remaining محاولات)"
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = loginInput,
            onValueChange = {
                loginInput = it
                viewModel.setPhone(it)
            },
            label = { Text("رقم الجوال أو اسم المستخدم", color = NeonCyan) },
            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = NeonCyan) },
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
            value = loginPassword,
            onValueChange = { loginPassword = it },
            label = { Text("كلمة السر", color = NeonPurple) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonPurple) },
            trailingIcon = {
                IconButton(onClick = { isLoginPasswordVisible = !isLoginPasswordVisible }) {
                    Icon(
                        imageVector = if (isLoginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Password",
                        tint = Color.LightGray
                    )
                }
            },
            visualTransformation = if (isLoginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "نسيت كلمة السر؟",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onShowForgotPassword() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val effectiveLoginError = localLoginErrorMsg ?: authErrorMsg
        if (effectiveLoginError != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = NeonPink.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
            ) {
                Text(
                    text = effectiveLoginError,
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = {
                if (loginPassword.isBlank() || loginPassword.length < 6) {
                    localLoginErrorMsg = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
                    return@Button
                }
                localLoginErrorMsg = null
                if (loginInput.contains("@")) {
                    viewModel.signInWithEmail(
                        email = loginInput,
                        pass = loginPassword,
                        onSuccess = onAuthSuccess
                    )
                } else {
                    val hasFirebase = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null
                    if (!hasFirebase) {
                        localLoginErrorMsg = "يرجى تسجيل الدخول عبر بريد إلكتروني صالح أو Google أو هاتف موثق"
                        return@Button
                    }
                    val currentProfile = viewModel.userProfile.value
                    if (currentProfile?.isTwoFactorEnabled == true) {
                        onNavigateToTwoFactor()
                    } else {
                        viewModel.completeProfileRegistration()
                        onAuthSuccess()
                    }
                }
            },
            enabled = !isAuthLoading,
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isAuthLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("تسجيل الدخول", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Google Sign In",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "تسجيل الدخول عبر Google (Firebase Auth)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "الرسائل مخزنة على خوادم Firebase ☁️",
                            color = Color.LightGray.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
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

        Spacer(modifier = Modifier.height(16.dp))

        if (!showOtpFallbackSection && !isBiometricLocked) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = { triggerInlineBiometricScan() },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = NeonPurple.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometrics",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تسجيل سريع بالبصمة الحيوية",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                if (biometricStatusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = biometricStatusMessage!!,
                        color = if (isBiometricErrorBanner) NeonPink else EncryptedGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                if (failedBiometricAttempts > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..3).forEach { index ->
                            val isFailed = index <= failedBiometricAttempts
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isFailed) NeonPink else Color.White.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
        } else {
            LoginOtpFallbackCard(
                activity = activity,
                selectedCountry = selectedCountry,
                initialDestination = loginInput,
                onAuthSuccess = onAuthSuccess
            )
        }
    }
}
