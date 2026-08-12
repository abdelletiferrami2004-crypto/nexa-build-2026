package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GooglePlayLegalModal
import com.example.ui.components.LegalTab
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundSurfaceDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

data class QuickActionItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val badgeText: String? = null,
    val isVerifiedBadge: Boolean = false,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    viewModel: MajarrahViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val isDataSaver by viewModel.isDataSaverEnabled.collectAsState()
    val isTeenMode = profile?.isTeenMode ?: true
    val isVipMember = profile?.isVipMember ?: false
    val currentLang by viewModel.selectedLanguage.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    // Dialog & Modal States
    var showVipModal by remember { mutableStateOf(false) }
    var showAccountSwitcherModal by remember { mutableStateOf(false) }
    var showNexaAccountsHubModal by remember { mutableStateOf(false) }
    var showPrivacyCheckupModal by remember { mutableStateOf(false) }
    var showProfileLockModal by remember { mutableStateOf(false) }
    var isProfileLocked by remember { mutableStateOf(false) }
    var showNotificationPrefsModal by remember { mutableStateOf(false) }
    var showBlockListModal by remember { mutableStateOf(false) }
    var showNexaVerifiedModal by remember { mutableStateOf(false) }
    var showSavedItemsModal by remember { mutableStateOf(false) }
    var showEventsModal by remember { mutableStateOf(false) }
    var showMemoriesModal by remember { mutableStateOf(false) }
    var showCommunityStandardsModal by remember { mutableStateOf(false) }
    var showLegalDocsModal by remember { mutableStateOf<LegalTab?>(null) }
    var showAiModal by remember { mutableStateOf(false) }
    var showLanguageModal by remember { mutableStateOf(false) }
    var showCreatorStudioModal by remember { mutableStateOf(false) }
    var showAudioSpacesModal by remember { mutableStateOf(false) }
    var showNearbyDiscoveryModal by remember { mutableStateOf(false) }

    // Security & Privacy settings states
    var isScreenshotProtectionEnabled by remember { mutableStateOf(true) }
    var showVoiceAssistantModal by remember { mutableStateOf(false) }
    var showPrivateVaultModal by remember { mutableStateOf(false) }

    // Enterprise Meta Suite Modals
    var showPagesAndBusinessModal by remember { mutableStateOf(false) }
    var showAdsManagerModal by remember { mutableStateOf(false) }
    var showLiveStreamModal by remember { mutableStateOf(false) }
    var showSecurityAnd2faModal by remember { mutableStateOf(false) }
    var showSupportModal by remember { mutableStateOf(false) }

    if (showPagesAndBusinessModal) {
        com.example.ui.components.NexaPagesAndBusinessModal(
            onDismiss = { showPagesAndBusinessModal = false }
        )
    }

    if (showAdsManagerModal) {
        com.example.ui.components.NexaAdsManagerModal(
            onDismiss = { showAdsManagerModal = false }
        )
    }

    if (showLiveStreamModal) {
        com.example.ui.components.NexaLiveStreamModal(
            onDismiss = { showLiveStreamModal = false }
        )
    }

    if (showSecurityAnd2faModal) {
        com.example.ui.components.NexaSecurityAnd2faModal(
            onDismiss = { showSecurityAnd2faModal = false }
        )
    }

    if (showSupportModal) {
        com.example.ui.components.NexaSupportAndHelpCenterModal(
            onDismiss = { showSupportModal = false }
        )
    }

    if (showVipModal) {
        com.example.ui.components.NexaVipSubscriptionModal(
            isCurrentlyVip = isVipMember,
            onSubscribe = { tier ->
                viewModel.activateVipSubscription(tier)
                showVipModal = false
            },
            onDismiss = { showVipModal = false }
        )
    }

    // Dark Mode Local State
    var isDarkModeEnabled by remember { mutableStateOf(true) }
    // Cache size simulated state
    var cacheSizeMb by remember { mutableStateOf(42.8f) }

    // AI Assistant Dialog Launcher
    if (showAiModal) {
        com.example.ui.components.AiAssistantModal(
            viewModel = viewModel,
            onDismiss = { showAiModal = false }
        )
    }

    // Google Play Legal Document Modal
    showLegalDocsModal?.let { tab ->
        GooglePlayLegalModal(
            initialTab = tab,
            onDismiss = { showLegalDocsModal = null }
        )
    }

    // Creator Studio & Wallet Dialog
    if (showCreatorStudioModal) {
        CreatorStudioAndWalletDialog(
            profileName = profile?.name ?: "مستخدم NEXA",
            onDismiss = { showCreatorStudioModal = false }
        )
    }

    // Audio Spaces Dialog
    if (showAudioSpacesModal) {
        AudioSpacesDialog(
            profileName = profile?.name ?: "مستخدم NEXA",
            onDismiss = { showAudioSpacesModal = false }
        )
    }

    // Nearby Local Discovery Dialog
    if (showNearbyDiscoveryModal) {
        NearbyDiscoveryDialog(
            onDismiss = { showNearbyDiscoveryModal = false },
            onNavigate = onNavigate
        )
    }

    if (showVoiceAssistantModal) {
        com.example.ui.components.NexaVoiceAssistantModal(
            onDismiss = { showVoiceAssistantModal = false },
            onNavigateToReels = {
                showVoiceAssistantModal = false
                onNavigate("reels")
            },
            onOpenVault = {
                showVoiceAssistantModal = false
                showPrivateVaultModal = true
            },
            onOpenChat = {
                showVoiceAssistantModal = false
                onNavigate("chat")
            },
            onSearchPosts = {
                showVoiceAssistantModal = false
                onNavigate("home")
            }
        )
    }

    if (showPrivateVaultModal) {
        com.example.ui.components.NexaPrivateVaultModal(
            onDismiss = { showPrivateVaultModal = false }
        )
    }

    // Modals Declarations
    if (showAccountSwitcherModal) {
        AccountSwitcherBottomSheet(
            currentUserName = profile?.name ?: "مستخدم NEXA",
            onDismiss = { showAccountSwitcherModal = false },
            onSwitchAccount = { accountName ->
                showAccountSwitcherModal = false
                viewModel.switchUserProfile(accountName)
                Toast.makeText(context, "تم التبديل إلى $accountName بنجاح", Toast.LENGTH_SHORT).show()
            },
            onAddAccount = {
                showAccountSwitcherModal = false
                viewModel.startLoginFlow()
                onNavigate("auth")
            }
        )
    }

    if (showNexaAccountsHubModal) {
        NexaAccountsHubDialog(
            profileName = profile?.name ?: "مستخدم NEXA",
            profilePhone = profile?.phone ?: "+212 600000000",
            onDismiss = { showNexaAccountsHubModal = false }
        )
    }

    if (showPrivacyCheckupModal) {
        PrivacyCheckupDialog(onDismiss = { showPrivacyCheckupModal = false })
    }

    if (showProfileLockModal) {
        ProfileLockDialog(
            isLocked = isProfileLocked,
            onToggleLock = { locked ->
                isProfileLocked = locked
                Toast.makeText(context, if (locked) "تم قفل الملف الشخصي للحماية" else "تم إلغاء قفل الملف الشخصي", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showProfileLockModal = false }
        )
    }

    if (showNotificationPrefsModal) {
        NotificationPreferencesDialog(onDismiss = { showNotificationPrefsModal = false })
    }

    if (showBlockListModal) {
        BlockListDialog(
            blockedUsers = blockedUsers,
            onUnblockUser = { name -> viewModel.unblockUser(name) },
            onDismiss = { showBlockListModal = false }
        )
    }

    if (showNexaVerifiedModal) {
        NexaVerifiedDialog(
            userName = profile?.name ?: "مستخدم NEXA",
            onDismiss = { showNexaVerifiedModal = false }
        )
    }

    if (showSavedItemsModal) {
        SavedItemsDialog(onDismiss = { showSavedItemsModal = false }, onNavigate = onNavigate)
    }

    if (showEventsModal) {
        EventsAndBirthdaysDialog(onDismiss = { showEventsModal = false })
    }

    if (showMemoriesModal) {
        MemoriesAndFeedsDialog(onDismiss = { showMemoriesModal = false })
    }

    if (showCommunityStandardsModal) {
        CommunityStandardsDialog(onDismiss = { showCommunityStandardsModal = false })
    }

    if (showLanguageModal) {
        LanguageSelectionDialog(
            currentLanguage = currentLang,
            onSelectLanguage = { lang ->
                viewModel.setAppLanguage(lang)
                showLanguageModal = false
                Toast.makeText(context, "تم تحديث اللغة بنجاح", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showLanguageModal = false }
        )
    }

    // Quick Action Grid Items definition (10 Required Action Items)
    val quickActions = listOf(
        QuickActionItem(
            title = "الرسائل المباشرة",
            description = "المحادثات الخاصة والمجموعات",
            icon = Icons.Default.Chat,
            iconColor = EncryptedGreen,
            onClick = { onNavigate("chat") }
        ),
        QuickActionItem(
            title = "إدارة الصفحات والأعمال (Meta Suite)",
            description = "إنشاء صفحات عامة، أدوار الأدمن، وتحليلات الأداء",
            icon = Icons.Default.BusinessCenter,
            iconColor = NeonPurple,
            badgeText = "Suite 💼",
            onClick = { showPagesAndBusinessModal = true }
        ),
        QuickActionItem(
            title = "مركز الإعلانات والحملات الممولة",
            description = "إطلاق إعلانات ممولة، استهداف الجمهور، والأرباح",
            icon = Icons.Default.Campaign,
            iconColor = NeonAmber,
            badgeText = "إعلانات 🎯",
            onClick = { showAdsManagerModal = true }
        ),
        QuickActionItem(
            title = "NEXA Live Stream (البث المباشر)",
            description = "بث مباشر تفاعلي، تفاعلات قلوب، ودردشة فورية",
            icon = Icons.Default.VideoLibrary,
            iconColor = NeonPink,
            badgeText = "مباشر LIVE",
            onClick = { showLiveStreamModal = true }
        ),
        QuickActionItem(
            title = "الأمان، 2FA، والأجهزة النشطة",
            description = "المصادقة الثنائية، جلسات الأجهزة، والفلترة الآلية",
            icon = Icons.Default.Shield,
            iconColor = EncryptedGreen,
            badgeText = "2FA 🛡️",
            onClick = { showSecurityAnd2faModal = true }
        ),
        QuickActionItem(
            title = "مركز المساعدة والدعم الفني",
            description = "الأسئلة الشائعة وتذاكر الدعم الفني المباشر",
            icon = Icons.Default.SupportAgent,
            iconColor = NeonCyan,
            badgeText = "دعم 🎧",
            onClick = { showSupportModal = true }
        ),
        QuickActionItem(
            title = "ريلز NEXA",
            description = "مقاطع فيديو قصيرة وتفاعلية",
            icon = Icons.Default.VideoLibrary,
            iconColor = NeonPink,
            onClick = { onNavigate("reels") }
        ),
        QuickActionItem(
            title = "المجموعات",
            description = "مجتمعات اهتمامات ونقاشات",
            icon = Icons.Default.Group,
            iconColor = TeenProtectionCyan,
            onClick = { onNavigate("friends") }
        ),
        QuickActionItem(
            title = "الأصدقاء",
            description = "طلبات الصداقة والمقترحون",
            icon = Icons.Default.People,
            iconColor = NeonCyan,
            onClick = { onNavigate("friends") }
        ),
        QuickActionItem(
            title = "المساعد الصوتي الذكي",
            description = "أوامر صوتية وتلخيص ذكي للمحتوى",
            icon = Icons.Default.Mic,
            iconColor = NeonCyan,
            badgeText = "صوتي",
            onClick = { showVoiceAssistantModal = true }
        ),
        QuickActionItem(
            title = "الخزنة السرية المشفرة",
            description = "حماية المحادثات والمنشورات ببصمة الجهاز",
            icon = Icons.Default.Lock,
            iconColor = EncryptedGreen,
            badgeText = "مشفر 🔐",
            onClick = { showPrivateVaultModal = true }
        ),
        QuickActionItem(
            title = "NEXA AI",
            description = "المساعد الذكي للإنتاجية",
            icon = Icons.Default.AutoAwesome,
            iconColor = NeonPurple,
            badgeText = "ذكاء آلي",
            onClick = { showAiModal = true }
        ),
        QuickActionItem(
            title = "العناصر المحفوظة",
            description = "المنشورات والمنتجات المفضلة",
            icon = Icons.Default.Bookmark,
            iconColor = NeonAmber,
            onClick = { showSavedItemsModal = true }
        ),
        QuickActionItem(
            title = "المناسبات والأعياد",
            description = "أعياد الميلاد والأحداث القادمة",
            icon = Icons.Default.Cake,
            iconColor = NeonPink,
            onClick = { showEventsModal = true }
        ),
        QuickActionItem(
            title = "NEXA Verified",
            description = "شارات التوثيق والحماية المتقدمة",
            icon = Icons.Default.Verified,
            iconColor = NeonCyan,
            isVerifiedBadge = true,
            onClick = { showNexaVerifiedModal = true }
        ),
        QuickActionItem(
            title = "المتجر / Marketplace",
            description = "عروض المنتجات الرقمية والتسوق",
            icon = Icons.Default.ShoppingBag,
            iconColor = EncryptedGreen,
            onClick = { onNavigate("store") }
        ),
        QuickActionItem(
            title = "استوديو صناع المحتوى والمحفظة",
            description = "تحليلات الأداء والأرباح المالية",
            icon = Icons.Default.AccountBalanceWallet,
            iconColor = NeonAmber,
            badgeText = "أرباح $",
            onClick = { showCreatorStudioModal = true }
        ),
        QuickActionItem(
            title = "الغرف الصوتية المباشرة",
            description = "بث صوتي مباشر ونقاشات تفاعلية",
            icon = Icons.Default.Mic,
            iconColor = NeonPink,
            badgeText = "مباشر LIVE",
            onClick = { showAudioSpacesModal = true }
        ),
        QuickActionItem(
            title = "رادار اكتشاف القريب Radar",
            description = "محتوى محلي وصناع من حولك",
            icon = Icons.Default.NearMe,
            iconColor = TeenProtectionCyan,
            badgeText = "الرادار 🛰️",
            onClick = { showNearbyDiscoveryModal = true }
        ),
        QuickActionItem(
            title = "الذكريات والمواجز",
            description = "منشوراتك القديمة في مثل هذا اليوم",
            icon = Icons.Default.AutoStories,
            iconColor = TeenProtectionCyan,
            onClick = { showMemoriesModal = true }
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Row (NEXA Header Bar)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "القائمة الرئيسية والإعدادات",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "مركز التحكم الشامل لبيئة NEXA الذكية",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { showNexaAccountsHubModal = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "Accounts Hub", tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { showAccountSwitcherModal = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SwitchAccount, contentDescription = "Switch Account", tint = NeonPurple, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Section 1: User Profile Card & Account Switcher Trigger
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigate("profile") }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(NeonPurple.copy(alpha = 0.6f), NeonCyan.copy(alpha = 0.3f))
                                        )
                                    )
                                    .border(2.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile?.name?.take(1) ?: "م",
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile?.name ?: "مستخدم NEXA",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Coins / Wallet Badge relocated cleanly inside User Profile
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NeonAmber.copy(alpha = 0.2f))
                                        .border(1.dp, NeonAmber, RoundedCornerShape(10.dp))
                                        .clickable { showCreatorStudioModal = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = "Wallet Balance",
                                            tint = NeonAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "850 عملة NEXA",
                                            color = NeonAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Switcher button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonPurple.copy(alpha = 0.2f))
                                .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { showAccountSwitcherModal = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تبديل الحسابات", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Quick Action Grid (القائمة الرئيسية)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "القائمة الرئيسية والخدمات السريعة",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 2-Column Grid Layout for 10 Quick Actions
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in quickActions.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionCard(
                                item = quickActions[i],
                                modifier = Modifier.weight(1f)
                            )
                            if (i + 1 < quickActions.size) {
                                QuickActionCard(
                                    item = quickActions[i + 1],
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Accounts Hub Banner ("مركز حسابات NEXA")
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNexaAccountsHubModal = true },
                shape = RoundedCornerShape(20.dp),
                borderColor = NeonCyan
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E1035), Color(0xFF0F1E36))
                            )
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = "Accounts Hub", tint = NeonCyan, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("مركز حسابات NEXA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                GlassBadge(text = "حماية فائقة", accentColor = EncryptedGreen)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("إدارة كلمة السر، الأمان، البصمة، والحسابات المرتبطة", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }

                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.LightGray)
                }
            }
        }

        // Section 4: Privacy Controls (التحكم في الخصوصية)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "الإعدادات وعناصر التحكم في الخصوصية",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsListTile(
                            title = "التحقق من الخصوصية",
                            subtitle = "جولة إرشادية لفحص وتأمين إعدادات مشاركة بياناتك",
                            icon = Icons.Default.Shield,
                            iconColor = TeenProtectionCyan,
                            onClick = { showPrivacyCheckupModal = true }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "قفل الملف الشخصي",
                            subtitle = if (isProfileLocked) "مفعل: ملفك الشخصي محمي من الغرباء" else "معطل: ملفك مفتوح للمتابعين",
                            icon = Icons.Default.Lock,
                            iconColor = EncryptedGreen,
                            trailingContent = {
                                Switch(
                                    checked = isProfileLocked,
                                    onCheckedChange = { locked ->
                                        isProfileLocked = locked
                                        Toast.makeText(context, if (locked) "تم قفل الملف الشخصي" else "تم إلغاء قفل الملف الشخصي", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = EncryptedGreen)
                                )
                            },
                            onClick = { showProfileLockModal = true }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "تفضيلات التفاعلات والإشعارات",
                            subtitle = "التحكم في الإشعارات الفورية والصوت وأعداد التفاعلات",
                            icon = Icons.Default.Notifications,
                            iconColor = NeonAmber,
                            onClick = { showNotificationPrefsModal = true }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "قائمة المحظورين",
                            subtitle = "إدارة الأشخاص والحسابات التي قمت بحظرها",
                            icon = Icons.Default.Block,
                            iconColor = Color.Red,
                            onClick = { showBlockListModal = true }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "حماية المحادثات من لقطات الشاشة",
                            subtitle = if (isScreenshotProtectionEnabled) "مفعل: منع لقطات الشاشة وإرسال إشعار فوري عند المحاولة" else "معطل: السماح بتصوير الشاشة",
                            icon = Icons.Default.PhonelinkLock,
                            iconColor = EncryptedGreen,
                            trailingContent = {
                                Switch(
                                    checked = isScreenshotProtectionEnabled,
                                    onCheckedChange = { enabled ->
                                        isScreenshotProtectionEnabled = enabled
                                        Toast.makeText(
                                            context,
                                            if (enabled) "تم تفعيل حماية لقطات الشاشة والتنبيه الفوري" else "تم إيقاف حماية لقطات الشاشة",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = EncryptedGreen)
                                )
                            },
                            onClick = {
                                isScreenshotProtectionEnabled = !isScreenshotProtectionEnabled
                                Toast.makeText(
                                    context,
                                    if (isScreenshotProtectionEnabled) "تم تفعيل حماية لقطات الشاشة والتنبيه الفوري" else "تم إيقاف حماية لقطات الشاشة",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "اكتشاف المحتوى المحلي القريب",
                            subtitle = "البحث عن صناع المحتوى والفاعليات في محيطك الجغرافي",
                            icon = Icons.Default.NearMe,
                            iconColor = TeenProtectionCyan,
                            onClick = { showNearbyDiscoveryModal = true }
                        )
                    }
                }
            }
        }

        // Section 5: Preferences & App Performance (التفضيلات وأداء التطبيق)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "التفضيلات وأداء التطبيق",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsListTile(
                            title = "الوضع المظلم (Dark Mode)",
                            subtitle = if (isDarkModeEnabled) "مفعل تلقائياً للحفاظ على سلامة العين والشاشة" else "الوضع الفاتح مفعل",
                            icon = Icons.Default.DarkMode,
                            iconColor = NeonPurple,
                            trailingContent = {
                                Switch(
                                    checked = isDarkModeEnabled,
                                    onCheckedChange = { isDarkModeEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = NeonPurple)
                                )
                            },
                            onClick = { isDarkModeEnabled = !isDarkModeEnabled }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "توفير بيانات الاتصال (*6 Pass)",
                            subtitle = if (isDataSaver) "مفعل: ضغط الصور وإيقاف الفيديو التلقائي" else "معطل: تحميل كامل الجودة HD",
                            icon = Icons.Default.DataSaverOn,
                            iconColor = NeonCyan,
                            trailingContent = {
                                Switch(
                                    checked = isDataSaver,
                                    onCheckedChange = { viewModel.toggleDataSaver(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = NeonCyan)
                                )
                            },
                            onClick = { viewModel.toggleDataSaver() }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "مسح المساحة / التخزين المؤقت",
                            subtitle = "حجم التخزين المؤقت حالياً: ${String.format("%.1f", cacheSizeMb)} ميجابايت",
                            icon = Icons.Default.CleaningServices,
                            iconColor = NeonAmber,
                            onClick = {
                                cacheSizeMb = 0.0f
                                Toast.makeText(context, "تم مسح التخزين المؤقت بنجاح (0.0 MB)", Toast.LENGTH_SHORT).show()
                            }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "اللغة والمنطقة",
                            subtitle = "اللغة الحالية: ${if (currentLang == AppLanguage.ARABIC || currentLang == AppLanguage.AUTO) "العربية (المغرب / الشرق الأوسط)" else currentLang.name}",
                            icon = Icons.Default.Language,
                            iconColor = TeenProtectionCyan,
                            onClick = { showLanguageModal = true }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "نظام استراحة الشاشة للناشئة والأشبال",
                            subtitle = if (isTeenMode) "مفعل: تنبيه أوتوماتيكي واستراحة كل ساعتين" else "معطل: تصفح عادي للبالغين",
                            icon = Icons.Default.Shield,
                            iconColor = TeenProtectionCyan,
                            trailingContent = {
                                Switch(
                                    checked = isTeenMode,
                                    onCheckedChange = { viewModel.toggleTeenMode(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = TeenProtectionCyan)
                                )
                            },
                            onClick = { viewModel.toggleTeenMode(!isTeenMode) }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "إعلانات AdMob وعضوية VIP بدون إعلانات",
                            subtitle = if (isVipMember) "عضوية VIP نشطة: تصفح بدون إعلانات ✨" else "انقر للتجديد أو مشاهدة إعلان مكافأة",
                            icon = Icons.Default.Star,
                            iconColor = NeonPink,
                            onClick = { showVipModal = true }
                        )
                    }
                }
            }
        }

        // Section 6: Legal & Play Store Policy Compliance (الامتثال والسياسات)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "السياسات والامتيثال القانوني (Google Play 2026)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsListTile(
                            title = "معايير مجتمع NEXA",
                            subtitle = "القواعد والإرشادات الحاكمة للمحتوى والسلامة",
                            icon = Icons.Default.Gavel,
                            iconColor = NeonCyan,
                            onClick = { showCommunityStandardsModal = true }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "شروط الخدمة والاستخدام",
                            subtitle = "الاتفاقية القانونية وحقوق المستخدم",
                            icon = Icons.Default.Shield,
                            iconColor = TeenProtectionCyan,
                            onClick = { showLegalDocsModal = LegalTab.TERMS_OF_SERVICE }
                        )

                        SettingsDivider()

                        SettingsListTile(
                            title = "سياسة الخصوصية وحماية البيانات",
                            subtitle = "كيف نحمي بياناتك وحقوقك البيومترية وفق المعايير الدولية",
                            icon = Icons.Default.PrivacyTip,
                            iconColor = EncryptedGreen,
                            onClick = { showLegalDocsModal = LegalTab.PRIVACY_POLICY }
                        )
                    }
                }
            }
        }

        // Copyright Branding Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NEXA Ecosystem 2026",
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Brand",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "جميع الحقوق محفوظة منصة NEXA الاجتماعية الذكية",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// Sub-component: Quick Action Grid Card
@Composable
fun QuickActionCard(
    item: QuickActionItem,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .height(110.dp)
            .clickable { item.onClick() },
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(item.iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (item.badgeText != null) {
                    GlassBadge(text = item.badgeText, accentColor = item.iconColor)
                } else if (item.isVerifiedBadge) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Badge",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.description,
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Helper: ListTile for Settings
@Composable
fun SettingsListTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = Color.LightGray, fontSize = 11.sp)
            }
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.06f))
    )
}

// Modal 1: Account Switcher Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSwitcherBottomSheet(
    currentUserName: String,
    onDismiss: () -> Unit,
    onSwitchAccount: (String) -> Unit,
    onAddAccount: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundSurfaceDark,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
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
                Text(
                    text = "تبديل الحسابات",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account 1: Active
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchAccount(currentUserName) },
                shape = RoundedCornerShape(16.dp),
                borderColor = NeonCyan
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentUserName.take(1), color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentUserName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            }
                            Text("الحساب النشط حالياً", color = NeonCyan, fontSize = 11.sp)
                        }
                    }

                    Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Account 2: Business / Secondary
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchAccount("NEXA Business - مجرة للأعمال") },
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("أ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("NEXA Business - مجرة للأعمال", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("حساب تجاري • 3 إشعارات جديدة", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }

                    GlassBadge(text = "3 جديد", accentColor = NeonPink)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Add new account button
            Button(
                onClick = onAddAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل الدخول إلى حساب آخر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Modal 2: NEXA Accounts Hub Dialog ("مركز حسابات NEXA")
@Composable
fun NexaAccountsHubDialog(
    profileName: String,
    profilePhone: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) }
    var isBiometricActive by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.5.dp, NeonCyan, RoundedCornerShape(26.dp)),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مركز حسابات NEXA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("كلمة السر والأمان", "التفاصيل الشخصية", "البصمة والمركبات")
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeTab == index) NeonCyan else Color.Transparent)
                                .clickable { activeTab = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (activeTab == index) BackgroundDark else Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeTab) {
                        0 -> {
                            // Passwords & Security
                            Text("إدارة الأمان وكلمات السر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("تغيير كلمة السر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("آخر تغيير: منذ 14 يوماً", color = Color.Gray, fontSize = 11.sp)
                                }
                            }

                            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("المصادقة الثنائية (2FA)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("مفعلة عبر رمز PIN المباشر وتأكيد الرسائل", color = EncryptedGreen, fontSize = 11.sp)
                                }
                            }
                        }
                        1 -> {
                            // Personal Details
                            Text("التفاصيل الشخصية والحساب", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("اسم الحساب المسجل:", color = Color.Gray, fontSize = 11.sp)
                                    Text(profileName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("رقم الهاتف:", color = Color.Gray, fontSize = 11.sp)
                                    Text(profilePhone, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                        else -> {
                            // Biometric & Linked
                            Text("قفل البصمة البيومترية والحسابات المرتبطة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("قفل بصمة الإصبع الرسمية", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("فتح الحساب عبر المستشعر البيومتري للجهاز", color = Color.Gray, fontSize = 11.sp)
                                        }
                                    }

                                    Switch(
                                        checked = isBiometricActive,
                                        onCheckedChange = {
                                            isBiometricActive = it
                                            Toast.makeText(context, if (it) "تم تفعيل القفل البيومتري" else "تم تعطيل القفل البيومتري", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = NeonPurple)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("حفظ وإغلاق", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Modal 3: Privacy Checkup Dialog
@Composable
fun PrivacyCheckupDialog(onDismiss: () -> Unit) {
    var step by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = TeenProtectionCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("التحقق من الخصوصية (خطوة $step من 3)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                when (step) {
                    1 -> {
                        Text("الخطوة 1: جمهور منشوراتك وقصصك", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("حالياً منشوراتك ورسائلك مرئية للأصدقاء فقط في بيئة NEXA المشفرة.", color = Color.LightGray, fontSize = 12.sp)
                    }
                    2 -> {
                        Text("الخطوة 2: حماية الحساب وكلمة السر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("حسابك محمي بنظام كشف التهديدات البيومتري المباشر مع تشفير 256-bit.", color = Color.LightGray, fontSize = 12.sp)
                    }
                    else -> {
                        Text("الخطوة 3: إعدادات البيانات المخصصة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("تكتمل الخصوصية بضبط أذونات الوصول للقصص والموقع الجغرافي.", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (step < 3) step++ else onDismiss()
                }
            ) {
                Text(if (step < 3) "الخطوة التالية" else "إتمام الفحص بنجاح", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// Modal 4: Profile Lock Dialog
@Composable
fun ProfileLockDialog(
    isLocked: Boolean,
    onToggleLock: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("قفل الملف الشخصي", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "عند قفل ملفك الشخصي، سيتمكن الأصدقاء فقط من رؤية صورك، منشوراتك، وقصصك بالحجم الكامل.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onToggleLock(!isLocked)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isLocked) Color.Red else EncryptedGreen)
            ) {
                Text(if (isLocked) "إلغاء قفل الملف" else "تفعيل قفل الملف الآن", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = Color.LightGray) }
        }
    )
}

// Modal 5: Notification Preferences Dialog
@Composable
fun NotificationPreferencesDialog(onDismiss: () -> Unit) {
    var isSoundsOn by remember { mutableStateOf(true) }
    var isReactionsCountVisible by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تفضيلات التفاعلات والإشعارات", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("أصوات الإشعارات الفورية", color = Color.White, fontSize = 13.sp)
                    Switch(checked = isSoundsOn, onCheckedChange = { isSoundsOn = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("إظهار عدد التفاعلات على المنشورات", color = Color.White, fontSize = 13.sp)
                    Switch(checked = isReactionsCountVisible, onCheckedChange = { isReactionsCountVisible = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("تم", color = NeonCyan, fontWeight = FontWeight.Bold) }
        }
    )
}

// Modal 6: Block List Dialog
@Composable
fun BlockListDialog(
    blockedUsers: Set<String>,
    onUnblockUser: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val displayList = remember(blockedUsers) {
        if (blockedUsers.isEmpty()) listOf("حساب تجريبي محظور 1", "حساب غير موثق 2") else blockedUsers.toList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Block, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("قائمة المحظورين", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (displayList.isEmpty()) {
                    Text("لا يوجد أي حساب في قائمة المحظورين حالياً.", color = Color.LightGray, fontSize = 12.sp)
                } else {
                    displayList.forEach { name ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, color = Color.White, fontSize = 13.sp)
                            TextButton(
                                onClick = {
                                    onUnblockUser(name)
                                }
                            ) {
                                Text("إلغاء الحظر", color = NeonCyan, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق", color = Color.LightGray) }
        }
    )
}

// Modal 7: NEXA Verified Dialog
@Composable
fun NexaVerifiedDialog(
    userName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("NEXA Verified", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "شارة التوثيق الزرقاء الموثوقة لحساب $userName. توفر حماية فائقة ضد الانتحال ودعم فني مباشر أولوي.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                GlassBadge(text = "الحساب موثق رسمياً بالشارة الزرقاء", accentColor = NeonCyan)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "حسابك موثق ونشط بامتيازات NEXA Verified", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("إدارة التوثيق", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// Modal 8: Saved Items Dialog
@Composable
fun SavedItemsDialog(onDismiss: () -> Unit, onNavigate: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("العناصر المحفوظة", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("منشور تقني: تحديثات الذكاء الاصطناعي NEXA 2026", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("منتج محفوظ: سماعات نيون اللاسلكية المشفرة", color = Color.LightGray, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("تم", color = NeonCyan) }
        }
    )
}

// Modal 9: Events & Birthdays Dialog
@Composable
fun EventsAndBirthdaysDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Cake, contentDescription = null, tint = NeonPink, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("المناسبات وأعياد الميلاد", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("عيد ميلاد صديقك 'ياسين' اليوم!", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("حدث قادم: ملتقى مطوري NEXA للشباب المغربي يوم السبت القادم.", color = Color.LightGray, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إرسال تهنئة", color = NeonPink, fontWeight = FontWeight.Bold) }
        }
    )
}

// Modal 10: Memories Dialog
@Composable
fun MemoriesAndFeedsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoStories, contentDescription = null, tint = TeenProtectionCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("الذكريات والمواجز", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("في مثل هذا اليوم منذ سنة واحدة:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("نشرت صورة تفاعلية في مجتمع NEXA وحصدت 142 تفاعلاً.", color = Color.LightGray, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("مشاركة في الستوري", color = TeenProtectionCyan, fontWeight = FontWeight.Bold) }
        }
    )
}

// Modal 11: Community Standards Dialog
@Composable
fun CommunityStandardsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Gavel, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("معايير مجتمع NEXA", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("1. الاحترام والمودة التامة بين كافة أعضاء المجتمعات.", color = Color.White, fontSize = 12.sp)
                Text("2. حظر خطاب الكراهية، الاحتيال، أو نشر البيانات الزائفة.", color = Color.White, fontSize = 12.sp)
                Text("3. حماية خاصة ومشددة لفئة الناشئة والأطفال أقل من 18 سنة.", color = Color.White, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("موافق ومفهوم", color = NeonCyan, fontWeight = FontWeight.Bold) }
        }
    )
}

// Modal 12: Language Selection Dialog
@Composable
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, tint = TeenProtectionCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("اختر لغة التطبيق والمنطقة", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(AppLanguage.ARABIC) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("العربية (المغرب والشرق الأوسط)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (currentLanguage == AppLanguage.ARABIC || currentLanguage == AppLanguage.AUTO) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeenProtectionCyan)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(AppLanguage.ENGLISH) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("English (International)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (currentLanguage == AppLanguage.ENGLISH) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeenProtectionCyan)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(AppLanguage.FRENCH) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Français (Maroc)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (currentLanguage == AppLanguage.FRENCH) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TeenProtectionCyan)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = Color.LightGray) }
        }
    )
}

// Modal 13: Creator Studio & Wallet Dialog
@Composable
fun CreatorStudioAndWalletDialog(
    profileName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var currentBalance by remember { mutableStateOf(2845.50) }
    var pendingBalance by remember { mutableStateOf(620.00) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.5.dp, NeonAmber, RoundedCornerShape(26.dp)),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
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
                                .background(NeonAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("استوديو صناع المحتوى والمحفظة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("تحليلات الأداء المباشر وإدارة أرباح الفيديو", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) NeonAmber else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Analytics,
                                contentDescription = null,
                                tint = if (selectedTab == 0) BackgroundDark else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "لوحة التحليلات",
                                color = if (selectedTab == 0) BackgroundDark else Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) NeonAmber else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = if (selectedTab == 1) BackgroundDark else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "المحفظة والسحب",
                                color = if (selectedTab == 1) BackgroundDark else Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (selectedTab == 0) {
                        // Analytics View
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("المشاهدات", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("1,428,900", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("+18.4% هذا الشهر", color = EncryptedGreen, fontSize = 10.sp)
                                }
                            }

                            GlassCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.BarChart, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("الانطباعات", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("4,850,200", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("معدل الظهور 92%", color = NeonCyan, fontSize = 10.sp)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("معدل التفاعل", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("8.4%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("أعلى من المتوسط", color = NeonAmber, fontSize = 10.sp)
                                }
                            }

                            GlassCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ساعات المشاهدة", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("342.1K", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("ساعة مشاهدة كليّة", color = NeonPink, fontSize = 10.sp)
                                }
                            }
                        }

                        Text("أفضل المقاطع أداءً والأرباح", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        val topVideos = listOf(
                            Triple("تحديثات الذكاء الاصطناعي NEXA 2026", "850K مشاهدة • 12.4K إعجاب", "$1,250.00"),
                            Triple("تقنية البصمة البيومترية والمحفظة", "340K مشاهدة • 5.1K إعجاب", "$420.00"),
                            Triple("الغرف الصوتية المباشرة والمجتمعات", "238K مشاهدة • 3.8K إعجاب", "$280.00")
                        )

                        topVideos.forEach { (title, subtitle, earn) ->
                            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(NeonPink.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(subtitle, color = Color.LightGray, fontSize = 11.sp)
                                        }
                                    }
                                    Text(earn, color = EncryptedGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        // Wallet & Monetization View
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            borderColor = NeonAmber
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF2A1D08), Color(0xFF140D02))
                                        )
                                    )
                                    .padding(18.dp)
                            ) {
                                Text("الرصيد المتاح للسحب المالي", color = Color.LightGray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "$${String.format("%.2f", currentBalance)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 28.sp
                                    )
                                    GlassBadge(text = "مؤكد للتحويل", accentColor = EncryptedGreen)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("أرباح قيد التصفية (تحت المراجعة):", color = Color.Gray, fontSize = 11.sp)
                                    Text("$${String.format("%.2f", pendingBalance)}", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (currentBalance > 0) {
                                            Toast.makeText(context, "تم تقديم طلب سحب مبلغ $${String.format("%.2f", currentBalance)} بنجاح إلى حسابك البنكي", Toast.LENGTH_LONG).show()
                                            currentBalance = 0.0
                                        } else {
                                            Toast.makeText(context, "لا يوجد رصيد متاح حالياً للسحب", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("طلب سحب الأرباح الفوري (NEXA Pay / البنك)", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("الحساب البنكي المرتبط", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("BMCE / Attijariwafa Bank ••••• 4892", color = Color.LightGray, fontSize = 11.sp)
                                    }
                                }
                                Icon(Icons.Default.Verified, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }

                        Text("سجل المعاملات المالية للأرباح", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        val transactions = listOf(
                            Triple("عائدات إعلانات ريلز - يوليو 2026", "28 يوليو 2026 • تلقائي", "+$1,250.00"),
                            Triple("هدايا وشارات المتابعين المباشرة", "25 يوليو 2026 • دعم خارجي", "+$345.50"),
                            Triple("تحويل أرباح سابق لحساب بنكي", "15 يوليو 2026 • اكتمل", "-$1,000.00")
                        )

                        transactions.forEach { (title, subtitle, amount) ->
                            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(subtitle, color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Text(
                                        amount,
                                        color = if (amount.startsWith("+")) EncryptedGreen else Color.LightGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Modal 14: Audio Spaces Dialog
@Composable
fun AudioSpacesDialog(
    profileName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isInsideRoom by remember { mutableStateOf(false) }
    var activeRoomTitle by remember { mutableStateOf("") }
    var isMicMuted by remember { mutableStateOf(false) }
    var isHandRaised by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.5.dp, NeonPink, RoundedCornerShape(26.dp)),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
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
                                .background(NeonPink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = NeonPink, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("الغرف الصوتية المباشرة - Audio Spaces", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("نقاشات وبث صوتي تفاعلي مع المجتمع", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isInsideRoom) {
                    Button(
                        onClick = {
                            activeRoomTitle = "🚀 غرفة $profileName - مستقبل الذكاء الاصطناعي والتكنولوجيا"
                            isInsideRoom = true
                            Toast.makeText(context, "تم إنشاء الغرفة الصوتية المباشرة بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إنشاء غرفة صوتية جديدة +", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("الغرف الصوتية النشطة الآن 🔴", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val activeRooms = listOf(
                            Triple("🚀 المستقبل والتطوير في NEXA AI 2026", "المستضيف: عبد اللطيف • 450 مستمع • 4 متحدثين", "تقنية 🔴"),
                            Triple("🎙️ مجتمع صناع المحتوى وصناع الريلز", "المستضيف: سارة التازي • 1,200 مستمع • 8 متحدثين", "صنّاع 🔴"),
                            Triple("💼 ريادة الأعمال والاستثمار التكنولوجي", "المستضيف: كريم العلمي • 820 مستمع • 6 متحدثين", "أعمال 🔴")
                        )

                        activeRooms.forEach { (title, subtitle, badge) ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeRoomTitle = title
                                        isInsideRoom = true
                                        Toast.makeText(context, "انضممت إلى الغرفة الصوتية", Toast.LENGTH_SHORT).show()
                                    },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        GlassBadge(text = badge, accentColor = NeonPink)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(subtitle, color = Color.LightGray, fontSize = 11.sp)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NeonCyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("انضمام للغرفة 🎧", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Active Inside Room View
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            borderColor = NeonPink
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(activeRoomTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    GlassBadge(text = "مباشر LIVE 🔴", accentColor = Color.Red)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("المستضيف الرئيسي: $profileName • 512 مستمع متواجد", color = Color.LightGray, fontSize = 11.sp)
                            }
                        }

                        Text("المتحدثون في الغرفة 🎙️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val speakers = listOf(
                                Pair(profileName, true),
                                Pair("سارة التازي", false)
                            )

                            speakers.forEach { (name, isSpeaking) ->
                                GlassCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(NeonPurple.copy(alpha = 0.3f))
                                                .border(2.dp, if (isSpeaking) NeonPink else Color.Gray, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                        Text(if (isSpeaking) "يتحدث الآن 🔊" else "صامت 🔇", color = if (isSpeaking) NeonPink else Color.Gray, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val speakers = listOf(
                                Pair("أحمد بناني", true),
                                Pair("كريم العلمي", false)
                            )

                            speakers.forEach { (name, isSpeaking) ->
                                GlassCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(NeonCyan.copy(alpha = 0.3f))
                                                .border(2.dp, if (isSpeaking) NeonPink else Color.Gray, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                        Text(if (isSpeaking) "يتحدث الآن 🔊" else "صامت 🔇", color = if (isSpeaking) NeonPink else Color.Gray, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        Text("محادثة المستمعين المباشرة 💬", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("علي العايدي: موضوع رائع جداً، شكراً لكم!", color = Color.LightGray, fontSize = 11.sp)
                                Text("إيمان التازي: هل سيتم تسجيل هذه الجلسة؟", color = Color.LightGray, fontSize = 11.sp)
                                Text("يوسف العلمي: أسئلة ممتازة في الذكاء الاصطناعي 👏", color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isMicMuted) Color.Red.copy(alpha = 0.3f) else EncryptedGreen.copy(alpha = 0.3f))
                                .clickable {
                                    isMicMuted = !isMicMuted
                                    Toast.makeText(context, if (isMicMuted) "تم كتم الميكروفون" else "تم تشغيل الميكروفون", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isMicMuted) "كتم" else "مباشر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isHandRaised) NeonAmber else Color.White.copy(alpha = 0.1f))
                                .clickable {
                                    isHandRaised = !isHandRaised
                                    Toast.makeText(context, if (isHandRaised) "تم إرسال طلب الحديث للمستضيف ✋" else "تم إلغاء طلب الحديث", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("طلب الحديث ✋", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                isInsideRoom = false
                                Toast.makeText(context, "غادرت الغرفة الصوتية", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Text("مغادرة 🚪", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// Modal 15: Nearby Discovery Dialog
@Composable
fun NearbyDiscoveryDialog(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var distanceKm by remember { mutableStateOf(10f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.5.dp, TeenProtectionCyan, RoundedCornerShape(26.dp)),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
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
                                .background(TeenProtectionCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.NearMe, contentDescription = null, tint = TeenProtectionCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("رادار اكتشاف القريب - Nearby Radar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("استكشاف المحتوى وصناع المحتوى والفعاليات في نطاقك", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("نطاق المسح الجغرافي للرادار", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            GlassBadge(text = "${distanceKm.toInt()} كم", accentColor = TeenProtectionCyan)
                        }

                        Slider(
                            value = distanceKm,
                            onValueChange = { distanceKm = it },
                            valueRange = 1f..50f,
                            colors = SliderDefaults.colors(
                                thumbColor = TeenProtectionCyan,
                                activeTrackColor = TeenProtectionCyan,
                                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(TeenProtectionCyan.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                        .border(1.dp, TeenProtectionCyan.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Radar, contentDescription = null, tint = TeenProtectionCyan, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("الرادار نشط • جاري مسح المحيط الجغرافي (${distanceKm.toInt()} كم)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("صناع المحتوى والفعاليات القريبة منك", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val nearbyItems = listOf(
                        Triple("ياسين الإدريسي (صانع محتوى)", "على بعد 1.2 كم • بث مباشر من الدار البيضاء 🔴", "متابعة"),
                        Triple("إلهام الشرايبي (مطبخ وتصميم)", "على بعد 3.5 كم • منشور جديد: معرض التكنولوجيا", "تواصل"),
                        Triple("ملتقى NEXA المحلي للتكنولوجيا", "على بعد 5.0 كم • اليوم الساعة 6:00 مساءً", "انضمام")
                    )

                    nearbyItems.forEach { (title, subtitle, action) ->
                        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(TeenProtectionCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.NearMe, contentDescription = null, tint = TeenProtectionCyan, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(subtitle, color = Color.LightGray, fontSize = 11.sp)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonPurple.copy(alpha = 0.3f))
                                        .clickable {
                                            Toast.makeText(context, "تم تنفيذ: $action", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(action, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "تم إعادة مسح الرادار الجغرافي بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TeenProtectionCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Radar, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إعادة مسح الرادار الجغرافي 🛰️", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
