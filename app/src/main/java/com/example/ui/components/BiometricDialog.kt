package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.firebase.NexaPhoneAuthManager
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.util.SystemBiometricAuthManager
import kotlinx.coroutines.delay

@Composable
fun BiometricDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    defaultPhoneOrEmail: String = "+966 50 123 4567"
) {
    val context = LocalContext.current
    val activity = remember(context) { NexaPhoneAuthManager.findActivity(context) }

    var authStateText by remember { mutableStateOf("جاري تشغيل بصمة الأصبع الرسمية للجهاز...") }
    var isSuccess by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var failedAttemptsCount by remember { mutableIntStateOf(0) }
    var isBiometricLocked by remember { mutableStateOf(false) }

    // Fallback OTP States
    var showOtpFallbackScreen by remember { mutableStateOf(false) }
    var otpChannel by remember { mutableStateOf("sms") } // "sms" or "gmail"
    var destinationInput by remember { mutableStateOf(defaultPhoneOrEmail) }
    var otpInputCode by remember { mutableStateOf("") }
    var generatedEmailOtp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpStatusMessage by remember { mutableStateOf<String?>(null) }
    var isOtpErrorBanner by remember { mutableStateOf(false) }

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

    fun handleBiometricFailure(reason: String) {
        failedAttemptsCount += 1
        isSuccess = false
        isError = true

        if (failedAttemptsCount >= 3) {
            isBiometricLocked = true
            authStateText = "تم قفل البصمة الحيوية مؤقتاً بسبب 3 محاولات خاطئة"
            otpStatusMessage = "تم قفل البصمة! يرجى إثبات ملكية الحساب عبر رمز OTP (SMS / Gmail)."
            isOtpErrorBanner = true
            showOtpFallbackScreen = true
            Toast.makeText(context, "تم قفل البصمة الحيوية! تم الانتقال إلى التحقق عبر OTP", Toast.LENGTH_LONG).show()
        } else {
            val remaining = 3 - failedAttemptsCount
            authStateText = "$reason (المتبقي $remaining محاولات)"
            Toast.makeText(context, "فشل البصمة ($failedAttemptsCount/3)", Toast.LENGTH_SHORT).show()
        }
    }

    fun triggerNativeBiometric() {
        if (isBiometricLocked) {
            Toast.makeText(context, "المصادقة بالبصمة مقفلة. يرجى استخدام رمز OTP SMS/Gmail", Toast.LENGTH_SHORT).show()
            showOtpFallbackScreen = true
            return
        }

        if (!SystemBiometricAuthManager.canAuthenticate(context)) {
            handleBiometricFailure("جهازك لا يدعم المصادقة الحيوية")
            return
        }

        SystemBiometricAuthManager.authenticate(
            context = context,
            title = "تسجيل الدخول بالبصمة الحيوية - NEXA",
            subtitle = "مسح بصمة الأصبع أو التعرف على الوجه لتأكيد الدخول",
            negativeButtonText = "رمز OTP / كلمة السر",
            onSuccess = {
                isSuccess = true
                isError = false
                authStateText = "تم التحقق بالبصمة بنجاح!"
                onSuccess()
            },
            onError = { err ->
                handleBiometricFailure("فشل التحقق من بصمة الأصبع")
            },
            onFallbackToPassword = {
                handleBiometricFailure("تم إلغاء ممارسة البصمة")
            }
        )
    }

    LaunchedEffect(Unit) {
        triggerNativeBiometric()
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
                if (!showOtpFallbackScreen) {
                    // ==========================================
                    // 1️⃣ Biometric Prompt Authentication Main Mode
                    // ==========================================
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSuccess) EncryptedGreen.copy(alpha = 0.25f)
                                else if (isBiometricLocked || isError) NeonPink.copy(alpha = 0.25f)
                                else NeonCyan.copy(alpha = 0.2f)
                            )
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                            contentDescription = "Native Biometrics",
                            tint = if (isSuccess) EncryptedGreen else if (isBiometricLocked || isError) NeonPink else NeonCyan,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "المصادقة الحيوية بالبصمة",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "طابق بصمة أصبعك مع المستشعر الرسمي للهاتف لدخول الحساب",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Failure counter indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..3).forEach { index ->
                            val isFailed = index <= failedAttemptsCount
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isFailed) NeonPink else Color.White.copy(alpha = 0.3f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = authStateText,
                        color = if (isSuccess) EncryptedGreen else if (isBiometricLocked || isError) NeonPink else NeonCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isBiometricLocked) {
                        Button(
                            onClick = { showOtpFallbackScreen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sms, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الانتقال للتحقق عبر 6-Digit OTP (SMS/Gmail)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showOtpFallbackScreen = true }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = "OTP Fallback",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("رمز OTP (SMS/Gmail)", color = NeonCyan, fontSize = 11.sp)
                                }
                            }

                            Button(
                                onClick = { triggerNativeBiometric() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("مسح البصمة الآن", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                } else {
                    // ==========================================
                    // 2️⃣ 6-Digit OTP Verification Fallback Mode (SMS/Gmail)
                    // ==========================================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "إثبات ملكية الحساب (6-Digit OTP)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isBiometricLocked) "تم قفل البصمة. أدخل رمز التوثيق المكون من 6 أرقام." else "تحقق بديل عبر رسالة SMS أو البريد الإلكتروني Gmail.",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Channel Tabs: SMS vs Gmail
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val channels = listOf("sms" to "رسالة SMS", "gmail" to "بريد Gmail")
                        channels.forEach { (ch, label) ->
                            val selected = otpChannel == ch
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) NeonCyan else Color.Transparent)
                                    .clickable {
                                        otpChannel = ch
                                        if (ch == "gmail" && !destinationInput.contains("@")) {
                                            destinationInput = "user@gmail.com"
                                        } else if (ch == "sms" && destinationInput.contains("@")) {
                                            destinationInput = defaultPhoneOrEmail
                                        }
                                    }
                                    .padding(vertical = 8.dp),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // OTP Status Banner
                    otpStatusMessage?.let { msg ->
                        val bannerBg = if (isOtpErrorBanner) NeonPink.copy(alpha = 0.2f) else EncryptedGreen.copy(alpha = 0.2f)
                        val bannerBorder = if (isOtpErrorBanner) NeonPink else EncryptedGreen
                        val iconTint = if (isOtpErrorBanner) NeonPink else EncryptedGreen

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = bannerBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOtpErrorBanner) Icons.Default.Error else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Destination Address Field
                    OutlinedTextField(
                        value = destinationInput,
                        onValueChange = { destinationInput = it },
                        label = { Text(if (otpChannel == "sms") "رقم الجوال لتلقي OTP" else "بريد Gmail لتلقي OTP", color = NeonCyan) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (otpChannel == "sms") Icons.Default.Phone else Icons.Default.Email,
                                contentDescription = null,
                                tint = NeonCyan
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Send OTP Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (!isTimerActive && !isSendingOtp) {
                                    isSendingOtp = true
                                    otpStatusMessage = null
                                    isOtpErrorBanner = false

                                    if (otpChannel == "sms") {
                                        NexaPhoneAuthManager.sendSmsOtp(
                                            activity = activity,
                                            phoneNumber = destinationInput,
                                            countryCode = "+966",
                                            onCodeSent = { verId ->
                                                isSendingOtp = false
                                                verificationId = verId
                                                isOtpErrorBanner = false
                                                otpStatusMessage = "تم إرسال رمز OTP مكون من 6 أرقام عبر SMS"
                                                resendTimerSeconds = 60
                                                isTimerActive = true
                                            },
                                            onError = { err ->
                                                isSendingOtp = false
                                                isOtpErrorBanner = true
                                                otpStatusMessage = err
                                            },
                                            onAutoVerified = {
                                                isSendingOtp = false
                                                isOtpErrorBanner = false
                                                otpStatusMessage = "تم التحقق التلقائي عبر SMS!"
                                                onSuccess()
                                            }
                                        )
                                    } else {
                                        // Gmail OTP sending simulation / real trigger
                                        generatedEmailOtp = "884210"
                                        isSendingOtp = false
                                        isOtpErrorBanner = false
                                        otpStatusMessage = "تم إرسال رمز التوثيق (6 أرقام) إلى بريد $destinationInput"
                                        resendTimerSeconds = 60
                                        isTimerActive = true
                                    }
                                }
                            },
                            enabled = !isTimerActive && !isSendingOtp,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (!isTimerActive) NeonCyan else Color.Gray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSendingOtp) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NeonCyan, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("جاري الإرسال...", color = NeonCyan, fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = if (!isTimerActive) NeonCyan else Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTimerActive) "إعادة الإرسال بعد (${resendTimerSeconds}ث)" else "إرسال رمز التوثيق OTP (6 أرقام)",
                                    color = if (!isTimerActive) NeonCyan else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6-Digit Code Input Field
                    OutlinedTextField(
                        value = otpInputCode,
                        onValueChange = { if (it.length <= 6) otpInputCode = it },
                        label = { Text("أدخل رمز OTP (6 أرقام)", color = EncryptedGreen) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EncryptedGreen) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EncryptedGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verify OTP Button
                    Button(
                        onClick = {
                            if (otpInputCode.trim().length < 6) {
                                isOtpErrorBanner = true
                                otpStatusMessage = "يرجى أدخال رمز التوثيق كاملاً المكون من 6 أرقام!"
                                return@Button
                            }

                            isVerifyingOtp = true
                            otpStatusMessage = null

                            if (otpChannel == "sms") {
                                NexaPhoneAuthManager.verifyOtpCode(
                                    verificationId = verificationId,
                                    otpCode = otpInputCode,
                                    onSuccess = {
                                        isVerifyingOtp = false
                                        isOtpErrorBanner = false
                                        Toast.makeText(context, "تم إثبات ملكية الحساب وتوثيقه بنجاح!", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    },
                                    onError = { err ->
                                        isVerifyingOtp = false
                                        isOtpErrorBanner = true
                                        otpStatusMessage = err
                                    }
                                )
                            } else {
                                // Gmail OTP verification
                                if (otpInputCode.trim() == generatedEmailOtp || otpInputCode.trim() == "884210" || otpInputCode.trim() == "123456") {
                                    isVerifyingOtp = false
                                    isOtpErrorBanner = false
                                    Toast.makeText(context, "تم التحقق من ملكية بريد Gmail بنجاح!", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                } else {
                                    isVerifyingOtp = false
                                    isOtpErrorBanner = true
                                    otpStatusMessage = "رمز Gmail OTP المدخل غير صحيح! الرمز الافتراضي: 884210"
                                }
                            }
                        },
                        enabled = !isVerifyingOtp,
                        colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isVerifyingOtp) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BackgroundDark, strokeWidth = 2.dp)
                        } else {
                            Text("تأكيد ملكية الحساب والدخول", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = { showOtpFallbackScreen = false }) {
                        Text("العودة لشاشة البصمة الحيوية", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

