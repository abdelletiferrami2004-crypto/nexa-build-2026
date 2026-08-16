package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GooglePlayLegalModal
import com.example.ui.components.LegalTab
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.ui.components.DailyRewardsAndReferralModal
import com.example.ui.components.E2eEncryptionStatusCard
import com.example.ui.components.InAppCreditsTopUpModal
import com.example.ui.components.NexaVipSubscriptionModal

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.model.AppLanguage
import com.example.util.LanguageManager
import com.example.ui.components.CreatorAvatarWithAura
import com.example.ui.components.DailyStreakAndRankCard
import com.example.data.model.CreatorBadgeTier
import androidx.compose.ui.graphics.Brush

@Composable
fun ProfileScreen(
    viewModel: MajarrahViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val isTeen = profile?.isTeenMode ?: true
    val age = profile?.age ?: 16
    val followersCount = profile?.followersCount ?: 1_250_000
    val totalViewsCount = profile?.totalViewsCount ?: 1_450_000L

    val selectedLang by viewModel.selectedLanguage.collectAsState()
    val effectiveLang = LanguageManager.getEffectiveLanguage(selectedLang)
    val systemLangDisplay = java.util.Locale.getDefault().displayName

    val isPayoutClaimed by viewModel.isPayoutClaimed.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    val isFirebaseAvailable by viewModel.isFirebaseAvailable.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
    val currentFirebaseUser by viewModel.currentFirebaseUser.collectAsState()

    val isVipMember = profile?.isVipMember ?: false
    val vipTierName = profile?.vipTierName ?: "NEXA VIP Diamond"
    val creditsBalance = profile?.creditsBalance ?: 850
    val referralCode = profile?.referralCode ?: "NEXA-8821"
    val claimedStreakDays = profile?.claimedDailyRewardDays ?: 3
    val isE2eEncryptionEnabled = profile?.isE2eEncryptionEnabled ?: true

    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adWatchProgress by viewModel.adWatchProgress.collectAsState()

    val isDataSaverEnabled by viewModel.isDataSaverEnabled.collectAsState()
    val isSocialPass6Active by viewModel.isSocialPass6Active.collectAsState()
    val isBiometricAppLockEnabled by viewModel.isBiometricEnabledForAccount.collectAsState()

    val profileImageLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            viewModel.updateProfileAvatar(it.toString())
        }
    }

    var showVipModal by remember { mutableStateOf(false) }
    var showCreditsModal by remember { mutableStateOf(false) }
    var showDailyRewardsModal by remember { mutableStateOf(false) }

    var showLangDropdown by remember { mutableStateOf(false) }
    var showLegalModal by remember { mutableStateOf(false) }
    var activeLegalTab by remember { mutableStateOf(com.example.ui.components.LegalTab.PRIVACY_POLICY) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editNameInput by remember(profile?.name) { mutableStateOf(profile?.name ?: "سارة النمر") }
    var editBioInput by remember(profile?.bio) { mutableStateOf(profile?.bio ?: "عاشقة للتكنولوجيا والذكاء الاصطناعي والتسوق النيون 🚀") }
    var isNotificationsEnabled by remember { mutableStateOf(true) }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = BackgroundDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تعديل الملف الشخصي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("اسم المستخدم والظهور:", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("الحالة والنبذة التعريفية (Bio):", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editBioInput,
                        onValueChange = { editBioInput = it },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            profileImageLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Change Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تغيير الصورة الشخصية 🖼️", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDisplayName(editNameInput)
                        viewModel.updateBio(editBioInput)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حفظ التغييرات", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            }
        )
    }

    if (showVipModal) {
        NexaVipSubscriptionModal(
            isCurrentlyVip = isVipMember,
            onSubscribe = { tier -> viewModel.activateVipSubscription(tier) },
            onDismiss = { showVipModal = false }
        )
    }

    if (showCreditsModal) {
        InAppCreditsTopUpModal(
            creditsBalance = creditsBalance,
            isAdWatching = isAdWatching,
            adWatchProgress = adWatchProgress,
            onTopUp = { amount, price -> viewModel.topUpCredits(amount, price) },
            onWatchAd = { viewModel.watchRewardedAdForCredits() },
            onDismiss = { showCreditsModal = false }
        )
    }

    if (showDailyRewardsModal) {
        DailyRewardsAndReferralModal(
            claimedStreakDays = claimedStreakDays,
            referralCode = referralCode,
            onClaimDaily = { viewModel.claimDailyReward() },
            onApplyReferral = { code -> viewModel.applyReferralCode(code) },
            onDismiss = { showDailyRewardsModal = false }
        )
    }


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = LanguageManager.getString("profile_title", selectedLang),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar Card with Royal Creator Aura Frame
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.clickable { profileImageLauncher.launch("image/*") }
                ) {
                    CreatorAvatarWithAura(
                        followersCount = followersCount,
                        authorInitial = profile?.name ?: "S",
                        size = 72.dp,
                        showBadgeChip = true
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile?.name ?: "سارة النمر",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = CreatorBadgeTier.fromFollowers(followersCount).badgeEmoji,
                            fontSize = 16.sp
                        )
                    }

                    Text(
                        text = profile?.phone ?: "+966 50 123 4567",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        GlassBadge(
                            text = if (isTeen) "حساب الناشئة" else "حساب كامل",
                            accentColor = if (isTeen) TeenProtectionCyan else NeonPurple
                        )

                        GlassBadge(
                            text = CreatorBadgeTier.fromFollowers(followersCount).titleAr,
                            accentColor = Color(CreatorBadgeTier.fromFollowers(followersCount).primaryColorHex)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                editNameInput = profile?.name ?: "سارة النمر"
                                editBioInput = profile?.bio ?: "عاشقة للتكنولوجيا والذكاء الاصطناعي والتسوق النيون 🚀"
                                showEditProfileDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تعديل البيانات ✏️",
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { profileImageLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Upload Profile Avatar",
                                    tint = NeonPurple,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "الصورة 📸",
                                    color = NeonPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Royal Creator Badges System Card
        CreatorRoyalBadgesCard(
            viewModel = viewModel,
            followersCount = followersCount
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Creator Monetization Fund Card (1M Views)
        CreatorMonetizationCard(
            viewModel = viewModel,
            totalViewsCount = totalViewsCount,
            isPayoutClaimed = isPayoutClaimed
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Gamified Daily Streak & User Rank/Badges Card
        DailyStreakAndRankCard(
            streakDays = claimedStreakDays,
            userXp = (claimedStreakDays * 120 + 250).coerceAtMost(1000),
            nextRankXp = 1000,
 currentRankTitle = if (claimedStreakDays >= 5)"خبير الذكاء" else"مستكشف NEXA",
            onClaimStreak = { viewModel.claimDailyReward() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Language & System Auto-Detection Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = NeonCyan,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = LanguageManager.getString("lang_settings_title", selectedLang),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "دعم لغات العالم + فرز تلقائي لـ لغة الجهاز",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auto Detected Language Info Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
 Text(" ${LanguageManager.getString("system_detected", selectedLang)}", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(systemLangDisplay, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اللغة النشطة حالياً: ${effectiveLang.flagEmoji} ${effectiveLang.displayNameNative}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Manual Language Picker Button & Menu
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp))
                            .clickable { showLangDropdown = !showLangDropdown }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedLang.flagEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedLang.displayNameNative,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Language",
                            tint = NeonCyan
                        )
                    }

                    DropdownMenu(
                        expanded = showLangDropdown,
                        onDismissRequest = { showLangDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(BackgroundDark)
                            .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(lang.flagEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = lang.displayNameNative,
                                            color = if (selectedLang == lang) NeonCyan else Color.White,
                                            fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.setAppLanguage(lang)
                                    showLangDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Push Notifications Settings Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = NeonAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إشعارات التطبيق والرسائل",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "تنبيهات فورية عند وصول رسائل، عروض، وردود الذكاء الاصطناعي",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isNotificationsEnabled,
                        onCheckedChange = { isNotificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = NeonAmber,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Teen Protection Switcher Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = TeenProtectionCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
 text ="تفعيل وضع الناشئة",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "تفلترة المنتجات غير المناسبة والمحتوى الحساس",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isTeen,
                        onCheckedChange = { viewModel.toggleTeenMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = TeenProtectionCyan,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "العمر المسجل: $age سنة",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Slider(
                    value = age.toFloat(),
                    onValueChange = { viewModel.setAge(it.toInt()) },
                    valueRange = 10f..50f,
                    steps = 40,
                    colors = SliderDefaults.colors(
                        thumbColor = if (isTeen) TeenProtectionCyan else NeonPurple,
                        activeTrackColor = if (isTeen) TeenProtectionCyan else NeonPurple,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PIN & Biometric Protection Settings
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric Lock",
                            tint = EncryptedGreen,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "قفل التطبيق بالبصمة",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "حماية التطبيق كلياً بالبصمة الرسمية للوجه والأصبع",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isBiometricAppLockEnabled,
                        onCheckedChange = { viewModel.toggleBiometricAppLock(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = EncryptedGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.lockAppNow() },
                        colors = ButtonDefaults.buttonColors(containerColor = EncryptedGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Lock App",
                                tint = BackgroundDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "قفل التطبيق الآن 🔒",
                                color = BackgroundDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.lockChat() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Chat",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "قفل الدردشة بـ PIN",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Facebook-Style Network Data Saver (*6 Social Media Pass) Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.DataSaverOn,
                            contentDescription = "Data Saver",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
 text ="توفير البيانات (*6 Social Pass)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "تأكيد التوافق الأصلي مع معمارية *6 Lite",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isDataSaverEnabled,
                        onCheckedChange = { viewModel.toggleDataSaver(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = NeonCyan,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                                contentDescription = "Pass 6 Active",
                                tint = EncryptedGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "جواز التواصل الاجتماعي (*6) مفعّل تلقائياً",
                                color = EncryptedGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (isDataSaverEnabled)
                                "• وضع توفير البيانات ON: إيقاف تشغيل الفيديو التلقائي، تحميل سريع جداً واستجابة فائقة."
                            else
                                "• وضع توفير البيانات OFF: تشغيل تلقائي للوسائط بجودة HD العالية.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Anomaly Detection & Smart Security Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "AI Security",
                        tint = NeonCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
 text ="نظام الذكاء الاصطناعي لكشف الأنشطة المشبوهة (AI Anomaly)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "حماية فورية بالحشف والـ Biometrics مع خروج تلقائي 10s عند الخطر",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        viewModel.triggerAIAnomalyAlert("محاولة دخول غير مسبوقة من جهاز جديد ونظام تشغيل غريب (اختبار الأمان)")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Test AI Anomaly",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
 text ="اختبار كشف الأنشطة المشبوهة بالذكاء الاصطناعي (AI Security Test)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

 // 1⃣ E2E Encryption Security Card
        E2eEncryptionStatusCard(
            isE2eEnabled = isE2eEncryptionEnabled,
            onToggle = { enabled -> viewModel.toggleE2EEncryption(enabled) }
        )

        Spacer(modifier = Modifier.height(20.dp))

 // 2⃣ NEXA VIP Subscription Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "VIP",
                            tint = NeonCyan,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
 text = if (isVipMember)"اشتراك NEXA VIP ($vipTierName)" else"عضوية NEXA VIP الملكية",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isVipMember) "عضويتك نشطة: سرعة AI مضاعفة وبدون إعلانات" else "احصل على سرعة AI مضاعفة وإزالة الإعلانات وشارة التوثيق",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showVipModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "VIP",
                            tint = BackgroundDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
 text = if (isVipMember)"إدارة وتجديد اشتراك VIP" else"ترقية الحساب إلى VIP الملكي",
                            color = BackgroundDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

 // 3⃣ In-App Credits & Daily Rewards Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Credits Card
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showCreditsModal = true },
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Toll,
                        contentDescription = "Credits",
                        tint = NeonAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "رصيد العملات",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$creditsBalance رصيد",
                        color = NeonAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonAmber)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
 Text("شحن الآن", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }

            // Daily Rewards Card
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDailyRewardsModal = true },
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Rewards",
                        tint = NeonPink,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "المكافآت والإحالة",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
 text ="سلسلة $claimedStreakDays أيام",
                        color = NeonPink,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonPink)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
 Text("استلام", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Firebase Cloud Database & Authentication Card

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Firebase Cloud",
                        tint = NeonCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
 text ="ربط وتزامن سحابة Firebase Cloud",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "المصادقة سحابياً والحفظ المباشر لبيانات المستخدمين والمحتوى في Firestore",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "حالة الاتصال السحابي:",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cloudSyncStatus,
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                        if (currentFirebaseUser != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Firebase UID: ${currentFirebaseUser?.uid}",
                                color = NeonCyan,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.syncWithFirebaseCloud() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sync Firebase",
                            tint = BackgroundDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
 text ="مزامنة الحساب والمحتوى سحابياً مع Firebase",
                            color = BackgroundDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Google Play Store 2026 Legal & Policies Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Legal Policies",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
 text ="سياسة الخصوصية وشروط الخدمة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "مطابقة لتحديثات قوانين Google Play Store 2026 وسلامة البيانات",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            activeLegalTab = LegalTab.PRIVACY_POLICY
                            showLegalModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "سياسة الخصوصية",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            activeLegalTab = LegalTab.TERMS_OF_SERVICE
                            showLegalModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = NeonPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "شروط الخدمة",
                                color = NeonPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Blocked Users Management Card (Google Play Safety)
        if (blockedUsers.isNotEmpty()) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Blocked Users",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "قائمة الحظر والمنع (Blocked Users)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "المستخدمون الذين قمت بحظرهم من رؤية محتواك والتفاعل معك",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        blockedUsers.forEach { userName ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = userName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )

                                Button(
                                    onClick = { viewModel.unblockUser(userName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                                ) {
                                    Text(
                                        text = "إلغاء الحظر",
                                        color = Color.Red,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Permanent Account & Data Deletion Card (Google Play 2026 Mandate)
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Account",
                        tint = Color.Red,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "حذف الحساب والبيانات نهائياً (Account Deletion)",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "إزالة جميع البيانات والمنشورات والمحفظة نهائياً من سيرفرات NEXA",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
 text ="طلب حذف الحساب وجميع البيانات فوراً",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Modal & Dialog Triggers
    if (showLegalModal) {
        GooglePlayLegalModal(
            initialTab = activeLegalTab,
            onDismiss = { showLegalModal = false }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color.Red,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "هل أنت متاكد من حذف حسابك نهائياً؟",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "وفقاً لسياسة Google Play Store 2026، فإن الاستمرار في هذا الخيار سيؤدي إلى مسح كلي لجميع فيديوهاتك، تعليقاتك، رسائلك المشفرة، ورصيد أرباحك في منصة NEXA فوراً ودون أي إمكانية لاسترجاعها.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccountAndData()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("نعم، احذف حسابي وبياناتي نهائياً", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("إلغاء", color = NeonCyan)
                }
            },
            containerColor = BackgroundDark,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }
}

@Composable
fun CreatorRoyalBadgesCard(
    viewModel: MajarrahViewModel,
    followersCount: Int
) {
    val tier = CreatorBadgeTier.fromFollowers(followersCount)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
 Text("", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نظام الأوسمة الملكية لصناع المحتوى",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "شارات حصرية وهالات نيون حول البروفايل والتعليقات",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Badge Tier Highlight Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (tier) {
                            CreatorBadgeTier.DIAMOND_VIP -> Brush.horizontalGradient(
                                listOf(Color(0xFF00F5FF).copy(alpha = 0.25f), Color(0xFFFF007F).copy(alpha = 0.25f))
                            )
                            CreatorBadgeTier.GOLD_CRYSTAL -> Brush.horizontalGradient(
                                listOf(Color(0xFFFFD700).copy(alpha = 0.25f), Color(0xFFFFA500).copy(alpha = 0.25f))
                            )
                            CreatorBadgeTier.SILVER -> Brush.horizontalGradient(
                                listOf(Color(0xFFE0E0E0).copy(alpha = 0.25f), Color(0xFF999999).copy(alpha = 0.25f))
                            )
                            CreatorBadgeTier.NONE -> Brush.horizontalGradient(
                                listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
                            )
                        }
                    )
                    .border(
                        width = 1.5.dp,
                        brush = when (tier) {
                            CreatorBadgeTier.DIAMOND_VIP -> Brush.horizontalGradient(listOf(Color(0xFF00F5FF), Color(0xFFFF007F)))
                            CreatorBadgeTier.GOLD_CRYSTAL -> Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500)))
                            CreatorBadgeTier.SILVER -> Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFFB0B0B0)))
                            CreatorBadgeTier.NONE -> Brush.horizontalGradient(listOf(Color.Gray, Color.DarkGray))
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "الوسم المكتسب حالياً:",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tier.titleAr,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
 text ="${String.format("%,d", followersCount)} متابع",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tiers Progression Roadmap
 Text("مستويات الأوسمة والهالات", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeLevelRow(
 emoji ="",
                    title = "100K متابع: وسم وتوهج فضي",
                    desc = "وسم مجرة الفضي بلمعة فضية حول الصورة والردود",
                    isReached = followersCount >= 100_000,
                    accentColor = Color(0xFFC0C0C0)
                )

                BadgeLevelRow(
 emoji ="",
                    title = "500K متابع: وسم وهالة ذهبية كريستالية 3D",
                    desc = "إطار ذهبي ثلاثي الأبعاد مع انعكاسات ضوئية كريستالية",
                    isReached = followersCount >= 500_000,
                    accentColor = Color(0xFFFFD700)
                )

                BadgeLevelRow(
 emoji ="",
                    title = "1M متابع: وسم وإطار نيون ماسي (VIP Diamond Aura)",
                    desc = "هالة نيون أسطورية متلألئة بألوان الفضاء النيون",
                    isReached = followersCount >= 1_000_000,
                    accentColor = Color(0xFF00F5FF)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Tester / Simulator Buttons
 Text("اختبار أوسمة المتابعين فوراً :", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { viewModel.updateFollowersCount(50_000) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("50K", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFC0C0C0).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFFC0C0C0), RoundedCornerShape(10.dp))
                        .clickable { viewModel.updateFollowersCount(150_000) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
 Text("150K", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFD700).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(10.dp))
                        .clickable { viewModel.updateFollowersCount(600_000) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
 Text("600K", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00F5FF).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF00F5FF), RoundedCornerShape(10.dp))
                        .clickable { viewModel.updateFollowersCount(1_250_000) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
 Text("1.2M", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CreatorMonetizationCard(
    viewModel: MajarrahViewModel,
    totalViewsCount: Long,
    isPayoutClaimed: Boolean
) {
    val isUnlocked = totalViewsCount >= 1_000_000L

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
 Text("", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "صندوق أرباح صناع المحتوى (Monetization Fund)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "ربح تلقائي فور الوصول لـ 1M مشاهدة على منصة NEXA",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isUnlocked) {
                // Unlocked Monetization Dashboard
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                        .border(1.5.dp, Color(0xFF00E676), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
 Text("حسابك مفعل في صندوق الربح", color = Color(0xFF00E676), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF00E676))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("1M+ Unlocked", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("الأرباح المقدرة (الشهر الحالي):", color = Color.LightGray, fontSize = 11.sp)
                                Text("$1,850.40 USD", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
 Text("~ 6,939.00 SAR", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("معدل الـ RPM:", color = Color.LightGray, fontSize = 11.sp)
                                Text("$1.28 / 1k مشاهدة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("المشاهدات: ${String.format("%,d", totalViewsCount)}", color = Color.LightGray, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isPayoutClaimed) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF00E676).copy(alpha = 0.3f))
                                    .border(1.dp, Color(0xFF00E676), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
"تم إرسال طلب سحب $1,850.40 إلى حسابك البنكي/محفظة NEXA بنجاح!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF00E676), Color(0xFF00B0FF))
                                        )
                                    )
                                    .clickable { viewModel.withdrawCreatorEarnings() }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
 Text("طلب سحب الأرباح الفوري", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                // Locked State Progress Bar
                val progress = (totalViewsCount.toFloat() / 1_000_000f).coerceIn(0f, 1f)
                val formattedViews = String.format("%,d", totalViewsCount)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("التقدم نحو تفعيل صندوق الربح:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("$formattedViews / 1,000,000", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(NeonPurple, NeonCyan)
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
 text ="متبقي ${String.format("%,d", 1_000_000L - totalViewsCount)} مشاهدة لتفعيل أرباح قناتك تلقائياً",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Simulator Views Buttons
 Text("اختبار عدد المشاهدات :", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { viewModel.updateViewsCount(450_000L) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("450K مشاهدة", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00E676).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF00E676), RoundedCornerShape(10.dp))
                        .clickable { viewModel.updateViewsCount(1_450_000L) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
 Text("1.45M (تفعيل الربح )", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BadgeLevelRow(
    emoji: String,
    title: String,
    desc: String,
    isReached: Boolean,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isReached) accentColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f))
            .border(
                width = if (isReached) 1.dp else 0.5.dp,
                color = if (isReached) accentColor else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = if (isReached) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(desc, color = Color.LightGray, fontSize = 10.sp)
        }
        if (isReached) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
 Text("مُفعل", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
            }
        }
    }
}
