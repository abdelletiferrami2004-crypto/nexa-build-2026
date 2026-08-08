package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun FacebookDataSaverBanner(
    isDataSaverEnabled: Boolean,
    isSocialPass6Active: Boolean,
    onToggleDataSaver: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPass6InfoModal by remember { mutableStateOf(false) }

    if (showPass6InfoModal) {
        AlertDialog(
            onDismissRequest = { showPass6InfoModal = false },
            containerColor = BackgroundDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = "Social Pass *6",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "جواز التواصل الاجتماعي (*6)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "تم تصميم معمارية NEXA لتعمل بتوافق كامل 100% مع جوازات البيانات المجانية (*6 / Social Media Pass)، مما يتيح لك استهلاك غير محدود وتصفح سريع بدون استخدام بيانات الرصيد الأساسي.",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "الخدمات المدعومة مجاناً عبر *6:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            PassFeatureItem("فيديوهات الريلز ومقاطع الفيديو HD/SD", true)
                            PassFeatureItem("منشورات الصور وتغذية الأخبار (Feed)", true)
                            PassFeatureItem("الستوري والقصص التفاعلية", true)
                            PassFeatureItem("الدردشة والرسائل المباشرة والملاحظات الصوتية", true)
                            PassFeatureItem("التعليقات والإعجابات والتفاعلات المباشرة", true)
                            PassFeatureItem("البث المباشر والغرف الصوتية", true)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonAmber.copy(alpha = 0.15f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Data Saver Effect",
                            tint = NeonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDataSaverEnabled)
                                "وضع توفير البيانات (ON): إيقاف الفيديو التلقائي، دمج صور سريعة، وأقصى استجابة على شبكات 2G/3G/4G."
                            else
                                "وضع الجودة العالية (OFF): تشغيل الفيديو تلقائياً وجودة صور كامة.",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPass6InfoModal = false }) {
                    Text("حسناً، فهمت", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isDataSaverEnabled) {
                        listOf(Color(0xFF0F2B48), Color(0xFF133B63), Color(0xFF091E36))
                    } else {
                        listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F0F1A))
                    }
                )
            )
            .border(
                width = 1.dp,
                color = if (isDataSaverEnabled) NeonCyan.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info & Status Text
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showPass6InfoModal = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isDataSaverEnabled) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DataSaverOn,
                        contentDescription = "Data Saver Icon",
                        tint = if (isDataSaverEnabled) NeonCyan else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
 text = if (isDataSaverEnabled)"توفير البيانات (*6) مفعّل" else"وضع البيانات العادي (HD)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Details",
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Text(
                        text = if (isDataSaverEnabled)
                            "توقيف تشغيل الفيديو التلقائي • تحميل سريع جداً"
                        else
                            "جودة عالية HD • تشغيل تلقائي للريلز والوسائط",
                        color = Color.LightGray.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Switch Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isDataSaverEnabled) "Lite ON" else "OFF",
                    color = if (isDataSaverEnabled) NeonCyan else Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = isDataSaverEnabled,
                    onCheckedChange = { onToggleDataSaver(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    }
}

@Composable
private fun PassFeatureItem(title: String, isIncluded: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Supported",
            tint = EncryptedGreen,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color.White
        )
    }
}
