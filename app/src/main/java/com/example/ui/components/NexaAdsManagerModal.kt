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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

data class AdCampaign(
    val id: String,
    val title: String,
    val goal: String,
    val budgetDaily: String,
    val impressions: String,
    val clicks: String,
    val spent: String,
    var isActive: Boolean = true
)

@Composable
fun NexaAdsManagerModal(
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Campaigns, 1: Launch New Ad, 2: Monetization Payouts
    var showConfirmationToast by remember { mutableStateOf<String?>(null) }

    val campaigns = remember {
        mutableStateListOf(
            AdCampaign("ad1", "حملة إعلان متجر NEXA للذكاء الاصطناعي", "زيارات الموقع", "25$ / يوم", "142.8K", "8.4K", "180$", true),
            AdCampaign("ad2", "تمويل منشور الريلز المباشر", "زيادة المتابعين", "10$ / يوم", "65.2K", "4.1K", "60$", true),
            AdCampaign("ad3", "عروض الخصم الموسمية", "مبيعات المباشرة", "50$ / يوم", "310.5K", "19.2K", "450$", false)
        )
    }

    // New Ad Campaign States
    var adTitle by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf("ترويج المنشورات والريلز") }
    var targetRegion by remember { mutableStateOf("السعودية، الإمارات، قطر، الكويت") }
    var targetInterests by remember { mutableStateOf("التقنية، الذكاء الاصطناعي، التجارة الرقمية") }
    var dailyBudget by remember { mutableFloatStateOf(15f) }
    var durationDays by remember { mutableFloatStateOf(7f) }

    val estimatedReachLower = (dailyBudget * 850).toInt()
    val estimatedReachUpper = (dailyBudget * 2200).toInt()

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
                                .background(NeonAmber.copy(alpha = 0.2f))
                                .border(1.dp, NeonAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Ads Center",
                                tint = NeonAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مركز الإعلانات والحملات الممولة (Ads Center)",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "NEXA Monetization & Ad Manager",
                                color = NeonAmber,
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

                // Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf("الحملات النشطة", "إنشاء إعلان ممول", "الأرباح والمحفظة")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = activeTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) NeonAmber.copy(alpha = 0.3f)
                                    else CardBackground
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonAmber else CardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { activeTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                showConfirmationToast?.let { toastMsg ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = NeonAmber.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = toastMsg, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                when (activeTab) {
                    0 -> {
                        // Campaigns Overview Tab
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                CampaignSummaryMetricsCard()
                            }

                            item {
                                Text(
                                    text = "الحملات الإعلانية الحالية والممولة:",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(campaigns) { campaign ->
                                CampaignCard(
                                    campaign = campaign,
                                    onToggleStatus = { newStatus ->
                                        campaign.isActive = newStatus
                                        showConfirmationToast = if (newStatus) "تم تفعيل الحملة '${campaign.title}'" else "تم إيقاف الحملة مؤقتاً"
                                    }
                                )
                            }
                        }
                    }

                    1 -> {
                        // Launch New Ad Campaign Wizard
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "إطلاق حملة ممولة جديدة وتحديد الجمهور المستهدف 🎯",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = adTitle,
                                    onValueChange = { adTitle = it },
                                    label = { Text("عنوان الحملة الإعلانية", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonAmber,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }

                            item {
                                Text("هدف الحملة الإعلانية:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val goals = listOf("ترويج المنشورات والريلز", "زيادة المتابعين", "زيارات الموقع/المتجر")
                                    goals.forEach { goal ->
                                        val isSel = selectedGoal == goal
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSel) NeonAmber.copy(alpha = 0.25f) else CardBackground)
                                                .border(1.dp, if (isSel) NeonAmber else CardBorder, RoundedCornerShape(10.dp))
                                                .clickable { selectedGoal = goal }
                                                .padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(goal, color = if (isSel) Color.White else Color.Gray, fontSize = 10.sp, textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }

                            item {
                                OutlinedTextField(
                                    value = targetRegion,
                                    onValueChange = { targetRegion = it },
                                    label = { Text("الاستهداف الجغرافي (الدول / المدن)", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonAmber,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = targetInterests,
                                    onValueChange = { targetInterests = it },
                                    label = { Text("اهتمامات الجمهور (التقنية، الموضة...)", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonAmber,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }

                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("الميزانية اليومية:", color = Color.White, fontSize = 13.sp)
                                            Text("${dailyBudget.toInt()}$ / يوم", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Slider(
                                            value = dailyBudget,
                                            onValueChange = { dailyBudget = it },
                                            valueRange = 5f..200f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = NeonAmber,
                                                activeTrackColor = NeonAmber
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("مدة الحملة:", color = Color.White, fontSize = 13.sp)
                                            Text("${durationDays.toInt()} أيام (الإجمالي: ${(dailyBudget * durationDays).toInt()}$)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Slider(
                                            value = durationDays,
                                            onValueChange = { durationDays = it },
                                            valueRange = 1f..30f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = NeonCyan,
                                                activeTrackColor = NeonCyan
                                            )
                                        )
                                    }
                                }
                            }

                            item {
                                // Realtime Impression Estimator Gauge
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = NeonAmber.copy(alpha = 0.1f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Speed, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(28.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("تقديرات الوصول اليومي المباشر:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "بين $estimatedReachLower و $estimatedReachUpper شخص يومياً",
                                                color = NeonAmber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        if (adTitle.isNotBlank()) {
                                            val newAd = AdCampaign(
                                                id = "ad_${System.currentTimeMillis()}",
                                                title = adTitle,
                                                goal = selectedGoal,
                                                budgetDaily = "${dailyBudget.toInt()}$ / يوم",
                                                impressions = "1K",
                                                clicks = "120",
                                                spent = "5$",
                                                isActive = true
                                            )
                                            campaigns.add(0, newAd)
                                            showConfirmationToast = "تم إطلاق الحملة الممولة '${adTitle}' بنجاح عبر محفظة NEXA! 🚀"
                                            adTitle = ""
                                            activeTab = 0
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("تأكيد وإدراج الحملة الإعلانية المباشرة", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Monetization & Payouts Tab
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("رصيد أرباح المنشئين والمؤسسات", color = Color.Gray, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("1,420.50 $ USD", color = NeonAmber, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("أرباح الريلز، الإعلانات المدمجة، والدعم المباشر", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        showConfirmationToast = "تم تقديم طلب سحب الأرباح لمصرفك بنجاح 🏦"
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("سحب الأرباح للحساب البنكي / PayPal", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampaignSummaryMetricsCard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("إجمالي الظهور", color = Color.Gray, fontSize = 10.sp)
                Text("518.5K", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("إجمالي النقرات", color = Color.Gray, fontSize = 10.sp)
                Text("31.7K", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("إجمالي الإنفاق", color = Color.Gray, fontSize = 10.sp)
                Text("690$", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CampaignCard(
    campaign: AdCampaign,
    onToggleStatus: (Boolean) -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = campaign.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "الهدف: ${campaign.goal} • الميزانية: ${campaign.budgetDaily}", color = Color.Gray, fontSize = 11.sp)
                }

                Switch(
                    checked = campaign.isActive,
                    onCheckedChange = onToggleStatus,
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonAmber)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "الظهور: ${campaign.impressions}", color = Color.White, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mouse, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "النقرات: ${campaign.clicks}", color = NeonCyan, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "المنفق: ${campaign.spent}", color = NeonAmber, fontSize = 11.sp)
                }
            }
        }
    }
}
