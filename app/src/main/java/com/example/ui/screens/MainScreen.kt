package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MajarrahViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

data class MainSidebarNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

/**
 * MainScreen with Adaptive WindowSizeClass Support for Phones, Foldables, and Tablets.
 * 
 * - Compact (phone portrait): Uses BottomNavBar with full-bleed content.
 * - Medium (foldables unfolded, landscape phones, small tablets): Uses sleek side NavigationRail.
 * - Expanded (large tablets, desktop/DeX): Uses spacious, rich Sidebar with brand header,
 *   full navigation pills, quick action buttons, and user profile status card.
 */
@Composable
fun MainScreen(
    viewModel: MajarrahViewModel,
    windowSizeClass: WindowSizeClass? = null,
    onOpenChatFullScreen: () -> Unit,
    onOpenStoryCreator: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthClass = windowSizeClass?.widthSizeClass ?: when {
            maxWidth >= 840.dp -> WindowWidthSizeClass.Expanded
            maxWidth >= 600.dp -> WindowWidthSizeClass.Medium
            else -> WindowWidthSizeClass.Compact
        }

        val heightClass = windowSizeClass?.heightSizeClass ?: when {
            maxHeight >= 900.dp -> WindowHeightSizeClass.Expanded
            maxHeight >= 480.dp -> WindowHeightSizeClass.Medium
            else -> WindowHeightSizeClass.Compact
        }

        val isWideLayout = widthClass != WindowWidthSizeClass.Compact
        val isExpandedLayout = widthClass == WindowWidthSizeClass.Expanded && maxWidth >= 840.dp

        var currentRoute by remember { mutableStateOf("home") }

        androidx.activity.compose.BackHandler(enabled = currentRoute != "home") {
            currentRoute = "home"
        }

        val sidebarNavItems = remember {
            listOf(
                MainSidebarNavItem("home", "الرئيسية", Icons.Default.Home),
                MainSidebarNavItem("friends", "الأصدقاء", Icons.Default.People),
                MainSidebarNavItem("chat", "المحادثات", Icons.AutoMirrored.Filled.Chat),
                MainSidebarNavItem("reels", "الريلز", Icons.Default.VideoLibrary),
                MainSidebarNavItem("store", "المتجر", Icons.Default.ShoppingBag),
                MainSidebarNavItem("services", "الخدمات", Icons.Default.Menu),
                MainSidebarNavItem("profile", "حسابي", Icons.Default.Person)
            )
        }

        if (isWideLayout) {
            // Adaptive Large Screen / Foldable Layout (Side-by-side: Sidebar + Content View)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Adaptive Navigation Sidebar / Rail
                MainAdaptiveSidebar(
                    items = sidebarNavItems,
                    currentRoute = currentRoute,
                    isExpanded = isExpandedLayout,
                    viewModel = viewModel,
                    onNavigate = { route ->
                        if (route == "chat") {
                            // On large screens, we can either switch content to chat or launch fullscreen
                            currentRoute = "chat"
                        } else {
                            currentRoute = route
                        }
                    },
                    onOpenStoryCreator = onOpenStoryCreator
                )

                // Main Content View Area with graceful adaptive constraints
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = if (isExpandedLayout) 1300.dp else 900.dp)
                    ) {
                        MainContentHost(
                            currentRoute = currentRoute,
                            viewModel = viewModel,
                            onNavigateToRoute = { currentRoute = it },
                            onOpenChat = {
                                if (isWideLayout) {
                                    currentRoute = "chat"
                                } else {
                                    onOpenChatFullScreen()
                                }
                            }
                        )
                    }
                }
            }
        } else {
            // Compact Phone Layout with standard BottomNavBar
            Scaffold(
                bottomBar = {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onTabSelected = { route ->
                            if (route == "chat") {
                                onOpenChatFullScreen()
                            } else {
                                currentRoute = route
                            }
                        },
                        onFloatingAddClick = onOpenStoryCreator
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                ) {
                    MainContentHost(
                        currentRoute = currentRoute,
                        viewModel = viewModel,
                        onNavigateToRoute = { currentRoute = it },
                        onOpenChat = onOpenChatFullScreen
                    )
                }
            }
        }
    }
}

@Composable
private fun MainAdaptiveSidebar(
    items: List<MainSidebarNavItem>,
    currentRoute: String,
    isExpanded: Boolean,
    viewModel: MajarrahViewModel,
    onNavigate: (String) -> Unit,
    onOpenStoryCreator: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isProUser by viewModel.isNexaProSubscriber.collectAsState()
    val sidebarWidth = if (isExpanded) 240.dp else 88.dp

    Surface(
        modifier = Modifier
            .width(sidebarWidth)
            .fillMaxHeight()
            .testTag("main_adaptive_sidebar"),
        color = BackgroundDark,
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.35f),
                            NeonPurple.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.ui.graphics.RectangleShape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(vertical = 16.dp, horizontal = if (isExpanded) 12.dp else 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Brand & Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isExpanded) 8.dp else 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeonCyan, NeonPurple, NeonPink)
                                    )
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "N",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "مجرة NEXA",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isProUser) "NEXA PRO 👑" else "الجيل القادم 2026",
                                        color = if (isProUser) NeonAmber else NeonCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Story Creator Action Button
                    if (isExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NeonPink, NeonPurple, NeonCyan)
                                    )
                                )
                                .clickable { onOpenStoryCreator() }
                                .testTag("sidebar_story_creator_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "قصة جديدة",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "نشر قصة جديدة",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(8.dp, CircleShape, spotColor = NeonCyan)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeonCyan, NeonPink)
                                    )
                                )
                                .clickable { onOpenStoryCreator() }
                                .testTag("sidebar_story_creator_button_compact"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "نشر قصة",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Navigation Menu Items
                    items.forEach { item ->
                        val isSelected = currentRoute == item.route
                        SidebarNavItemCell(
                            item = item,
                            isSelected = isSelected,
                            isExpanded = isExpanded,
                            onSelect = { onNavigate(item.route) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                // Bottom User / Status Card
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isProUser) NeonAmber.copy(alpha = 0.12f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                1.dp,
                                if (isProUser) NeonAmber.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onNavigate("profile") }
                            .padding(if (isExpanded) 10.dp else 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isExpanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeonPurple.copy(alpha = 0.3f))
                                        .border(1.dp, NeonPurple, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isProUser) Icons.Default.Diamond else Icons.Default.Person,
                                        contentDescription = "الملف الشخصي",
                                        tint = if (isProUser) NeonAmber else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = userProfile?.name?.ifBlank { "مستخدم NEXA" } ?: "مستخدم NEXA",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isProUser) "VIP مفعّل 👑" else "المستوى العادي",
                                        color = if (isProUser) NeonAmber else Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        } else {
                            Icon(
                                imageVector = if (isProUser) Icons.Default.Diamond else Icons.Default.Person,
                                contentDescription = "حسابي",
                                tint = if (isProUser) NeonAmber else Color.LightGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarNavItemCell(
    item: MainSidebarNavItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    onSelect: () -> Unit
) {
    val activeBrush = Brush.horizontalGradient(
        listOf(NeonCyan.copy(alpha = 0.35f), NeonPurple.copy(alpha = 0.2f))
    )

    val modifierWithBackground = if (isSelected) {
        Modifier
            .background(activeBrush)
            .border(
                1.dp,
                NeonCyan.copy(alpha = 0.6f),
                RoundedCornerShape(12.dp)
            )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(modifierWithBackground)
            .clickable { onSelect() }
            .testTag("nav_item_${item.route}"),
        contentAlignment = Alignment.Center
    ) {
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgedBox(
                    badge = {
                        if (item.badgeCount > 0) {
                            Badge(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ) {
                                Text("${item.badgeCount}")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) NeonCyan else Color.LightGray,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = item.title,
                    color = if (isSelected) Color.White else Color.LightGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            BadgedBox(
                badge = {
                    if (item.badgeCount > 0) {
                        Badge(
                            containerColor = NeonPink,
                            contentColor = Color.White
                        ) {
                            Text("${item.badgeCount}")
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (isSelected) NeonCyan else Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun MainContentHost(
    currentRoute: String,
    viewModel: MajarrahViewModel,
    onNavigateToRoute: (String) -> Unit,
    onOpenChat: () -> Unit
) {
    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            (fadeIn(androidx.compose.animation.core.tween(220)) + slideInHorizontally { it / 10 }) togetherWith
                    (fadeOut(androidx.compose.animation.core.tween(180)) + slideOutHorizontally { -it / 10 })
        },
        label = "MainContentRouteTransition"
    ) { route ->
        when (route) {
            "home" -> {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenChat = onOpenChat,
                    onOpenServicesMenu = { onNavigateToRoute("services") },
                    onNavigateToProduct = { onNavigateToRoute("store") },
                    onNavigateToReels = { onNavigateToRoute("reels") }
                )
            }

            "friends" -> {
                SocialScreen(
                    viewModel = viewModel,
                    onOpenChatWithFriend = { convId ->
                        viewModel.selectConversation(convId)
                        onOpenChat()
                    },
                    onNavigateToReels = {
                        onNavigateToRoute("reels")
                    }
                )
            }

            "chat" -> {
                ChatScreen(
                    viewModel = viewModel,
                    onBackClick = { onNavigateToRoute("home") }
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
                            onOpenChat()
                        } else {
                            onNavigateToRoute(key)
                        }
                    }
                )
            }

            "profile" -> {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToPrivacyAndSupport = {
                        onNavigateToRoute("privacy_and_support")
                    }
                )
            }

            "privacy_and_support" -> {
                PrivacyPolicyAndSupportScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        onNavigateToRoute("profile")
                    }
                )
            }

            "support_and_report" -> {
                SupportAndReportScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        onNavigateToRoute("services")
                    }
                )
            }

            else -> {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenChat = onOpenChat,
                    onOpenServicesMenu = { onNavigateToRoute("services") },
                    onNavigateToProduct = { onNavigateToRoute("store") },
                    onNavigateToReels = { onNavigateToRoute("reels") }
                )
            }
        }
    }
}
