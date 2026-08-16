package com.example

import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.LoginStep
import com.example.ui.MajarrahViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.screens.SocialScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MajarrahTheme

class MainActivity : FragmentActivity() {

    private val viewModel: MajarrahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            com.example.util.NotificationSoundManager.init(this)
            com.example.util.NexaNotificationManager.initNotificationChannels(this)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    androidx.core.app.ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        101
                    )
                }
            }
        } catch (e: Throwable) {
            android.util.Log.e("MainActivity", "Init exception caught", e)
        }
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MajarrahTheme(darkTheme = isDarkTheme) {
                // Mandatory RTL Layout Direction for Arabic Interface
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MajarrahApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MajarrahApp(viewModel: MajarrahViewModel) {
    val loginStep by viewModel.loginStep.collectAsState()
    val isAnomalyDetected by viewModel.isAnomalyDetected.collectAsState()
    val anomalyReason by viewModel.anomalyReason.collectAsState()
    val anomalyCountdownSeconds by viewModel.anomalyCountdownSeconds.collectAsState()
    val anomalyAuthErrorMessage by viewModel.anomalyAuthErrorMessage.collectAsState()

    val isAppBanned by viewModel.isAppBanned.collectAsState()
    val appBanMessage by viewModel.appBanMessage.collectAsState()

    val isAppLocked by viewModel.isAppLocked.collectAsState()

    val isBreakWarningActive by viewModel.isBreakWarningActive.collectAsState()
    val gracePeriodRemainingSeconds by viewModel.gracePeriodRemainingSeconds.collectAsState()
    val isReEntryWarningActive by viewModel.isReEntryWarningActive.collectAsState()
    val reEntryGraceRemainingSeconds by viewModel.reEntryGraceRemainingSeconds.collectAsState()
    val isScreenTimeLocked by viewModel.isScreenTimeLocked.collectAsState()
    val lockRemainingSeconds by viewModel.lockRemainingSeconds.collectAsState()
    val isCoolingAppExitTriggered by viewModel.isCoolingAppExitTriggered.collectAsState()
    val activeScreenTimeSeconds by viewModel.activeScreenTimeSeconds.collectAsState()

    var currentRoute by remember { mutableStateOf("home") }
    var isChatActive by remember { mutableStateOf(false) }
    var showGlobalStoryCreatorModal by remember { mutableStateOf(false) }

    // Multi-Permission Runtime Requester for Microphone, Camera, Storage, and Notifications
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissionsList = mutableListOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(android.Manifest.permission.POST_NOTIFICATIONS)
            permissionsList.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionsLauncher.launch(permissionsList.toTypedArray())
    }

    if (showGlobalStoryCreatorModal) {
        com.example.ui.components.StoryCreatorModal(
            onDismiss = { showGlobalStoryCreatorModal = false },
            onPublishStory = { story ->
                viewModel.publishStory(story)
                showGlobalStoryCreatorModal = false
            }
        )
    }

    // App Lock with Biometrics
    if (isAppLocked) {
        com.example.ui.components.AppBiometricLockScreen(
            onUnlockSuccess = { viewModel.unlockAppWithBiometricsOrPin() }
        )
        return
    }

    // Smart Screen Time 3-Hour Strict Lock View (under_18_mode = true)
    if (isScreenTimeLocked) {
        com.example.ui.components.Under18ScreenTimeLockScreen(
            remainingSeconds = lockRemainingSeconds,
            onTestUnlock = { viewModel.unlockScreenTimeForTest() }
        )
        return
    }

    // Gentle 2-3 Hours Screen Time Break Modal
    if (isBreakWarningActive) {
        val hoursText = "${activeScreenTimeSeconds / 3600} ساعات و ${(activeScreenTimeSeconds % 3600) / 60} دقائق"
        com.example.ui.components.SmartScreenTimeBreakModal(
            activeHoursText = hoursText,
            gracePeriodSeconds = gracePeriodRemainingSeconds,
            onAcceptBreak = { viewModel.triggerCoolingExitAndLock() },
            onSnoozeGrace = { viewModel.dismissBreakWarningForNow() },
            onTestUnlock = { viewModel.unlockScreenTimeForTest() }
        )
    }

    // Early Re-Entry Warning Modal
    if (isReEntryWarningActive) {
        com.example.ui.components.SmartReEntryWarningModal(
            reEntryGraceSeconds = reEntryGraceRemainingSeconds,
            onExitNow = { viewModel.triggerCoolingExitAndLock() },
            onIgnoreTriggerLock = { viewModel.triggerStrict3HourLock() },
            onTestUnlock = { viewModel.unlockScreenTimeForTest() }
        )
    }

    // Cooling Exit Apology Modal before force exit
    if (isCoolingAppExitTriggered) {
        com.example.ui.components.CoolingExitApologyModal(
            onConfirmClose = { viewModel.triggerStrict3HourLock() }
        )
    }

    // AI Temporary App Suspension / Ban Modal
    if (isAppBanned) {
        com.example.ui.components.AppBanSuspensionModal(
            reasonText = appBanMessage,
            onAppealRequest = {
                viewModel.dismissAppBanAppeal()
            },
            onDismissForTest = {
                viewModel.dismissAppBanAppeal()
            }
        )
    }

    if (isAnomalyDetected) {
        com.example.ui.components.AiAnomalySecurityModal(
            reasonText = anomalyReason,
            countdownSeconds = anomalyCountdownSeconds,
            errorMessage = anomalyAuthErrorMessage,
            onVerifyBiometric = { viewModel.verifyBiometricsForAnomaly() },
            onVerifyPin = { pin -> viewModel.verifyPinForAnomaly(pin) },
            onAutoLogout = { viewModel.performAutoLogout() }
        )
    }

    if (loginStep != LoginStep.Completed) {
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = {
                // Completed login
            }
        )
    } else if (isChatActive) {
        ChatScreen(
            viewModel = viewModel,
            onBackClick = { isChatActive = false }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        if (route == "chat") {
                            isChatActive = true
                        } else {
                            currentRoute = route
                        }
                    },
                    onFloatingAddClick = {
                        showGlobalStoryCreatorModal = true
                    }
                )
            },
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                when (currentRoute) {
                    "home" -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onOpenChat = { isChatActive = true },
                            onOpenServicesMenu = { currentRoute = "services" },
                            onNavigateToProduct = { currentRoute = "store" },
                            onNavigateToReels = { currentRoute = "reels" }
                        )
                    }

                    "friends" -> {
                        SocialScreen(
                            viewModel = viewModel,
                            onOpenChatWithFriend = { convId ->
                                viewModel.selectConversation(convId)
                                isChatActive = true
                            },
                            onNavigateToReels = {
                                currentRoute = "reels"
                            }
                        )
                    }

                    "reels" -> {
                        ReelsScreen(viewModel = viewModel)
                    }

                    "store" -> {
                        StoreScreen(
                            viewModel = viewModel,
                            onProductSelected = { product ->
                                viewModel.addToCart(product)
                            }
                        )
                    }

                    "services" -> {
                        ServicesScreen(
                            viewModel = viewModel,
                            onNavigate = { key ->
                                if (key == "chat") {
                                    isChatActive = true
                                } else {
                                    currentRoute = key
                                }
                            }
                        )
                    }

                    "profile" -> {
                        ProfileScreen(viewModel = viewModel)
                    }

                    else -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onOpenChat = { isChatActive = true },
                            onOpenServicesMenu = { currentRoute = "services" },
                            onNavigateToProduct = { currentRoute = "store" },
                            onNavigateToReels = { currentRoute = "reels" }
                        )
                    }
                }
            }
        }
    }
}
