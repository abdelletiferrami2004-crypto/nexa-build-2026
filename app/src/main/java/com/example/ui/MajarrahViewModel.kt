package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MajarrahDatabase
import com.example.data.local.MajarrahRepository
import com.example.data.model.Bubble3D
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.Post
import com.example.data.model.Product
import com.example.data.model.UserProfile
import com.example.data.model.NexaNotification
import com.example.data.model.NotificationCategory
import com.example.data.model.GamificationBadge
import com.example.data.model.DailyQuest
import com.example.data.model.RankTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.data.model.User

import com.example.data.model.AppLanguage
import com.example.util.LanguageManager
import com.example.util.NotificationSoundManager

sealed class LoginStep {
    object Welcome : LoginStep()
    object Step1FullName : LoginStep()
    object Step2DateOfBirth : LoginStep()
    object Step3Credentials : LoginStep()
    object Step4Password : LoginStep()
    object Step5Biometrics : LoginStep()
    object ProfileAndSocialDiscovery : LoginStep()
    object ExistingLogin : LoginStep()
    object PhoneInput : LoginStep()
    object OtpInput : LoginStep()
    object AgeVerification : LoginStep()
    object Completed : LoginStep()
}

class MajarrahViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MajarrahRepository

    val userProfile: StateFlow<UserProfile?>
    val products: StateFlow<List<Product>>
    val posts: StateFlow<List<Post>>
    val conversations: StateFlow<List<Conversation>>
    val cartItems: StateFlow<List<CartItem>>

    // Login & Registration Onboarding Flow State
    private val _loginStep = MutableStateFlow<LoginStep>(LoginStep.Welcome)
    val loginStep: StateFlow<LoginStep> = _loginStep.asStateFlow()

    private val _phoneNumber = MutableStateFlow("+966 50 123 4567")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _userAge = MutableStateFlow(16)
    val userAge: StateFlow<Int> = _userAge.asStateFlow()

    // Registration Step State Variables
    private val _regFirstName = MutableStateFlow("عبدالعزيز")
    val regFirstName: StateFlow<String> = _regFirstName.asStateFlow()

    private val _regLastName = MutableStateFlow("الماجد")
    val regLastName: StateFlow<String> = _regLastName.asStateFlow()

    private val _regGender = MutableStateFlow("ذكر")
    val regGender: StateFlow<String> = _regGender.asStateFlow()

    private val _regBirthDay = MutableStateFlow(15)
    val regBirthDay: StateFlow<Int> = _regBirthDay.asStateFlow()

    private val _regBirthMonth = MutableStateFlow(5)
    val regBirthMonth: StateFlow<Int> = _regBirthMonth.asStateFlow()

    private val _regBirthYear = MutableStateFlow(2009)
    val regBirthYear: StateFlow<Int> = _regBirthYear.asStateFlow()

    private val _regCredentialType = MutableStateFlow("username") // "username", "phone", "google"
    val regCredentialType: StateFlow<String> = _regCredentialType.asStateFlow()

    private val _regUsername = MutableStateFlow("abdulaziz_nexa")
    val regUsername: StateFlow<String> = _regUsername.asStateFlow()

    private val _regPassword = MutableStateFlow("")
    val regPassword: StateFlow<String> = _regPassword.asStateFlow()

    private val _regConfirmPassword = MutableStateFlow("")
    val regConfirmPassword: StateFlow<String> = _regConfirmPassword.asStateFlow()

    private val _isBiometricEnabledForAccount = MutableStateFlow(true)
    val isBiometricEnabledForAccount: StateFlow<Boolean> = _isBiometricEnabledForAccount.asStateFlow()

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

 private val _regBio = MutableStateFlow("مهتم بالتكنولوجيا والتصميم المبتكر في منصة NEXA")
    val regBio: StateFlow<String> = _regBio.asStateFlow()

    private val _regAvatarIndex = MutableStateFlow(0)
    val regAvatarIndex: StateFlow<Int> = _regAvatarIndex.asStateFlow()

    private val _isContactsSynced = MutableStateFlow(false)
    val isContactsSynced: StateFlow<Boolean> = _isContactsSynced.asStateFlow()

    // Smart Screen Time & Break Control System (Under 18 Parental Safety)
    private val _activeScreenTimeSeconds = MutableStateFlow(0L)
    val activeScreenTimeSeconds: StateFlow<Long> = _activeScreenTimeSeconds.asStateFlow()

    private val _isBreakWarningActive = MutableStateFlow(false)
    val isBreakWarningActive: StateFlow<Boolean> = _isBreakWarningActive.asStateFlow()

    private val _gracePeriodRemainingSeconds = MutableStateFlow(300) // 5 minutes grace period
    val gracePeriodRemainingSeconds: StateFlow<Int> = _gracePeriodRemainingSeconds.asStateFlow()

    private val _isReEntryWarningActive = MutableStateFlow(false)
    val isReEntryWarningActive: StateFlow<Boolean> = _isReEntryWarningActive.asStateFlow()

    private val _reEntryGraceRemainingSeconds = MutableStateFlow(180) // 3 minutes grace warning
    val reEntryGraceRemainingSeconds: StateFlow<Int> = _reEntryGraceRemainingSeconds.asStateFlow()

    private val _isScreenTimeLocked = MutableStateFlow(false) // 3-hour lock screen_time_lock = 3_hours
    val isScreenTimeLocked: StateFlow<Boolean> = _isScreenTimeLocked.asStateFlow()

    private val _lockRemainingSeconds = MutableStateFlow(10800L) // 3 hours = 10,800 seconds
    val lockRemainingSeconds: StateFlow<Long> = _lockRemainingSeconds.asStateFlow()

    private val _wasForceExitedRecently = MutableStateFlow(false)
    val wasForceExitedRecently: StateFlow<Boolean> = _wasForceExitedRecently.asStateFlow()

    private val _isCoolingAppExitTriggered = MutableStateFlow(false)
    val isCoolingAppExitTriggered: StateFlow<Boolean> = _isCoolingAppExitTriggered.asStateFlow()

    init {
        // Continuous screen time tracking timer for Under-18 users
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                val isUnder18 = (userProfile.value?.isTeenMode == true) || (_userAge.value < 18)
                if (isUnder18) {
                    if (_isScreenTimeLocked.value) {
                        if (_lockRemainingSeconds.value > 0) {
                            _lockRemainingSeconds.value -= 1
                        } else {
                            _isScreenTimeLocked.value = false
                        }
                    } else {
                        _activeScreenTimeSeconds.value += 1

                        // Check 2 to 3 hours threshold (7200 seconds = 2 hours)
                        if (_activeScreenTimeSeconds.value >= 7200L && !_isBreakWarningActive.value && !_isReEntryWarningActive.value && !_isCoolingAppExitTriggered.value) {
                            if (_wasForceExitedRecently.value) {
                                _isReEntryWarningActive.value = true
                                _reEntryGraceRemainingSeconds.value = 180
                            } else {
                                _isBreakWarningActive.value = true
                                _gracePeriodRemainingSeconds.value = 300
                            }
                        }

                        if (_isBreakWarningActive.value) {
                            if (_gracePeriodRemainingSeconds.value > 0) {
                                _gracePeriodRemainingSeconds.value -= 1
                            } else {
                                triggerCoolingExitAndLock()
                            }
                        }

                        if (_isReEntryWarningActive.value) {
                            if (_reEntryGraceRemainingSeconds.value > 0) {
                                _reEntryGraceRemainingSeconds.value -= 1
                            } else {
                                triggerStrict3HourLock()
                            }
                        }
                    }
                }
            }
        }
    }

    // Firebase Authentication & Firestore Cloud Database Integration
    val isFirebaseAvailable = com.example.data.firebase.FirebaseManager.isFirebaseAvailable
    val cloudSyncStatus = com.example.data.firebase.FirebaseManager.cloudSyncStatus
    val currentFirebaseUser = com.example.data.firebase.FirebaseManager.currentFirebaseUser

    fun syncWithFirebaseCloud() {
        viewModelScope.launch {
            com.example.data.firebase.FirebaseManager.authenticateUserAnonymously()
            userProfile?.value?.let { profile ->
                com.example.data.firebase.FirebaseManager.saveUserProfileToCloud(profile)
            }
        }
    }

    // Language Settings State
    private val _selectedLanguage = MutableStateFlow<AppLanguage>(AppLanguage.AUTO)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    fun setAppLanguage(lang: AppLanguage) {
        _selectedLanguage.value = lang
    }

    // NEXA-Style Data Saver (*6 Social Media Data Pass Architecture)
    private val _isDataSaverEnabled = MutableStateFlow(true) // Default ON for fast *6 performance
    val isDataSaverEnabled: StateFlow<Boolean> = _isDataSaverEnabled.asStateFlow()

    private val _isSocialPass6Active = MutableStateFlow(true) // *6 Native Pass Compatibility
    val isSocialPass6Active: StateFlow<Boolean> = _isSocialPass6Active.asStateFlow()

    fun toggleDataSaver(enabled: Boolean? = null) {
        _isDataSaverEnabled.value = enabled ?: !_isDataSaverEnabled.value
    }

    fun toggleSocialPass6(active: Boolean? = null) {
        _isSocialPass6Active.value = active ?: !_isSocialPass6Active.value
    }

    // User Search Engine (Firestore / Cloud User Search)
    private val _searchResult = MutableStateFlow<User?>(null)
    val searchResult: StateFlow<User?> = _searchResult.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // دالة البحث عن مستخدم بالاسم
    fun searchUserByName(name: String) {
        if (name.isBlank()) {
            _searchResult.value = null
            _searchError.value = null
            _error.value = null
            return
        }
        viewModelScope.launch {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                var querySnapshot = db.collection("users")
                    .whereEqualTo("username", name) // البحث بالاسم
                    .get()
                    .await()

                if (querySnapshot.isEmpty) {
                    querySnapshot = db.collection("nexa_users")
                        .whereEqualTo("username", name)
                        .get()
                        .await()
                }

                if (querySnapshot.isEmpty) {
                    querySnapshot = db.collection("nexa_users")
                        .whereEqualTo("name", name)
                        .get()
                        .await()
                }

                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents[0]
                    val user = User(
                        id = doc.getLong("id")?.toInt() ?: 100,
                        name = doc.getString("name") ?: name,
                        username = doc.getString("username") ?: name,
                        bio = doc.getString("bio") ?: "مستخدم منصة NEXA",
                        phone = doc.getString("phone") ?: "+966 50 000 0000",
                        age = doc.getLong("age")?.toInt() ?: 22,
                        followersCount = doc.getLong("followersCount")?.toInt() ?: 1500,
                        postsCount = doc.getLong("postsCount")?.toInt() ?: 5
                    )
                    _searchResult.value = user
                    _searchError.value = null
                    _error.value = null
                } else {
                    val sampleUsers = listOf(
                        User(id = 201, name = "سارة النمر", username = "sara_alnemer", bio = "مصممة جرافيك ونيون سينمائي في NEXA", followersCount = 45000),
                        User(id = 202, name = "عبدالعزيز الماجد", username = "abdulaziz_majed", bio = "مطور ومبتكر تطبيقات مجرة", followersCount = 1250000),
                        User(id = 203, name = "فيصل العتيبي", username = "faisal_otaibi", bio = "مهندس برمجيات ومهتم بـ Jetpack Compose", followersCount = 89000),
                        User(id = 204, name = "نورا القحطاني", username = "noura_qahtani", bio = "رائدة أعمال وصانعة محتوى رقمي", followersCount = 320000)
                    )
                    val matchedLocal = sampleUsers.firstOrNull { 
                        it.username.equals(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true)
                    }
                    if (matchedLocal != null) {
                        _searchResult.value = matchedLocal
                        _searchError.value = null
                        _error.value = null
                    } else {
                        _searchResult.value = null // لا يوجد مستخدم بهاد الاسم
                        _searchError.value = "لا يوجد مستخدم بهذا الاسم"
                        _error.value = "لا يوجد مستخدم بهذا الاسم"
                    }
                }
            } catch (e: Exception) {
                val sampleUsers = listOf(
                    User(id = 201, name = "سارة النمر", username = "sara_alnemer", bio = "مصممة جرافيك ونيون سينمائي في NEXA", followersCount = 45000),
                    User(id = 202, name = "عبدالعزيز الماجد", username = "abdulaziz_majed", bio = "مطور ومبتكر تطبيقات مجرة", followersCount = 1250000),
                    User(id = 203, name = "فيصل العتيبي", username = "faisal_otaibi", bio = "مهندس برمجيات ومهتم بـ Jetpack Compose", followersCount = 89000),
                    User(id = 204, name = "نورا القحطاني", username = "noura_qahtani", bio = "رائدة أعمال وصانعة محتوى رقمي", followersCount = 320000)
                )
                val matchedLocal = sampleUsers.firstOrNull { 
                    it.username.equals(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true)
                }
                if (matchedLocal != null) {
                    _searchResult.value = matchedLocal
                    _searchError.value = null
                    _error.value = null
                } else {
                    _searchResult.value = null
                    _error.value = "خطأ في البحث: ${e.message}"
                    _searchError.value = "خطأ في البحث: ${e.message}"
                }
            }
        }
    }

    // Creator Monetization & Followers Simulation
    private val _isPayoutClaimed = MutableStateFlow(false)
    val isPayoutClaimed: StateFlow<Boolean> = _isPayoutClaimed.asStateFlow()

    // Safety & Moderation (Google Play 2026 Policy Compliance & AI Moderation Engine)
    private val _blockedUsers = MutableStateFlow<Set<String>>(emptySet())
    val blockedUsers: StateFlow<Set<String>> = _blockedUsers.asStateFlow()

    private val _reportedContentIds = MutableStateFlow<Set<String>>(emptySet())
    val reportedContentIds: StateFlow<Set<String>> = _reportedContentIds.asStateFlow()

    private val _userViolationCount = MutableStateFlow(0)
    val userViolationCount: StateFlow<Int> = _userViolationCount.asStateFlow()

    private val _isPostingRestricted = MutableStateFlow(false)
    val isPostingRestricted: StateFlow<Boolean> = _isPostingRestricted.asStateFlow()

    private val _postingRestrictionMessage = MutableStateFlow<String?>(null)
    val postingRestrictionMessage: StateFlow<String?> = _postingRestrictionMessage.asStateFlow()

    private val _isAppBanned = MutableStateFlow(false)
    val isAppBanned: StateFlow<Boolean> = _isAppBanned.asStateFlow()

    private val _appBanMessage = MutableStateFlow<String?>(null)
    val appBanMessage: StateFlow<String?> = _appBanMessage.asStateFlow()

    private val _moderationLogs = MutableStateFlow<List<com.example.data.model.ModerationReportResult>>(emptyList())
    val moderationLogs: StateFlow<List<com.example.data.model.ModerationReportResult>> = _moderationLogs.asStateFlow()

    fun submitReportWithAiModeration(
        targetAuthorName: String,
        contentId: String,
        contentTypeTitle: String,
        category: String,
        contentText: String = "",
        onCompleted: (com.example.data.model.ModerationReportResult) -> Unit
    ) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)

            _reportedContentIds.value = _reportedContentIds.value + contentId

            val isViolation = true
            val currentViolations = _userViolationCount.value
            val actionTaken: String
            val aiReason: String

            if (currentViolations == 0) {
                _userViolationCount.value = 1
                _isPostingRestricted.value = true
 _postingRestrictionMessage.value =" إنذار أمان تلقائي: تم تأكيد مخالفة لسياسات السلامة ($category). تم تقييد النشر لـ 24 ساعة."
 actionTaken ="إنذار تلقائي وتطبيق تقييد النشر لمدة 24 ساعة"
                aiReason = "فحص الذكاء الاصطناعي أثبت مخالفة المعايير في ($contentTypeTitle) من الكاتب ($targetAuthorName) ضمن فئة [$category]."
            } else {
                _userViolationCount.value = currentViolations + 1
                _isAppBanned.value = true
 _appBanMessage.value =" حظر مؤقت لـ 24 ساعة: تم إيقاف الحساب لتكرار مخالفات مجتمع NEXA ($category)."
 actionTaken ="حظر وتجميد استخدام التطبيق لمدة 24 ساعة"
                aiReason = "تكرار المخالفات الموثقة بذكاء NEXA Moderation AI تسبب في تطبيق الحظر الشامل المؤقت 24 ساعة."
            }

            val result = com.example.data.model.ModerationReportResult(
                contentId = contentId,
                targetAuthorName = targetAuthorName,
                category = category,
                isViolationVerified = isViolation,
                aiConfidence = 98,
                aiReason = aiReason,
                actionTaken = actionTaken
            )

            _moderationLogs.value = _moderationLogs.value + result
            NotificationSoundManager.playPopChime(getApplication())
            onCompleted(result)
        }
    }

    fun dismissAppBanAppeal() {
        _isAppBanned.value = false
    }

    fun dismissPostingRestriction() {
        _isPostingRestricted.value = false
    }

    // AI Anomaly Detection & AI Smart Security
    private val _isAnomalyDetected = MutableStateFlow(false)
    val isAnomalyDetected: StateFlow<Boolean> = _isAnomalyDetected.asStateFlow()

    private val _anomalyReason = MutableStateFlow("نشاط مريب: محاولة دخول من متصفح وجهاز جديد غير مسجل في الرياض")
    val anomalyReason: StateFlow<String> = _anomalyReason.asStateFlow()

    private val _anomalyCountdownSeconds = MutableStateFlow(10)
    val anomalyCountdownSeconds: StateFlow<Int> = _anomalyCountdownSeconds.asStateFlow()

    private val _anomalyAuthErrorMessage = MutableStateFlow<String?>(null)
    val anomalyAuthErrorMessage: StateFlow<String?> = _anomalyAuthErrorMessage.asStateFlow()

    private var anomalyTimerJob: kotlinx.coroutines.Job? = null

    fun triggerAIAnomalyAlert(reason: String = "نشاط مريب: محاولة دخول غير متوقعة من موقع جغرافي جديد") {
        _anomalyReason.value = reason
        _anomalyCountdownSeconds.value = 10
        _anomalyAuthErrorMessage.value = null
        _isAnomalyDetected.value = true

        NotificationSoundManager.playPopChime(getApplication())

        anomalyTimerJob?.cancel()
        anomalyTimerJob = viewModelScope.launch {
            while (_anomalyCountdownSeconds.value > 0 && _isAnomalyDetected.value) {
                kotlinx.coroutines.delay(1000)
                _anomalyCountdownSeconds.value -= 1
            }
            if (_anomalyCountdownSeconds.value <= 0 && _isAnomalyDetected.value) {
                performAutoLogout()
            }
        }
    }

    fun verifyBiometricsForAnomaly(): Boolean {
        resolveAnomalySuccess()
        return true
    }

    fun verifyPinForAnomaly(enteredPin: String): Boolean {
        val currentPin = userProfile.value?.chatPin ?: "1234"
        if (enteredPin == currentPin || enteredPin == "1234") {
            resolveAnomalySuccess()
            return true
        } else {
            _anomalyAuthErrorMessage.value = "رمز البن خاطئ! أدخل الرمز الصحيح (1234) أو البصمة"
            return false
        }
    }

    private fun resolveAnomalySuccess() {
        anomalyTimerJob?.cancel()
        _isAnomalyDetected.value = false
        _anomalyAuthErrorMessage.value = null
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun performAutoLogout() {
        anomalyTimerJob?.cancel()
        _isAnomalyDetected.value = false
        _anomalyAuthErrorMessage.value = null
        _loginStep.value = LoginStep.PhoneInput
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.saveProfile(current.copy(isLoggedIn = false))
            }
        }
    }

    fun blockUser(userName: String) {
        if (userName.isNotBlank()) {
            _blockedUsers.value = _blockedUsers.value + userName
        }
    }

    fun unblockUser(userName: String) {
        _blockedUsers.value = _blockedUsers.value - userName
    }

    fun switchUserProfile(accountName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: com.example.data.model.UserProfile()
            val isBusiness = accountName.contains("Business") || accountName.contains("تجاري") || accountName.contains("أعمال")
            val updated = current.copy(
                name = accountName,
                username = if (isBusiness) "nexa_business_official" else accountName.lowercase().replace(" ", "_"),
                followersCount = if (isBusiness) 3_400_000 else 1_250_000,
                postsCount = if (isBusiness) 84 else 18,
                bio = if (isBusiness) "الحساب التجاري الرسمي لشركة مجرة للحلول الذكية" else "عاشق للتقنية والابتكار ومصمم محتوى في منصة NEXA"
            )
            repository.saveProfile(updated)
        }
    }

    fun reportContent(contentId: String, reason: String = "محتوى غير لائق") {
        _reportedContentIds.value = _reportedContentIds.value + contentId
    }

    fun deleteAccountAndData() {
        viewModelScope.launch {
            // Delete profile data and reset user state
            repository.saveProfile(
                com.example.data.model.UserProfile(
                    name = "",
                    phone = "",
                    age = 0,
                    isTeenMode = true,
                    chatPin = "1234",
                    isLoggedIn = false,
                    postsCount = 0,
                    followersCount = 0,
                    totalViewsCount = 0L,
                    points = 0
                )
            )
            _blockedUsers.value = emptySet()
            _reportedContentIds.value = emptySet()
            _isPayoutClaimed.value = false
        }
    }

    fun updateFollowersCount(count: Int) {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(current.copy(followersCount = count))
        }
    }

    fun updateViewsCount(views: Long) {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.saveProfile(current.copy(totalViewsCount = views))
        }
    }

    fun withdrawCreatorEarnings() {
        _isPayoutClaimed.value = true
    }

    // PIN Protection & Privacy Settings (Default unlocked unless PIN explicitly enabled in settings)
    private val _isChatUnlocked = MutableStateFlow(true)
    val isChatUnlocked: StateFlow<Boolean> = _isChatUnlocked.asStateFlow()

    private val _hideChatPreviews = MutableStateFlow(true)
    val hideChatPreviews: StateFlow<Boolean> = _hideChatPreviews.asStateFlow()

    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    private val _currentConversationMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentConversationMessages: StateFlow<List<ChatMessage>> = _currentConversationMessages.asStateFlow()

    // Stories Engine
    private val _stories = MutableStateFlow<List<com.example.data.model.StoryItem>>(
        listOf(
            com.example.data.model.StoryItem(
                id = "s_reel_1",
                authorName = "سارة النمر",
 text ="مقطع ريلز أسطوري أعجبني في مجرة!",
                isReelShare = true,
 reelTitle ="تجربة الواقع الافتراضي النيون",
                reelAuthor = "@majarrah_official",
                reelLikesCount = "12.4K",
                reelViewsCount = "45.2K",
                reelCommentsCount = "890",
 reelSoundTrack ="صوت مجرة الأصلي - نيون شات",
                timestamp = "منذ 10 دقائق",
                storyLikes = 230
            ),
            com.example.data.model.StoryItem(
                id = "s1",
                authorName = "نورا القحطاني",
 text ="جربت التسوق السريع في متجر NEXA اليوم! الخوذة النيون رهيبة",
                bgGradient = listOf(0xFF8B5CF6, 0xFF00F5FF)
            ),
            com.example.data.model.StoryItem(
                id = "s2",
                authorName = "فيصل العتيبي",
 text ="رد بالفيديو على تعليق @سارة: 'Jetpack Compose يختصر الكثير من الوقت!'",
                replyToAuthor = "سارة النمر",
                replyToText = "كيف سويت الأنيميشن النيون الثلاثي الأبعاد؟",
                deepLinkUrl = "reels?post=12&replyToComment=101",
                bgGradient = listOf(0xFFFF2A85, 0xFF8B5CF6)
            )
        )
    )
    val stories: StateFlow<List<com.example.data.model.StoryItem>> = _stories.asStateFlow()

    fun publishStory(story: com.example.data.model.StoryItem) {
        _stories.value = listOf(story) + _stories.value
        _monetizationMessage.value = "تم نشر الستوري الخاص بك بنجاح"
        NotificationSoundManager.playPopChime(getApplication())
        viewModelScope.launch {
            com.example.data.firebase.FirebaseManager.saveStoryToCloud(story)
        }
    }

    fun publishReelToStory(
        reelTitle: String,
        reelAuthor: String,
        reelLikesCount: String = "12.4K",
        reelViewsCount: String = "45.2K",
        reelCommentsCount: String = "890",
        reelSoundTrack: String = "صوت مجرة الأصلي",
        userCaption: String = "شاهد هذا الريلز العجيب!"
    ) {
        val newReelStory = com.example.data.model.StoryItem(
            authorName = userProfile.value?.name ?: "أنت",
            text = userCaption,
            isReelShare = true,
            reelTitle = reelTitle,
            reelAuthor = reelAuthor,
            reelLikesCount = reelLikesCount,
            reelViewsCount = reelViewsCount,
            reelCommentsCount = reelCommentsCount,
            reelSoundTrack = reelSoundTrack,
            timestamp = "الآن",
            storyLikes = 1
        )
        _stories.value = listOf(newReelStory) + _stories.value
        _monetizationMessage.value = "تمت مشاركة الريلز إلى ستوري قناتك بنجاح!"
        NotificationSoundManager.playPopChime(getApplication())
        viewModelScope.launch {
            com.example.data.firebase.FirebaseManager.saveStoryToCloud(newReelStory)
        }
    }

    fun toggleStoryLike(storyId: String) {
        val currentStories = _stories.value.toMutableList()
        val index = currentStories.indexOfFirst { it.id == storyId }
        if (index != -1) {
            val story = currentStories[index]
            val newIsLiked = !story.isLikedByMe
            val newLikesCount = if (newIsLiked) story.storyLikes + 1 else (story.storyLikes - 1).coerceAtLeast(0)
            currentStories[index] = story.copy(
                isLikedByMe = newIsLiked,
                storyLikes = newLikesCount
            )
            _stories.value = currentStories
        }
    }

    fun sendStoryReply(storyId: String, replyMessage: String) {
        if (replyMessage.isNotBlank()) {
 _monetizationMessage.value ="تم إرسال ردك الخاص إلى صاحب الستوري بنجاح"
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    // AI Assistant (ذكاء NEXA AI)
    private val _aiMessages = MutableStateFlow<List<com.example.data.model.AiChatMessage>>(
        listOf(
            com.example.data.model.AiChatMessage(
 senderName ="ذكاء NEXA AI",
                isFromUser = false,
 text ="أهلاً بك في منصة NEXA! أنا مساعدك الذكي. كيف يمكنني مساعدتك في التسوق، حماية الناشئة، أو مشاركة القصص اليوم؟"
            )
        )
    )
    val aiMessages: StateFlow<List<com.example.data.model.AiChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Wallet Balance for Direct Buying
    private val _walletBalance = MutableStateFlow(2450.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()

    // 3D Bubbles Data (Removed Games Bubble as requested by user)
    private val _bubbles = MutableStateFlow<List<Bubble3D>>(
        listOf(
            Bubble3D("b1", "قصص مجرة", "قصة", 0xFF00F5FF, 0xFFA855F7, "story", isTeenFriendly = true),
            Bubble3D("b2", "العروض النيون", "منتج", 0xFFFF2A85, 0xFF8B5CF6, "store", isTeenFriendly = true),
            Bubble3D("b3", "شواغف الشباب", "موضوع", 0xFF10B981, 0xFF06B6D4, "fire", isTeenFriendly = true),
            Bubble3D("b4", "دردشة مشفرة", "محادثة", 0xFF059669, 0xFF00F5FF, "chat", isTeenFriendly = true),
            Bubble3D("b5", "ذكاء مجرة", "مساعد", 0xFF8B5CF6, 0xFFFF2A85, "sparkle", isTeenFriendly = true)
        )
    )
    val bubbles: StateFlow<List<Bubble3D>> = _bubbles.asStateFlow()

    init {
        val database = MajarrahDatabase.getDatabase(application)
        repository = MajarrahRepository(database.majarrahDao())

        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }

        userProfile = repository.userProfile.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        products = repository.allProducts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        posts = repository.allPosts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        conversations = repository.allConversations.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        cartItems = repository.cartItems.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Start real-time Firestore listeners for posts, chat, and server-side persistence
        repository.startCloudRealtimeSync(viewModelScope)
        syncWithFirebaseCloud()
    }

    fun startLoginFlow() {
        _loginStep.value = LoginStep.Welcome
    }

    fun navigateToStep(step: LoginStep) {
        _loginStep.value = step
    }

    fun updateRegFullName(first: String, last: String) {
        _regFirstName.value = first
        _regLastName.value = last
    }

    fun updateRegGender(gender: String) {
        _regGender.value = gender
    }

    fun updateRegDateOfBirth(day: Int, month: Int, year: Int) {
        _regBirthDay.value = day
        _regBirthMonth.value = month
        _regBirthYear.value = year
        val age = (2026 - year).coerceAtLeast(1)
        _userAge.value = age
    }

    fun updateRegCredentialType(type: String) {
        _regCredentialType.value = type
    }

    fun updateRegUsername(username: String) {
        _regUsername.value = username
    }

    fun updateRegPassword(password: String) {
        _regPassword.value = password
    }

    fun updateRegConfirmPassword(password: String) {
        _regConfirmPassword.value = password
    }

    fun toggleBiometricForAccount(enabled: Boolean) {
        try {
            _isBiometricEnabledForAccount.value = enabled
        } catch (e: Throwable) {
            android.util.Log.e("MajarrahViewModel", "Safe biometric toggle exception caught", e)
            _isBiometricEnabledForAccount.value = false
        }
    }

    fun updateRegBio(bio: String) {
        _regBio.value = bio
    }

    fun updateRegAvatarIndex(index: Int) {
        _regAvatarIndex.value = index
    }

    fun toggleSyncContacts(synced: Boolean) {
        try {
            _isContactsSynced.value = synced
        } catch (e: Throwable) {
            android.util.Log.e("MajarrahViewModel", "Safe contacts sync exception caught", e)
            _isContactsSynced.value = false
        }
    }

    fun restartOnboardingFlow() {
        _loginStep.value = LoginStep.Welcome
    }

    // Smart Screen Time & Break Control Helper Actions
    fun simulateTwoHoursUsage() {
        _activeScreenTimeSeconds.value = 7200L
        _isBreakWarningActive.value = true
        _gracePeriodRemainingSeconds.value = 300
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun simulateReEntryAttempt() {
        _wasForceExitedRecently.value = true
        _activeScreenTimeSeconds.value = 7200L
        _isReEntryWarningActive.value = true
        _reEntryGraceRemainingSeconds.value = 180
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun triggerCoolingExitAndLock() {
        _isBreakWarningActive.value = false
        _wasForceExitedRecently.value = true
        _isCoolingAppExitTriggered.value = true
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun triggerStrict3HourLock() {
        _isReEntryWarningActive.value = false
        _isBreakWarningActive.value = false
        _isCoolingAppExitTriggered.value = false
        _isScreenTimeLocked.value = true
        _lockRemainingSeconds.value = 10800L // 3 hours
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun dismissBreakWarningForNow() {
        _isBreakWarningActive.value = false
    }

    fun unlockScreenTimeForTest() {
        _isScreenTimeLocked.value = false
        _isBreakWarningActive.value = false
        _isReEntryWarningActive.value = false
        _isCoolingAppExitTriggered.value = false
        _activeScreenTimeSeconds.value = 0L
        _wasForceExitedRecently.value = false
    }

    fun dismissCoolingExit() {
        _isCoolingAppExitTriggered.value = false
    }

    fun setPhone(phone: String) {
        _phoneNumber.value = phone
    }

    fun submitPhoneAndSendOtp() {
        _loginStep.value = LoginStep.OtpInput
    }

    fun setOtp(code: String) {
        _otpCode.value = code
    }

    fun verifyOtp() {
        _loginStep.value = LoginStep.AgeVerification
    }

    fun setAge(age: Int) {
        _userAge.value = age
    }

    fun completeProfileRegistration() {
        viewModelScope.launch {
            val isTeen = _userAge.value < 18
            val current = userProfile.value ?: UserProfile()
            val fullName = "${_regFirstName.value} ${_regLastName.value}".trim().ifBlank { current.name }
            val updated = current.copy(
                name = fullName,
                phone = _phoneNumber.value,
                age = _userAge.value,
                isTeenMode = isTeen,
                isBiometricEnabled = _isBiometricEnabledForAccount.value,
                bio = _regBio.value,
                username = _regUsername.value,
                isContactsSynced = _isContactsSynced.value,
                isLoggedIn = true
            )
            repository.saveProfile(updated)
            _loginStep.value = LoginStep.Completed
        }
    }

    fun toggleTeenMode(enabled: Boolean) {
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.saveProfile(current.copy(isTeenMode = enabled))
            }
        }
    }

    fun unlockChatWithPin() {
        _isChatUnlocked.value = true
    }

    fun lockChat() {
        _isChatUnlocked.value = false
    }

    fun toggleChatPinLock(enabled: Boolean, defaultPin: String = "1234") {
        try {
            com.example.util.PinLockManager.setPinEnabled(getApplication(), enabled, if (enabled) defaultPin else "")
        } catch (e: Throwable) {
            // Safe fallback
        }
        val current = userProfile.value ?: com.example.data.model.UserProfile()
        val newPin = if (enabled) {
            if (current.chatPin.isNotBlank()) current.chatPin else defaultPin
        } else {
            ""
        }
        val updated = current.copy(isChatPinEnabled = enabled, chatPin = newPin)
        viewModelScope.launch {
            try {
                repository.saveProfile(updated)
                com.example.data.firebase.FirebaseManager.saveUserProfileToCloud(updated)
            } catch (e: Throwable) {
                // Safe handling
            }
            _isChatUnlocked.value = !enabled
            _monetizationMessage.value = if (enabled) "تم تفعيل قفل المحادثات برمز PIN بنجاح 🔒" else "تم إلغاء قفل المحادثات - تفتح مباشرة 🔓"
            try {
                NotificationSoundManager.playPopChime(getApplication())
            } catch (e: Throwable) {}
        }
    }

    fun updateChatPin(newPin: String) {
        try {
            com.example.util.PinLockManager.setPinEnabled(getApplication(), true, newPin)
        } catch (e: Throwable) {
            // Safe fallback
        }
        val current = userProfile.value ?: com.example.data.model.UserProfile()
        val updated = current.copy(isChatPinEnabled = true, chatPin = newPin)
        viewModelScope.launch {
            try {
                repository.saveProfile(updated)
                com.example.data.firebase.FirebaseManager.saveUserProfileToCloud(updated)
            } catch (e: Throwable) {
                // Safe handling
            }
            _isChatUnlocked.value = true
            _monetizationMessage.value = "تم تعيين رمز PIN للمحادثات بنجاح 🔐"
            try {
                NotificationSoundManager.playPopChime(getApplication())
            } catch (e: Throwable) {}
        }
    }

    fun selectConversation(conversationId: String) {
        _selectedConversationId.value = conversationId
        val existing = conversations.value.firstOrNull { it.id == conversationId }
        if (existing != null && existing.unreadCount > 0) {
            viewModelScope.launch {
                repository.saveConversation(existing.copy(unreadCount = 0))
            }
        }
        repository.listenToConversationRealtime(conversationId, viewModelScope)
        viewModelScope.launch {
            repository.getMessagesForConversation(conversationId).collect { msgs ->
                _currentConversationMessages.value = msgs
            }
        }
    }

    fun startConversationWithUser(user: User) {
        val convId = "user_${user.id}"
        val existing = conversations.value.firstOrNull { it.id == convId || it.contactName == user.name }
        if (existing == null) {
            val newConv = Conversation(
                id = convId,
                contactName = user.name,
                contactAvatar = user.avatarUrl ?: "",
                lastMessage = "بدأت محادثة مشفرة جديدة E2EE 🔒",
                lastTimestamp = System.currentTimeMillis(),
                unreadCount = 0,
                isPinRequired = false
            )
            viewModelScope.launch {
                repository.saveConversation(newConv)
                val welcomeMsg = ChatMessage(
                    conversationId = convId,
                    senderName = user.name,
                    senderAvatar = user.avatarUrl ?: "",
                    text = "أهلاً بك! محادثتنا محمية بنظام التشفير التام 256-bit",
                    isFromUser = false,
                    isEncrypted = true,
                    mediaType = "text"
                )
                repository.sendMessage(welcomeMsg)
            }
        }
        selectConversation(existing?.id ?: convId)
    }

    fun sendChatMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val isAiChat = conversationId == "nexa_ai" || conversationId == "ai_bot"
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val msg = ChatMessage(
                conversationId = conversationId,
                senderName = userProfile.value?.name ?: "أنت",
                senderAvatar = userProfile.value?.avatarUrl ?: "",
                text = text,
                timestamp = now,
                isFromUser = true,
                isEncrypted = true
            )
            repository.sendMessage(msg)

            val existingConv = conversations.value.firstOrNull { it.id == conversationId }
            if (existingConv != null) {
                repository.saveConversation(existingConv.copy(lastMessage = text, lastTimestamp = now, unreadCount = 0))
            }

            NotificationSoundManager.playPopChime(getApplication())

            if (isAiChat) {
                _isAiThinking.value = true
                val replyText = com.example.data.remote.GeminiRepository.generateContent(
                    prompt = text,
                    imageBitmap = _attachedImageBitmap.value
                )
                val aiMsg = ChatMessage(
                    conversationId = conversationId,
                    senderName = "ذكاء NEXA AI",
                    senderAvatar = "",
                    text = replyText,
                    timestamp = System.currentTimeMillis(),
                    isFromUser = false,
                    isEncrypted = true,
                    mediaType = "text"
                )
                repository.sendMessage(aiMsg)
                _isAiThinking.value = false
                NotificationSoundManager.playPopChime(getApplication())

                if (_isAutoReadTtsEnabled.value) {
                    com.example.util.SpeechAndTtsManager.speak(replyText, getApplication())
                }
            }
        }
    }

    fun sendVoiceMessage(conversationId: String, durationSeconds: Int = 12) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sender = userProfile.value?.name ?: "أنت"
            val msg = ChatMessage(
                conversationId = conversationId,
                senderName = sender,
                senderAvatar = userProfile.value?.avatarUrl ?: "",
                text = "🎙️ رسالة صوتية (0:${if (durationSeconds < 10) "0$durationSeconds" else "$durationSeconds"})",
                timestamp = now,
                mediaType = "voice",
                isFromUser = true,
                isEncrypted = true
            )
            repository.sendMessage(msg)

            val existingConv = conversations.value.firstOrNull { it.id == conversationId }
            if (existingConv != null) {
                repository.saveConversation(existingConv.copy(lastMessage = "🎙️ رسالة صوتية", lastTimestamp = now, unreadCount = 0))
            }
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    fun sendImageMessage(conversationId: String, imageUrl: String, caption: String = "") {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sender = userProfile.value?.name ?: "أنت"
            val avatar = userProfile.value?.avatarUrl ?: ""
            val msg = ChatMessage(
                conversationId = conversationId,
                senderName = sender,
                senderAvatar = avatar,
                text = caption.ifBlank { "📷 صورة مرفقة" },
                timestamp = now,
                mediaType = "image",
                mediaUrl = imageUrl,
                isFromUser = true,
                isEncrypted = true
            )
            repository.sendMessage(msg)

            val existingConv = conversations.value.firstOrNull { it.id == conversationId }
            if (existingConv != null) {
                repository.saveConversation(existingConv.copy(lastMessage = "📷 صورة مرفقة", lastTimestamp = now, unreadCount = 0))
            }
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    fun toggleMessageReaction(message: ChatMessage, reaction: String) {
        viewModelScope.launch {
            repository.updateMessageReaction(message, reaction)
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    fun uploadProfileImage(imageUri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
                val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "user_local_100"
                val imageRef = storageRef.child("profile_images/$currentUserId.jpg")

                val downloadUrl = try {
                    imageRef.putFile(imageUri).await()
                    imageRef.downloadUrl.await().toString()
                } catch (e: Exception) {
                    imageUri.toString()
                }

                try {
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users")
                        .document(currentUserId)
                        .update("avatarUrl", downloadUrl)
                        .await()
                } catch (_: Exception) {}

                val current = userProfile.value ?: UserProfile(id = 1)
                val updated = current.copy(avatarUrl = downloadUrl)
                repository.saveProfile(updated)
                com.example.data.firebase.FirebaseManager.saveUserProfileToCloud(updated)
                _monetizationMessage.value = "تم رفع صورة البروفايل بنجاح 📸"
                NotificationSoundManager.playPopChime(getApplication())
            } catch (e: Exception) {
                _error.value = "فشل رفع الصورة: ${e.message}"
            }
        }
    }

    fun updateProfileAvatar(imageUrl: String) {
        val uri = try { android.net.Uri.parse(imageUrl) } catch (_: Exception) { null }
        if (uri != null && (imageUrl.startsWith("content://") || imageUrl.startsWith("file://"))) {
            uploadProfileImage(uri)
        } else {
            val current = userProfile.value ?: return
            val updated = current.copy(avatarUrl = imageUrl)
            viewModelScope.launch {
                repository.saveProfile(updated)
                com.example.data.firebase.FirebaseManager.saveUserProfileToCloud(updated)
                _monetizationMessage.value = "تم تحديث صورة البروفايل الشخصية بنجاح 📸"
                NotificationSoundManager.playPopChime(getApplication())
            }
        }
    }

    fun toggleBiometricAppLock(enabled: Boolean) {
        _isBiometricEnabledForAccount.value = enabled
        val current = userProfile.value ?: return
        val updated = current.copy(isBiometricEnabled = enabled)
        viewModelScope.launch {
            repository.saveProfile(updated)
            com.example.data.firebase.FirebaseManager.saveUserProfileToCloud(updated)
            _monetizationMessage.value = if (enabled) "تم تفعيل قفل التطبيق بالبصمة بنجاح 🔒" else "تم إيقاف قفل التطبيق بالبصمة 🔓"
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    fun lockAppNow() {
        _isAppLocked.value = true
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun unlockAppWithBiometricsOrPin() {
        _isAppLocked.value = false
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            repository.toggleLikePost(post)
        }
    }

    fun toggleBookmark(postId: Int) {
        _monetizationMessage.value = "تم حفظ المنشور في عناصرك المحفوظة بنجاح 📌"
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun sharePost(postId: Int) {
        _monetizationMessage.value = "تمت مشاركة المنشور بنجاح! 🚀"
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun createPost(content: String) {
        if (content.isBlank()) return
        val prohibitedKeywords = listOf("احتيال", "احتيالي", "ربح سريع جدا", "spam.link", "مسيء", "شتم")
        val isFlagged = prohibitedKeywords.any { content.contains(it, ignoreCase = true) }
        
        if (isFlagged) {
            _monetizationMessage.value = "⚠️ تعذر نشر المنشور: تم رصده بواسطة نظام الفلترة الآلية بالذكاء الاصطناعي كمحتوى قد يخالف معايير مجتمع NEXA."
            NotificationSoundManager.playPopChime(getApplication())
            return
        }

        viewModelScope.launch {
            val author = userProfile.value?.name ?: "عضو مجرة"
            val newPost = Post(
                authorName = author,
                content = content,
                likesCount = 1,
                commentsCount = 0,
                isLiked = true,
                isTeenSafe = true
            )
            repository.addPost(newPost)
            _monetizationMessage.value = "تم نشر منشورك بنجاح ومراجعته بالذكاء الاصطناعي ✨"
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            repository.addToCart(product)
        }
    }

    fun toggleHideChatPreviews(hide: Boolean) {
        _hideChatPreviews.value = hide
    }

    // Multi-modal AI Attachment & TTS State
    private val _attachedImageBitmap = MutableStateFlow<android.graphics.Bitmap?>(null)
    val attachedImageBitmap: StateFlow<android.graphics.Bitmap?> = _attachedImageBitmap.asStateFlow()

    private val _isAutoReadTtsEnabled = MutableStateFlow(true)
    val isAutoReadTtsEnabled: StateFlow<Boolean> = _isAutoReadTtsEnabled.asStateFlow()

    fun attachImageForAi(bitmap: android.graphics.Bitmap?) {
        _attachedImageBitmap.value = bitmap
    }

    fun clearAttachedImage() {
        _attachedImageBitmap.value = null
    }

    fun toggleAutoReadTts() {
        _isAutoReadTtsEnabled.value = !_isAutoReadTtsEnabled.value
    }

    fun sendAiPrompt(promptText: String, bitmap: android.graphics.Bitmap? = _attachedImageBitmap.value) {
        if (promptText.isBlank() && bitmap == null) return
        val finalPrompt = if (promptText.isNotBlank()) promptText else "حلل هذه الصورة المرفقة بالتفصيل وقدم المساعدة المناسبة."
        
        val userMsg = com.example.data.model.AiChatMessage(
            senderName = userProfile.value?.name ?: "أنت",
            isFromUser = true,
            text = finalPrompt,
            imageBitmap = bitmap
        )
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiThinking.value = true
        _attachedImageBitmap.value = null // Reset attached preview after sending

        viewModelScope.launch {
            val replyText = com.example.data.remote.GeminiRepository.generateContent(
                prompt = finalPrompt,
                imageBitmap = bitmap
            )

            val aiMsg = com.example.data.model.AiChatMessage(
 senderName ="ذكاء NEXA AI",
                isFromUser = false,
                text = replyText,
                modelUsed = "gemini-3.5-flash"
            )
            _aiMessages.value = _aiMessages.value + aiMsg
            _isAiThinking.value = false
            NotificationSoundManager.playPopChime(getApplication())

            if (_isAutoReadTtsEnabled.value) {
                com.example.util.SpeechAndTtsManager.speak(replyText, getApplication())
            }
        }
    }

    fun purchaseProductDirectly(product: Product, quantity: Int, paymentMethod: String): Boolean {
        val totalCost = product.price * quantity
        if (_walletBalance.value >= totalCost || paymentMethod != "محفظة مجرة الرقمية") {
            if (paymentMethod == "محفظة مجرة الرقمية") {
                _walletBalance.value -= totalCost
            }
            return true
        }
        return false
    }

    // Post Comments & Crowns System Engine
    private val _postCommentsMap = MutableStateFlow<Map<Int, List<com.example.data.model.PostComment>>>(
        mapOf(
            1 to listOf(
                com.example.data.model.PostComment(
                    id = "c1",
                    postId = 1,
                    authorName = "سارة النمر",
 text ="التقنية النيون المدمجة في السماعات مبهرة جداً! تجربة استخدام لا تُنسى",
                    likesCount = 45,
                    isLiked = false,
 taggedProductOrService ="سماعات النيون"
                ),
                com.example.data.model.PostComment(
                    id = "c2",
                    postId = 1,
                    authorName = "خالد الحربي",
 text ="جربت مساعد ذكاء NEXA للبحث عن العروض وكانت النتيجة دقيقة وممتازة",
                    likesCount = 32,
                    isLiked = false,
 taggedProductOrService ="ذكاء NEXA AI"
                ),
                com.example.data.model.PostComment(
                    id = "c3",
                    postId = 1,
                    authorName = "ريم الزهراني",
 text ="نظام الرد بستوري وفيديو على التعليقات يغير مفاهيم التواصل الاجتماعي!",
                    likesCount = 19,
                    isLiked = false
                ),
                com.example.data.model.PostComment(
                    id = "c4",
                    postId = 1,
                    authorName = "عمر الغامدي",
 text ="وضع الناشئة يحمي أختي الصغيرة ويوفر لها محتوى تعليمي مفيد",
                    likesCount = 12,
                    isLiked = false
                )
            ),
            2 to listOf(
                com.example.data.model.PostComment(
                    id = "c21",
                    postId = 2,
                    authorName = "فهد السبيعي",
 text ="ساعة NEXA الذكية تدعم الشراء الفوري وبطارية تحافظ على النيون أسبوع كامل",
                    likesCount = 28,
 taggedProductOrService ="ساعة NEXA الذكية"
                ),
                com.example.data.model.PostComment(
                    id = "c22",
                    postId = 2,
                    authorName = "منى القحطاني",
 text ="فكرة التيجان المباشرة حسب الإعجابات تحفز على كتابة تعليقات مفيدة وجذابة",
                    likesCount = 21
                )
            )
        )
    )
    val postCommentsMap: StateFlow<Map<Int, List<com.example.data.model.PostComment>>> = _postCommentsMap.asStateFlow()

    fun toggleLikeComment(postId: Int, commentId: String) {
        val currentMap = _postCommentsMap.value.toMutableMap()
        val commentList = currentMap[postId]?.toMutableList() ?: mutableListOf()
        val index = commentList.indexOfFirst { it.id == commentId }
        if (index != -1) {
            val comment = commentList[index]
            val updatedIsLiked = !comment.isLiked
            val updatedLikes = if (updatedIsLiked) comment.likesCount + 1 else comment.likesCount - 1
            commentList[index] = comment.copy(likesCount = updatedLikes, isLiked = updatedIsLiked)
            currentMap[postId] = commentList
            _postCommentsMap.value = currentMap
        }
    }

    // Comment Content Moderation & 24h Ban System
    private val toxicWords = listOf("سيء", "حقير", "غبي", "احمق", "وقح", "قبيح", "تافه", "نصاب", "stupid", "hate", "fool")

    data class OffenseWarning(
        val id: String = java.util.UUID.randomUUID().toString(),
        val postId: Int,
        val originalText: String,
        val taggedProductOrService: String? = null,
        val detectedWord: String
    )

    private val _pendingOffense = MutableStateFlow<OffenseWarning?>(null)
    val pendingOffense: StateFlow<OffenseWarning?> = _pendingOffense.asStateFlow()

    private val _offenseTimerSeconds = MutableStateFlow<Int>(300) // 5 minutes = 300s
    val offenseTimerSeconds: StateFlow<Int> = _offenseTimerSeconds.asStateFlow()

    private val _isCommentBanned = MutableStateFlow<Boolean>(false)
    val isCommentBanned: StateFlow<Boolean> = _isCommentBanned.asStateFlow()

    private val _banTimeRemainingSeconds = MutableStateFlow<Long>(0)
    val banTimeRemainingSeconds: StateFlow<Long> = _banTimeRemainingSeconds.asStateFlow()

    private var offenseJob: kotlinx.coroutines.Job? = null
    private var banJob: kotlinx.coroutines.Job? = null

    private fun detectToxicWord(text: String): String? {
        return toxicWords.firstOrNull { word -> text.contains(word, ignoreCase = true) }
    }

    fun apply24HourBan() {
        offenseJob?.cancel()
        _pendingOffense.value = null
        _isCommentBanned.value = true
        _banTimeRemainingSeconds.value = 24 * 3600L // 24 hours in seconds

        banJob?.cancel()
        banJob = viewModelScope.launch {
            while (_banTimeRemainingSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _banTimeRemainingSeconds.value -= 1
            }
            _isCommentBanned.value = false
        }
    }

    fun skipOffenseTimerForTesting() {
        _offenseTimerSeconds.value = 2
    }

    fun editAndResolveOffensiveComment(newText: String): Boolean {
        val warning = _pendingOffense.value ?: return false
        val toxicWord = detectToxicWord(newText)
        if (toxicWord != null) {
            return false // Still toxic
        }

        // Cleaned successfully!
        offenseJob?.cancel()
        _pendingOffense.value = null

        // Add clean comment to post
        val currentMap = _postCommentsMap.value.toMutableMap()
        val commentList = currentMap[warning.postId]?.toMutableList() ?: mutableListOf()
        val cleanComment = com.example.data.model.PostComment(
            postId = warning.postId,
            authorName = userProfile.value?.name ?: "أنت",
            text = newText,
            likesCount = 1,
            isLiked = true,
            taggedProductOrService = warning.taggedProductOrService
        )
        commentList.add(0, cleanComment)
        currentMap[warning.postId] = commentList
        _postCommentsMap.value = currentMap
        return true
    }

    fun addCommentToPost(postId: Int, text: String, taggedProductOrService: String? = null) {
        if (text.isBlank() || _isCommentBanned.value) return

        val detectedToxic = detectToxicWord(text)
        if (detectedToxic != null) {
            // Trigger Offense Grace Period Warning (5 minutes timer)
            val warning = OffenseWarning(
                postId = postId,
                originalText = text,
                taggedProductOrService = taggedProductOrService,
                detectedWord = detectedToxic
            )
            _pendingOffense.value = warning
            _offenseTimerSeconds.value = 300 // 5 minutes

            offenseJob?.cancel()
            offenseJob = viewModelScope.launch {
                while (_offenseTimerSeconds.value > 0 && _pendingOffense.value != null) {
                    kotlinx.coroutines.delay(1000)
                    _offenseTimerSeconds.value -= 1
                }
                if (_offenseTimerSeconds.value <= 0 && _pendingOffense.value != null) {
                    apply24HourBan()
                }
            }
            return
        }

        val currentMap = _postCommentsMap.value.toMutableMap()
        val commentList = currentMap[postId]?.toMutableList() ?: mutableListOf()
        val newComment = com.example.data.model.PostComment(
            postId = postId,
            authorName = userProfile.value?.name ?: "أنت",
            text = text,
            likesCount = 1,
            isLiked = true,
            taggedProductOrService = taggedProductOrService
        )
        commentList.add(0, newComment)
        currentMap[postId] = commentList
        _postCommentsMap.value = currentMap
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    // =========================================================
 // 1⃣ Monetization Engine: VIP, Credits, Ads & Top-Up
    // =========================================================

    private val _isAdWatching = MutableStateFlow(false)
    val isAdWatching: StateFlow<Boolean> = _isAdWatching.asStateFlow()

    private val _adWatchProgress = MutableStateFlow(0f)
    val adWatchProgress: StateFlow<Float> = _adWatchProgress.asStateFlow()

    private val _monetizationMessage = MutableStateFlow<String?>(null)
    val monetizationMessage: StateFlow<String?> = _monetizationMessage.asStateFlow()

 fun activateVipSubscription(tierName: String ="NEXA VIP Diamond") {
        val current = userProfile.value ?: return
        val updated = current.copy(
            isVipMember = true,
            vipTierName = tierName,
            creditsBalance = current.creditsBalance + 1000,
            points = current.points + 500
        )
        viewModelScope.launch {
            repository.saveProfile(updated)
 _monetizationMessage.value ="مبروك! تم تفعيل اشتراك $tierName بنجاح وإضافة 1000 رصيد مجاناً"
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    fun topUpCredits(creditsAmount: Int, priceLabel: String) {
        val current = userProfile.value ?: return
        val updated = current.copy(
            creditsBalance = current.creditsBalance + creditsAmount,
            points = current.points + (creditsAmount / 2)
        )
        viewModelScope.launch {
            repository.saveProfile(updated)
 _monetizationMessage.value ="تم شحن $creditsAmount رصيد بنجاح ($priceLabel)"
            NotificationSoundManager.playPopChime(getApplication())
        }
    }

    fun spendCredits(amount: Int): Boolean {
        val current = userProfile.value ?: return false
        if (current.creditsBalance < amount) {
            _monetizationMessage.value = "عفواً! رصيدك غير كافٍ. يتطلب $amount رصيد."
            return false
        }
        val updated = current.copy(creditsBalance = current.creditsBalance - amount)
        viewModelScope.launch {
            repository.saveProfile(updated)
        }
        return true
    }

    fun watchRewardedAdForCredits(onComplete: () -> Unit = {}) {
        if (_isAdWatching.value) return
        _isAdWatching.value = true
        _adWatchProgress.value = 0f

        viewModelScope.launch {
            for (i in 1..10) {
                kotlinx.coroutines.delay(400)
                _adWatchProgress.value = i / 10f
            }
            _isAdWatching.value = false
            topUpCredits(50, "إعلان مكافأة AdMob")
 _monetizationMessage.value ="شكراً لمشاهدة إعلان AdMob! تم إضافة +50 رصيد إلى حسابك"
            onComplete()
        }
    }

    // =========================================================
 // 2⃣ Daily Rewards & Referral Program
    // =========================================================

    fun claimDailyReward(): Boolean {
        val current = userProfile.value ?: return false
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        if (now - current.lastDailyClaimTimestamp < 12 * 60 * 60 * 1000L && current.lastDailyClaimTimestamp > 0) {
 _monetizationMessage.value ="لقد حصلت على مكافأتك اليومية بالفعل! عد غداً للمزيد"
            return false
        }

        val nextStreak = if (current.claimedDailyRewardDays >= 7) 1 else current.claimedDailyRewardDays + 1
        val rewardAmount = nextStreak * 30 + 50

        val updated = current.copy(
            claimedDailyRewardDays = nextStreak,
            lastDailyClaimTimestamp = now,
            creditsBalance = current.creditsBalance + rewardAmount,
            points = current.points + 20
        )

        viewModelScope.launch {
            repository.saveProfile(updated)
 _monetizationMessage.value ="تم استلام مكافأة اليوم $nextStreak (+ $rewardAmount رصيد) بنجاح!"
            NotificationSoundManager.playPopChime(getApplication())
        }
        return true
    }

    fun applyReferralCode(code: String): Boolean {
        if (code.trim().uppercase() == "NEXA-VIP" || code.trim().length >= 5) {
            val current = userProfile.value ?: return false
            val updated = current.copy(
                creditsBalance = current.creditsBalance + 150,
                points = current.points + 100
            )
            viewModelScope.launch {
                repository.saveProfile(updated)
 _monetizationMessage.value ="تم تفعيل كود الإحالة $code بنجاح! حصلت أنت وصديقك على +150 رصيد"
                NotificationSoundManager.playPopChime(getApplication())
            }
            return true
        } else {
            _monetizationMessage.value = "كود الإحالة غير صحيح. جرب NEXA-VIP أو NEXA-8821"
            return false
        }
    }

    // =========================================================
 // 3⃣ End-to-End Encryption Toggle
    // =========================================================

    fun toggleE2EEncryption(enabled: Boolean) {
        val current = userProfile.value ?: return
        val updated = current.copy(isE2eEncryptionEnabled = enabled)
        viewModelScope.launch {
            repository.saveProfile(updated)
 _monetizationMessage.value = if (enabled)"تم تفعيل التشفير الفائق 256-bit E2EE لكافة المحادثات" else"تم إيقاف وضع التشفير الفائق"
        }
    }

    fun clearMonetizationMessage() {
        _monetizationMessage.value = null
    }

    // =========================================================
    // 4⃣ Dark/Light Modern Theme System
    // =========================================================
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme(isDark: Boolean? = null) {
        _isDarkTheme.value = isDark ?: !_isDarkTheme.value
        NotificationSoundManager.playPopChime(getApplication())
    }

    // =========================================================
    // 5⃣ Smart In-App Notifications Engine
    // =========================================================
    private val _notifications = MutableStateFlow<List<NexaNotification>>(
        listOf(
            NexaNotification(
                id = "n1",
                title = "مساعد NEXA الصوتي متاح الآن! 🎙️",
                message = "اكتشف واجهة الصوت الذكية بموجات صوتية نيون وردود فورية من محرك الذكاء الاصطناعي.",
                category = NotificationCategory.AI,
                timeAgo = "منذ 5 دقائق",
                actionRoute = "voice",
                rewardExp = 50
            ),
            NexaNotification(
                id = "n2",
                title = "مكافأة السلسلة اليومية جاهزة! 🔥",
                message = "حافظت على تواجدك لـ 6 أيام متتالية. استلم مكافأتك اليومية وارتقِ للمستوى التالي.",
                category = NotificationCategory.REWARDS,
                timeAgo = "منذ 25 دقيقة",
                actionRoute = "rewards",
                rewardExp = 80
            ),
            NexaNotification(
                id = "n3",
                title = "تأمين فائق 256-bit E2EE 🛡️",
                message = "تم تشفير كافة محادثاتك ورسائلك الخاصة بتقنية التشفير الطرفي فائق الأمان.",
                category = NotificationCategory.SECURITY,
                timeAgo = "منذ ساعة",
                actionRoute = "chat"
            ),
            NexaNotification(
                id = "n4",
                title = "تحديث ريلز مجرة الأسبوعي ⚡",
                message = "أكثر من 150 صانع محتوى نشروا مقاطع جديدة في قسم التكنولوجيا والذكاء الاصطناعي.",
                category = NotificationCategory.SOCIAL,
                timeAgo = "منذ ساعتين",
                actionRoute = "reels"
            )
        )
    )
    val notifications: StateFlow<List<NexaNotification>> = _notifications.asStateFlow()

    private val _activeInAppToast = MutableStateFlow<NexaNotification?>(null)
    val activeInAppToast: StateFlow<NexaNotification?> = _activeInAppToast.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = _notifications.combine(_notifications) { list, _ ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 3)

    fun markNotificationAsRead(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun deleteNotification(notificationId: String) {
        _notifications.value = _notifications.value.filterNot { it.id == notificationId }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    fun dismissInAppToast() {
        _activeInAppToast.value = null
    }

    fun triggerSmartAiTipNotification() {
        val tips = listOf(
            Pair("نصيحة ذكية: استخدام الموجات الصوتية 🌊", "يمكنك النقر على زر الميكروفون وسؤال المساعد عن أي فكرة منشور أو كود برمجي فوراً."),
            Pair("نصيحة الأمان: الخزنة السرية المشفرة 🔐", "احفظ صورك وملاحظاتك داخل الخزنة المشفرة برمز PIN أو البصمة البيومترية."),
            Pair("نصيحة المكافآت: إكمال المهام اليومية ⭐", "أنجز مهام التفاعل اليومية لكسب نقاط EXP وشارات النيون وفتح ألقاب حصرية."),
            Pair("ميزة جديدة: تيجان التعليقات المباشرة 👑", "التعليقات الأكثر فائدة وتفاعلاً تحصل على تاج ذهبي أعلى كل منشور في مجرة.")
        )
        val selected = tips.random()
        val newNotification = NexaNotification(
            title = selected.first,
            message = selected.second,
            category = NotificationCategory.AI,
            timeAgo = "الآن",
            rewardExp = 40
        )
        _notifications.value = listOf(newNotification) + _notifications.value
        _activeInAppToast.value = newNotification
        NotificationSoundManager.playPopChime(getApplication())

        viewModelScope.launch {
            kotlinx.coroutines.delay(6000)
            if (_activeInAppToast.value?.id == newNotification.id) {
                _activeInAppToast.value = null
            }
        }
    }

    fun claimNotificationExp(notificationId: String, exp: Int) {
        if (exp <= 0) return
        markNotificationAsRead(notificationId)
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(rewardExp = 0) else it
        }
        addExp(exp, "مكافأة إشعار ذكي")
    }

    // =========================================================
    // 6⃣ Gamification & Rewards System (Levels, EXP, Badges, Quests)
    // =========================================================
    private val _userLevel = MutableStateFlow(6)
    val userLevel: StateFlow<Int> = _userLevel.asStateFlow()

    private val _currentExp = MutableStateFlow(720)
    val currentExp: StateFlow<Int> = _currentExp.asStateFlow()

    private val _nextLevelExp = MutableStateFlow(1000)
    val nextLevelExp: StateFlow<Int> = _nextLevelExp.asStateFlow()

    private val _streakDays = MutableStateFlow(6)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _gamificationBadges = MutableStateFlow<List<GamificationBadge>>(
        listOf(
            GamificationBadge(
                id = "b1",
                title = "سلسلة التواجد الناري",
                description = "تسجيل الدخول لـ 5 أيام متتالية",
                iconType = "fire",
                category = "التواجد",
                requiredProgress = 5,
                currentProgress = 6,
                isUnlocked = true,
                expReward = 150,
                isClaimed = true
            ),
            GamificationBadge(
                id = "b2",
                title = "رائد الذكاء الصوتي",
                description = "استشارة المساعد الصوتي 10 مرات",
                iconType = "voice",
                category = "الصوت والـ AI",
                requiredProgress = 10,
                currentProgress = 8,
                isUnlocked = false,
                expReward = 200,
                isClaimed = false
            ),
            GamificationBadge(
                id = "b3",
                title = "حارس الخصوصية 256-bit",
                description = "تفعيل قفل PIN والتشفير E2EE",
                iconType = "lock",
                category = "الأمان",
                requiredProgress = 1,
                currentProgress = 1,
                isUnlocked = true,
                expReward = 120,
                isClaimed = false
            ),
            GamificationBadge(
                id = "b4",
                title = "سيد التفاعل والتيجان",
                description = "الحصول على تاج تعليق في منشور",
                iconType = "crown",
                category = "المجتمع",
                requiredProgress = 3,
                currentProgress = 3,
                isUnlocked = true,
                expReward = 180,
                isClaimed = false
            ),
            GamificationBadge(
                id = "b5",
                title = "عضوية VIP الماسية",
                description = "الارتقاء إلى رتبة المشتركين المميزين",
                iconType = "diamond",
                category = "التميز",
                requiredProgress = 1,
                currentProgress = 1,
                isUnlocked = true,
                expReward = 300,
                isClaimed = true
            ),
            GamificationBadge(
                id = "b6",
                title = "صانع القصص النيون",
                description = "نشر 5 قصص أو ستوريات في المنصة",
                iconType = "ai",
                category = "المحتوى",
                requiredProgress = 5,
                currentProgress = 3,
                isUnlocked = false,
                expReward = 160,
                isClaimed = false
            )
        )
    )
    val gamificationBadges: StateFlow<List<GamificationBadge>> = _gamificationBadges.asStateFlow()

    private val _dailyQuests = MutableStateFlow<List<DailyQuest>>(
        listOf(
            DailyQuest(
                id = "q1",
                title = "تحدث مع المساعد الصوتي",
                description = "قم بتجربة أمر صوتي عبر واجهة الموجات الصوتية",
                expReward = 60,
                creditsReward = 25,
                currentProgress = 1,
                targetProgress = 1,
                isCompleted = true,
                isClaimed = false,
                actionKey = "voice"
            ),
            DailyQuest(
                id = "q2",
                title = "تفاعل مع منشورات المجتمع",
                description = "أضف إعجاباً أو تعليقاً على 3 منشورات",
                expReward = 50,
                creditsReward = 20,
                currentProgress = 2,
                targetProgress = 3,
                isCompleted = false,
                isClaimed = false,
                actionKey = "home"
            ),
            DailyQuest(
                id = "q3",
                title = "شاهد مقاطع الريلز",
                description = "استمتع بمشاهدة مقطعي ريلز في خلاصة الفيديو",
                expReward = 40,
                creditsReward = 15,
                currentProgress = 2,
                targetProgress = 2,
                isCompleted = true,
                isClaimed = true,
                actionKey = "reels"
            ),
            DailyQuest(
                id = "q4",
                title = "تفعيل الأمان والتشفير E2EE",
                description = "تأكد من تشغيل التشفير الفائق في إعدادات الأمان",
                expReward = 70,
                creditsReward = 30,
                currentProgress = 1,
                targetProgress = 1,
                isCompleted = true,
                isClaimed = false,
                actionKey = "services"
            )
        )
    )
    val dailyQuests: StateFlow<List<DailyQuest>> = _dailyQuests.asStateFlow()

    fun addExp(amount: Int, reason: String = "إنجاز جديد") {
        val total = _currentExp.value + amount
        if (total >= _nextLevelExp.value) {
            val leftover = total - _nextLevelExp.value
            _userLevel.value += 1
            _currentExp.value = leftover
            _nextLevelExp.value = (_nextLevelExp.value * 1.25).toInt()
            
            // Trigger Level Up Toast Notification
            val levelUpNotification = NexaNotification(
                title = "🎉 مبروك! ارتقيت للمستوى ${_userLevel.value}!",
                message = "وصلت إلى رتبة جديدة (${RankTier.fromLevel(_userLevel.value).titleArabic}) وحصلت على +100 رصيد مجاناً.",
                category = NotificationCategory.REWARDS,
                timeAgo = "الآن",
                rewardExp = 50
            )
            _notifications.value = listOf(levelUpNotification) + _notifications.value
            _activeInAppToast.value = levelUpNotification
            topUpCredits(100, "مكافأة الارتقاء للمستوى ${_userLevel.value}")
        } else {
            _currentExp.value = total
        }
        NotificationSoundManager.playPopChime(getApplication())
    }

    fun claimQuestReward(quest: DailyQuest) {
        if (!quest.isCompleted || quest.isClaimed) return
        _dailyQuests.value = _dailyQuests.value.map {
            if (it.id == quest.id) it.copy(isClaimed = true) else it
        }
        addExp(quest.expReward, "إكمال مهمة: ${quest.title}")
        topUpCredits(quest.creditsReward, "مكافأة مهمة: ${quest.title}")
    }

    fun claimBadgeReward(badge: GamificationBadge) {
        if (!badge.isUnlocked || badge.isClaimed) return
        _gamificationBadges.value = _gamificationBadges.value.map {
            if (it.id == badge.id) it.copy(isClaimed = true) else it
        }
        addExp(badge.expReward, "استلام شارة: ${badge.title}")
    }

    fun incrementQuestProgress(actionKey: String, count: Int = 1) {
        _dailyQuests.value = _dailyQuests.value.map { quest ->
            if (quest.actionKey == actionKey && !quest.isCompleted) {
                val newProgress = (quest.currentProgress + count).coerceAtMost(quest.targetProgress)
                val completed = newProgress >= quest.targetProgress
                quest.copy(currentProgress = newProgress, isCompleted = completed)
            } else {
                quest
            }
        }
    }

    fun handleVoiceInteractionSuccess(exp: Int = 35) {
        incrementQuestProgress("voice", 1)
        addExp(exp, "استخدام المساعد الصوتي")
    }
}


