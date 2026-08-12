package com.example.ui.profile

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileManagementScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBiometricAuthRequest: () -> Unit = {},
    onAccountDeleted: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    LaunchedEffect(uiState.phoneNumber, uiState.fullName, uiState.city, uiState.bio) {
        phone = uiState.phoneNumber
        name = uiState.fullName
        city = uiState.city
        bio = uiState.bio
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "NEXA",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E88E5)
        )
        Text(
            text = "منصة إدارة الحسابات والخصوصية",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "الملف الشخصي والبيانات",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )

        // Text Fields
        ProfileTextField(value = phone, onValueChange = { phone = it }, placeholder = "+212753721972")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileTextField(value = name, onValueChange = { name = it }, placeholder = "أحمد المحترف")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileTextField(value = city, onValueChange = { city = it }, placeholder = "الدار البيضاء")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileTextField(value = bio, onValueChange = { bio = it }, placeholder = "طور تطبيقات 🚀")

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        ActionButton(
            text = "حفظ / تحديث البروفايل 💾",
            color = Color(0xFF1E88E5)
        ) {
            viewModel.updateProfile(phone, name, city, bio)
        }

        Spacer(modifier = Modifier.height(8.dp))

        ActionButton(
            text = "بحث ف قاعدة البيانات 🔍",
            color = Color(0xFF4CAF50)
        ) {
            viewModel.searchDatabase(name) { results ->
                Toast.makeText(context, "تم العثور على ${results.size} نتائج", Toast.LENGTH_SHORT).show()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ActionButton(
            text = "حظر / إلغاء حظر رقم 🚫",
            color = Color(0xFFFF9800)
        ) {
            if (phone.isNotBlank()) {
                viewModel.toggleBlockNumber(phone)
            } else {
                Toast.makeText(context, "أدخل الرقم أولاً", Toast.LENGTH_SHORT).show()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ActionButton(
            text = "توثيق بالبصمة 🔒",
            color = Color(0xFF8E24AA)
        ) {
            val activity = context as? FragmentActivity
            if (activity != null) {
                val executor = ContextCompat.getMainExecutor(context)
                val biometricPrompt = BiometricPrompt(
                    activity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Toast.makeText(context, "تم التوثيق بالبصمة بنجاح! 🟢", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(context, "تأكيد البصمة: $errString", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(context, "لم يتم التعرف على البصمة", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("توثيق الهوية بالبصمة")
                    .setSubtitle("منصة NEXA لإدارة الحسابات والخصوصية")
                    .setNegativeButtonText("إلغاء")
                    .build()

                try {
                    biometricPrompt.authenticate(promptInfo)
                } catch (e: Exception) {
                    Toast.makeText(context, "تعذر تشغيل البصمة: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            } else {
                onBiometricAuthRequest()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ActionButton(
            text = "حذف الحساب 🗑️",
            color = Color(0xFFE53935)
        ) {
            viewModel.deleteAccount(onAccountDeleted)
        }
    }
}

@Composable
fun ProfileTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF0F4F8),
            unfocusedContainerColor = Color(0xFFF0F4F8),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
