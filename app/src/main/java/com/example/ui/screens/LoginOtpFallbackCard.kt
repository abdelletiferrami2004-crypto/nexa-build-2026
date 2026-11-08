package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.NexaPhoneAuthManager
import com.example.data.firebase.NexaPhoneAuthManager.CountryCode
import com.example.ui.theme.*

@Composable
fun LoginOtpFallbackCard(
    activity: Activity?,
    selectedCountry: CountryCode,
    initialDestination: String,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otpDestinationInput by remember { mutableStateOf(initialDestination) }
    var otpInputCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpStatusMessage by remember { mutableStateOf<String?>(null) }
    var isOtpErrorBanner by remember { mutableStateOf(false) }
    var isTimerActive by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundDark.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = NeonPink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("إثبات ملكية الحساب (SMS OTP)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("تم قفل البصمة مؤقتاً. استخدم رمز SMS OTP لتأكيد ملكية الحساب.", color = Color.LightGray, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            otpStatusMessage?.let { msg ->
                Text(
                    text = msg,
                    color = if (isOtpErrorBanner) NeonPink else EncryptedGreen,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = otpDestinationInput,
                onValueChange = { otpDestinationInput = it },
                label = { Text("رقم الجوال لتلقي رمز SMS OTP", color = NeonCyan) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OtpResendCountdownButton(
                isTimerActive = isTimerActive,
                isSendingOtp = isSendingOtp,
                initialSeconds = 60,
                idleText = "إرسال رمز التوثيق OTP (6 أرقام)",
                onSendOtp = {
                    isSendingOtp = true
                    otpStatusMessage = null
                    isOtpErrorBanner = false

                    NexaPhoneAuthManager.sendSmsOtp(
                        activity = activity,
                        phoneNumber = otpDestinationInput,
                        countryCode = selectedCountry.code,
                        onCodeSent = { verId ->
                            isSendingOtp = false
                            verificationId = verId
                            otpStatusMessage = "تم إرسال رمز OTP المكون من 6 أرقام عبر SMS"
                            isTimerActive = true
                        },
                        onError = { err ->
                            isSendingOtp = false
                            isOtpErrorBanner = true
                            otpStatusMessage = err
                        },
                        onAutoVerified = {
                            isSendingOtp = false
                            onAuthSuccess()
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = otpInputCode,
                onValueChange = { if (it.length <= 6) otpInputCode = it },
                label = { Text("أدخل رمز OTP (6 أرقام)", color = EncryptedGreen) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EncryptedGreen,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (otpInputCode.trim().length < 6) {
                        isOtpErrorBanner = true
                        otpStatusMessage = "يرجى أدخال رمز التوثيق المكون من 6 أرقام كاملاً!"
                        return@Button
                    }

                    isVerifyingOtp = true

                    NexaPhoneAuthManager.verifyOtpCode(
                        verificationId = verificationId,
                        otpCode = otpInputCode,
                        onSuccess = {
                            isVerifyingOtp = false
                            onAuthSuccess()
                        },
                        onError = { err ->
                            isVerifyingOtp = false
                            isOtpErrorBanner = true
                            otpStatusMessage = err
                        }
                    )
                },
                enabled = !isVerifyingOtp,
                colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("تأكيد ملكية الحساب والدخول", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
