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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

data class DeviceSession(
    val id: String,
    val deviceName: String,
    val location: String,
    val lastActive: String,
    val isCurrent: Boolean = false
)

@Composable
fun NexaSecurityAnd2faModal(
    onDismiss: () -> Unit
) {
    var is2faEnabled by remember { mutableStateOf(true) }
    var isAutoModerationEnabled by remember { mutableStateOf(true) }
    var isBiometricAuthEnabled by remember { mutableStateOf(true) }
    var showSuccessToast by remember { mutableStateOf<String?>(null) }

    val activeSessions = remember {
        mutableStateListOf(
            DeviceSession("d1", "Samsung Galaxy S24 Ultra", "الرياض، السعودية", "نشط الآن 🟢", isCurrent = true),
            DeviceSession("d2", "MacBook Pro M3 Max", "دبي، الإمارات العربية المتحدة", "قبل ساعتين"),
            DeviceSession("d3", "iPhone 15 Pro Max", "القاهرة، مصر", "قبل يوم واحد")
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = BackgroundDark,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EncryptedGreen.copy(alpha = 0.2f))
                                .border(1.dp, EncryptedGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security",
                                tint = EncryptedGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "الأمان والحماية الفائقة (Enterprise Security)",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "2FA, Device Sessions & Content Moderation",
                                color = EncryptedGreen,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
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

                showSuccessToast?.let { toastMsg ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = EncryptedGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EncryptedGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EncryptedGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = toastMsg, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Two-Factor Authentication (2FA)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PhonelinkLock, contentDescription = null, tint = EncryptedGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("المصادقة الثنائية (2FA)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("تأكيد تسجيل الدخول عبر تطبيق Authenticator أو SMS", color = Color.Gray, fontSize = 11.sp)
                                        }
                                    }

                                    Switch(
                                        checked = is2faEnabled,
                                        onCheckedChange = {
                                            is2faEnabled = it
                                            showSuccessToast = if (it) "تم تفعيل المصادقة الثنائية (2FA) لحسابك بنجاح 🔐" else "تم إيقاف المصادقة الثنائية"
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = EncryptedGreen)
                                    )
                                }

                                if (is2faEnabled) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(EncryptedGreen.copy(alpha = 0.15f))
                                            .border(1.dp, EncryptedGreen, RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.QrCode, contentDescription = null, tint = EncryptedGreen)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("رمز الأمان المخصص: NEXA-8942-SECURE-2FA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Automated Content Moderation Engine
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("نظام الفلترة المباشرة بالذكاء الاصطناعي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("حظر العبارات المسيئة والمحتوى الضار تلقائياً", color = Color.Gray, fontSize = 11.sp)
                                        }
                                    }

                                    Switch(
                                        checked = isAutoModerationEnabled,
                                        onCheckedChange = {
                                            isAutoModerationEnabled = it
                                            showSuccessToast = if (it) "محرّك الموديريتور الذكي نشط ومستعد" else "تم إيقاف الفلترة التلقائية"
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Device Session Manager
                    item {
                        Text(
                            text = "الأجهزة والجلسات المفتوحة حالياً (${activeSessions.size}):",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(activeSessions) { device ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (device.isCurrent) EncryptedGreen else CardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (device.deviceName.contains("MacBook") || device.deviceName.contains("PC")) Icons.Default.Computer else Icons.Default.Smartphone,
                                        contentDescription = null,
                                        tint = if (device.isCurrent) EncryptedGreen else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = device.deviceName + if (device.isCurrent) " (الجهاز الحالي)" else "",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${device.location} • ${device.lastActive}",
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (!device.isCurrent) {
                                    IconButton(
                                        onClick = {
                                            activeSessions.remove(device)
                                            showSuccessToast = "تم إنهاء جلسة ${device.deviceName}"
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Revoke", tint = Color.Red.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }

                    // 4. "Log out of all other sessions" Action Button
                    item {
                        Button(
                            onClick = {
                                activeSessions.removeIf { !it.isCurrent }
                                showSuccessToast = "تم تسجيل الخروج فوراً من كافة الأجهزة والجلسات الأخرى! 🛡️"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تسجيل الخروج من كافة الأجهزة الأخرى فوراً", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
