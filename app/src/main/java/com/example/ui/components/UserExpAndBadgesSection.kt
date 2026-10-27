package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeCategory
import com.example.data.model.BadgeRarity
import com.example.data.model.RankTier
import com.example.data.model.SocialExpBadge
import com.example.data.model.UserExpProgress
import com.example.ui.MajarrahViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

/**
 * Main UI component for tracking user EXP (Experience Points), level progression,
 * and displaying earned badges with social perks in the user profile section.
 */
@Composable
fun UserExpAndBadgesSection(
    viewModel: MajarrahViewModel,
    modifier: Modifier = Modifier
) {
    val expProgress by viewModel.userExpProgress.collectAsState()
    val context = LocalContext.current

    var selectedCategoryFilter by remember { mutableStateOf<BadgeCategory>(BadgeCategory.ALL) }
    var selectedStatusFilter by remember { mutableStateOf("ALL") } // "ALL", "UNLOCKED", "IN_PROGRESS"
    var selectedBadgeForModal by remember { mutableStateOf<SocialExpBadge?>(null) }
    var isActivityHistoryExpanded by remember { mutableStateOf(false) }

    // Pulsing aura animation for rank and high-tier badges
    val infiniteTransition = rememberInfiniteTransition(label = "badgePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // =========================================================
        // 1. EXP & Rank Progression Card
        // =========================================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header: Level Badge, Rank Title & Streak Multiplier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Level Orb with animated aura
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(expProgress.rankTier.gradientStartHex),
                                            Color(expProgress.rankTier.gradientEndHex),
                                            BackgroundDark
                                        )
                                    )
                                )
                                .border(
                                    2.dp,
                                    Color(expProgress.rankTier.gradientStartHex),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "LVL",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${expProgress.currentLevel}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = expProgress.rankTier.titleArabic,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(expProgress.rankTier.gradientStartHex),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = expProgress.rankTier.titleEnglish,
                                color = Color(expProgress.rankTier.gradientStartHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Streak & Multiplier Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        NeonPink.copy(alpha = 0.2f),
                                        NeonPurple.copy(alpha = 0.2f)
                                    )
                                )
                            )
                            .border(1.dp, NeonPink.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = NeonPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${expProgress.streakDays} أيام نشطة 🔥",
                                    color = NeonPink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "مضاعف x${expProgress.dailyMultiplier}",
                                    color = Color.LightGray,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // EXP Progress Bar with Animated Fill
                val animatedFraction by animateFloatAsState(
                    targetValue = expProgress.progressFraction,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                    label = "expProgressAnim"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "نقاط الخبرة (EXP)",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${expProgress.currentExp} / ${expProgress.nextLevelExp} EXP (${expProgress.progressPercentage}%)",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedFraction)
                            .height(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        NeonCyan,
                                        NeonPurple,
                                        Color(expProgress.rankTier.gradientStartHex)
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "متبقي ${expProgress.expNeededForNextLevel} EXP للمستوى التالي ${expProgress.currentLevel + 1} 🎯",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "إجمالي الخبرة: ${expProgress.totalExpEarned} ⭐",
                        color = NeonAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Quick EXP Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.testGainExp() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Add XP",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تفاعل اجتماعي (+65 EXP)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.claimDailyReward() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .border(1.dp, NeonPink, RoundedCornerShape(12.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Daily Streak",
                                tint = NeonAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تسجيل التواجد (+50 EXP)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // =========================================================
        // 2. Badges Showcase & Filter Section
        // =========================================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Section Title & Badges Counter Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonAmber.copy(alpha = 0.2f))
                                .border(1.dp, NeonAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Badges",
                                tint = NeonAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "أوسمة وشارات الإنجاز الاجتماعي",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "شارات تمنحك امتيازات ومضاعفات داخل مجرة NEXA",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Count Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(EncryptedGreen.copy(alpha = 0.15f))
                            .border(1.dp, EncryptedGreen, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${expProgress.earnedBadgesCount} / ${expProgress.totalBadgesCount} مكتسبة",
                            color = EncryptedGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Filter Chips (All, Unlocked, In Progress)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusFilterChip(
                        title = "الكل (${expProgress.badges.size})",
                        isSelected = selectedStatusFilter == "ALL",
                        onClick = { selectedStatusFilter = "ALL" }
                    )
                    StatusFilterChip(
                        title = "المكتسبة 🏆 (${expProgress.badges.count { it.isUnlocked }})",
                        isSelected = selectedStatusFilter == "UNLOCKED",
                        onClick = { selectedStatusFilter = "UNLOCKED" }
                    )
                    StatusFilterChip(
                        title = "قيد الإنجاز ⏳ (${expProgress.badges.count { !it.isUnlocked }})",
                        isSelected = selectedStatusFilter == "IN_PROGRESS",
                        onClick = { selectedStatusFilter = "IN_PROGRESS" }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Scrollable Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BadgeCategory.values().forEach { category ->
                        val isCatSelected = selectedCategoryFilter == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isCatSelected) NeonPurple.copy(alpha = 0.35f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    1.dp,
                                    if (isCatSelected) NeonPurple else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategoryFilter = category }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = category.titleArabic,
                                color = if (isCatSelected) Color.White else Color.LightGray,
                                fontSize = 10.sp,
                                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtered Badges List
                val filteredBadges = expProgress.badges.filter { badge ->
                    val statusMatch = when (selectedStatusFilter) {
                        "UNLOCKED" -> badge.isUnlocked
                        "IN_PROGRESS" -> !badge.isUnlocked
                        else -> true
                    }
                    val categoryMatch = if (selectedCategoryFilter == BadgeCategory.ALL) true
                    else badge.category == selectedCategoryFilter

                    statusMatch && categoryMatch
                }

                if (filteredBadges.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد أوسمة مطابقة لهذا التصنيف حالياً",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    // Grid Layout using paired rows for 2-column aesthetic
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        filteredBadges.chunked(2).forEach { rowBadges ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowBadges.forEach { badge ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        BadgeCardItem(
                                            badge = badge,
                                            onBadgeClick = { selectedBadgeForModal = badge },
                                            onClaimReward = { viewModel.claimSocialBadgeReward(badge.id) }
                                        )
                                    }
                                }
                                // Empty spacer if odd number of items in last row
                                if (rowBadges.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================
        // 3. Expandable Recent EXP Activity History Log
        // =========================================================
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isActivityHistoryExpanded = !isActivityHistoryExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سجل اكتساب نقاط الخبرة الأخيرة (EXP Log)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = if (isActivityHistoryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(visible = isActivityHistoryExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        expProgress.recentActivities.forEach { activity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(NeonPurple.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = activity.title,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${activity.description} • ${activity.timestamp}",
                                            color = Color.Gray,
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "+${activity.expGained} EXP",
                                    color = EncryptedGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================
    // 4. Modal Dialog for Detailed Badge View & Social Share
    // =========================================================
    selectedBadgeForModal?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadgeForModal = null },
            onClaim = {
                viewModel.claimSocialBadgeReward(badge.id)
                selectedBadgeForModal = null
            },
            onShare = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "شارة مجرة NEXA: ${badge.title}")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "🌟 حصلت على وسام ${badge.rarity.titleArabic} «${badge.title}» في مجرة NEXA! ${badge.socialPerk} 🚀"
                    )
                }
                context.startActivity(Intent.createChooser(shareIntent, "مشاركة وسام NEXA"))
            }
        )
    }
}

/**
 * Filter chip for status
 */
@Composable
private fun StatusFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) NeonCyan.copy(alpha = 0.25f)
                else Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.dp,
                if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) NeonCyan else Color.LightGray,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Single Badge Card within the Grid
 */
@Composable
private fun BadgeCardItem(
    badge: SocialExpBadge,
    onBadgeClick: () -> Unit,
    onClaimReward: () -> Unit
) {
    val rarityColor = Color(badge.rarity.colorHex)
    val isUnlocked = badge.isUnlocked
    val icon = getBadgeIconVector(badge.iconType)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (isUnlocked) {
                    Modifier.background(
                        Brush.verticalGradient(
                            listOf(
                                rarityColor.copy(alpha = 0.15f),
                                BackgroundDark.copy(alpha = 0.8f)
                            )
                        )
                    )
                } else {
                    Modifier.background(Color.White.copy(alpha = 0.03f))
                }
            )
            .border(
                1.5.dp,
                if (isUnlocked) rarityColor.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(18.dp)
            )
            .clickable { onBadgeClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rarity Tag & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = badge.rarity.iconEmoji,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(rarityColor.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge.rarity.titleArabic,
                        color = rarityColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badge Icon Orb
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) rarityColor.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.05f)
                    )
                    .border(
                        1.5.dp,
                        if (isUnlocked) rarityColor else Color.Gray.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = badge.title,
                    tint = if (isUnlocked) rarityColor else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = badge.title,
                color = if (isUnlocked) Color.White else Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Short Requirement / Description
            Text(
                text = badge.description,
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(28.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress or Claim State
            if (isUnlocked) {
                if (!badge.isClaimed) {
                    Button(
                        onClick = onClaimReward,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    ) {
                        Text(
                            text = "استلام +${badge.expReward} EXP",
                            color = BackgroundDark,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Claimed",
                            tint = EncryptedGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مكتسبة ✓",
                            color = EncryptedGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // In progress
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "التقدم",
                            color = Color.Gray,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "${badge.currentProgress}/${badge.requiredProgress}",
                            color = Color.LightGray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    val progressFraction = (badge.currentProgress.toFloat() / badge.requiredProgress.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = NeonCyan,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

/**
 * Detailed Dialog Modal for clicking on a badge
 */
@Composable
private fun BadgeDetailDialog(
    badge: SocialExpBadge,
    onDismiss: () -> Unit,
    onClaim: () -> Unit,
    onShare: () -> Unit
) {
    val rarityColor = Color(badge.rarity.colorHex)
    val icon = getBadgeIconVector(badge.iconType)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        shape = RoundedCornerShape(24.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Badge Emblem
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    rarityColor.copy(alpha = 0.4f),
                                    rarityColor.copy(alpha = 0.1f),
                                    BackgroundDark
                                )
                            )
                        )
                        .border(2.dp, rarityColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = badge.title,
                        tint = rarityColor,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rarity Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(rarityColor.copy(alpha = 0.2f))
                        .border(1.dp, rarityColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${badge.rarity.iconEmoji} ${badge.rarity.titleArabic} • ${badge.rarity.titleEnglish.uppercase()}",
                        color = rarityColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = badge.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = badge.description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Requirement & Unlocking Rule
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "شروط الاكتساب والتحقيق:",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = badge.requirementDescription,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                        if (badge.unlockedDate != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تاريخ الاكتساب: ${badge.unlockedDate}",
                                color = EncryptedGreen,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Social Perk & Community Privilege
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonPurple.copy(alpha = 0.15f))
                        .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Perk",
                                tint = NeonPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الامتياز الاجتماعي الممنوح:",
                                color = NeonPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = badge.socialPerk,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (badge.isUnlocked && !badge.isClaimed) {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "استلام (+${badge.expReward} EXP)",
                            color = BackgroundDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = BackgroundDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة الوسام", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = Color.Gray)
            }
        }
    )
}

/**
 * Maps icon types to Material vectors
 */
private fun getBadgeIconVector(iconType: String): ImageVector {
    return when (iconType.lowercase()) {
        "fire" -> Icons.Default.LocalFireDepartment
        "voice" -> Icons.Default.Psychology
        "shield" -> Icons.Default.Security
        "lock" -> Icons.Default.Lock
        "crown" -> Icons.Default.EmojiEvents
        "diamond" -> Icons.Default.Diamond
        "vip" -> Icons.Default.WorkspacePremium
        "star" -> Icons.Default.Star
        "bolt" -> Icons.Default.Bolt
        "video", "reel" -> Icons.Default.Videocam
        else -> Icons.Default.AutoAwesome
    }
}
