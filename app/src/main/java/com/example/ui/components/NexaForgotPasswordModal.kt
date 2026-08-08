package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.firebase.NexaPhoneAuthManager
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay

@Composable
fun NexaForgotPasswordModal(
    onDismiss: () -> Unit,
    onSuccessReset: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { NexaPhoneAuthManager.findActivity(context) }

    var recoveryStep by remember { mutableIntStateOf(1) } // 1: Enter Phone & Country Code, 2: 6-Digit OTP Verification, 3: New Password
    var selectedCountryCode by remember { mutableStateOf(NexaPhoneAuthManager.supportedCountryCodes.first()) }
    var phoneNumber by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    var verificationId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Success / Error status banner state
    var isErrorBanner by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // 60-second Resend OTP countdown timer state
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp),
            color = BackgroundDark,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.2f))
                                .border(1.dp, NeonPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = "Forgot Password",
                                tint = NeonPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "استعادة كلمة السر (Firebase SMS Auth)",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "التحقق عبر Firebase SMS OTP وتعديل كلمة السر",
                                color = NeonPurple,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error / Success Toast Notification Box
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

                when (recoveryStep) {
                    1 -> {
                        // Step 1: Phone & Country Code Input
                        Text(
                            text = "اختر كود الدولة وأدخل رقم الجوال لإرسال رمز OTP:",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Country Code Quick Selector Row
                        Text("رمز الدولة (Country Code):", color = NeonCyan, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(NexaPhoneAuthManager.supportedCountryCodes) { cc ->
                                val selected = selectedCountryCode.code == cc.code
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) NeonCyan.copy(alpha = 0.3f) else CardBackground)
                                        .border(1.dp, if (selected) NeonCyan else CardBorder, RoundedCornerShape(10.dp))
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

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("رقم الجوال (${selectedCountryCode.code})", color = NeonCyan) },
                            leadingIcon = {
                                Text(
                                    text = selectedCountryCode.flag,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                statusMessage = null
                                isErrorBanner = false

                                NexaPhoneAuthManager.sendSmsOtp(
                                    activity = activity,
                                    phoneNumber = phoneNumber,
                                    countryCode = selectedCountryCode.code,
                                    onCodeSent = { verId ->
                                        isLoading = false
                                        verificationId = verId
                                        isErrorBanner = false
                                        statusMessage = "تم إرسال رمز OTP عبر SMS إلى ${selectedCountryCode.code} $phoneNumber بنجاح! 📲"
                                        recoveryStep = 2
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
                                        statusMessage = "تم التحقق التلقائي بـ Firebase Auth! أدخل كلمة السر الجديدة."
                                        recoveryStep = 3
                                    }
                                )
                            },
                            enabled = !isLoading && phoneNumber.trim().length >= 7,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري الاتصال بـ Firebase SMS...", color = Color.Black, fontSize = 13.sp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sms, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("إرسال رمز SMS OTP عبر Firebase", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    2 -> {
                        // Step 2: 6-Digit OTP Verification with Resend Timer
                        Text(
                            text = "أدخل رمز OTP المكون من 6 أرقام المرسل إلى ${selectedCountryCode.code} $phoneNumber:",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { if (it.length <= 6) otpInput = it },
                            label = { Text("رمز التحقق OTP (6 أرقام)", color = NeonAmber) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonAmber) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Verify Button
                        Button(
                            onClick = {
                                isLoading = true
                                statusMessage = null
                                isErrorBanner = false

                                NexaPhoneAuthManager.verifyOtpCode(
                                    verificationId = verificationId,
                                    otpCode = otpInput,
                                    onSuccess = {
                                        isLoading = false
                                        isErrorBanner = false
                                        statusMessage = "تم التحقق من الرمز بنجاح بـ Firebase Auth! حدد كلمة السر الجديدة 🔐"
                                        recoveryStep = 3
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        isErrorBanner = true
                                        statusMessage = err
                                    }
                                )
                            },
                            enabled = !isLoading && otpInput.trim().length >= 4,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري فحص الرمز بـ Firebase...", color = Color.Black, fontSize = 13.sp)
                            } else {
                                Text("تأكيد الرمز والمتابعة (Verify)", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Resend OTP Row with 60s Timer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTimerActive) "إعادة الإرسال متاحة بعد: ${resendTimerSeconds}s" else "لم تصلك الرسالة النصية؟",
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
                                            phoneNumber = phoneNumber,
                                            countryCode = selectedCountryCode.code,
                                            onCodeSent = { verId ->
                                                isLoading = false
                                                verificationId = verId
                                                isErrorBanner = false
                                                statusMessage = "تم إعاده إرسال رمز OTP بنجاح! 📲"
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
                                                recoveryStep = 3
                                            }
                                        )
                                    }
                                },
                                enabled = !isTimerActive && !isLoading,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (!isTimerActive) NeonCyan else Color.Gray)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = if (!isTimerActive) NeonCyan else Color.Gray, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTimerActive) "انتظر (${resendTimerSeconds}s)" else "إعادة إرسال الرمز",
                                        color = if (!isTimerActive) NeonCyan else Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Set New Password
                        Text("أنشئ كلمة سر جديدة قوية لحسابك:", color = Color.LightGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("كلمة السر الجديدة", color = NeonPurple) },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = NeonPurple) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = { confirmNewPassword = it },
                            label = { Text("تأكيد كلمة السر الجديدة", color = NeonPurple) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (newPassword.isNotBlank() && newPassword == confirmNewPassword) {
                                    statusMessage = "تم تعيين كلمة السر بنجاح! جاري توجيهك للمنصة... 🔐"
                                    onSuccessReset()
                                }
                            },
                            enabled = newPassword.length >= 6 && newPassword == confirmNewPassword,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("حفظ كلمة السر والدخول لحسابك", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
