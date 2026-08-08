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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
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
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

data class NexaPage(
    val id: String,
    val name: String,
    val category: String,
    val followers: String,
    val reach: String,
    val engagementRate: String,
    val isVerified: Boolean = true,
    val userRole: String = "المالك (Owner)"
)

data class AdminRoleUser(
    val name: String,
    val handle: String,
    var role: String,
    var canPublish: Boolean = true,
    var canManageAds: Boolean = true
)

@Composable
fun NexaPagesAndBusinessModal(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: My Pages, 1: Create Page, 2: Roles Manager, 3: Analytics
    var showSuccessToast by remember { mutableStateOf<String?>(null) }

    val pages = remember {
        mutableStateListOf(
            NexaPage("p1", "متجر NEXA التقني", "تجارة إلكترونية وتقنية", "142.5K", "1.2M", "18.4%"),
            NexaPage("p2", "أكاديمية الذكاء الاصطناعي", "تعليم وتكنولوجيا", "89.2K", "640K", "22.1%"),
            NexaPage("p3", "مجلة مجرة الرقمية", "إعلام وصناعة محتوى", "210.8K", "3.4M", "15.9%")
        )
    }

    var activePage by remember { mutableStateOf(pages[0]) }

    // Page creation inputs
    var newPageName by remember { mutableStateOf("") }
    var newPageCategory by remember { mutableStateOf("تجارة إلكترونية") }
    var newPageBio by remember { mutableStateOf("") }

    // Admin roles list for active page
    val adminRoles = remember {
        mutableStateListOf(
            AdminRoleUser("عبداللطيف القحطاني", "@abdelletif", "مالك الصفحة (Owner)"),
            AdminRoleUser("سارة الشمري", "@sara_designer", "محرر محتوى (Editor)"),
            AdminRoleUser("أحمد الكندي", "@ahmed_mod", "مشرف محادثات (Moderator)")
        )
    }

    var newMemberHandle by remember { mutableStateOf("") }
    var newMemberRole by remember { mutableStateOf("محرر محتوى (Editor)") }

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
                                .background(NeonPurple.copy(alpha = 0.2f))
                                .border(1.dp, NeonPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BusinessCenter,
                                contentDescription = "Pages Suite",
                                tint = NeonPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "إدارة الصفحات والأعمال (Meta Suite)",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "NEXA Business & Pages Hub",
                                color = NeonCyan,
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

                // Navigation Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "صفحاتي (My Pages)",
                        "إنشاء صفحة",
                        "أدوار الأدمن",
                        "تحليلات الأداء"
                    )
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) NeonPurple.copy(alpha = 0.3f)
                                    else CardBackground
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonPurple else CardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedTab = index }
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

                showSuccessToast?.let { toastMsg ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = NeonCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = toastMsg, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                when (selectedTab) {
                    0 -> {
                        // My Pages List & Dashboard Summary
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "صفحاتك العامة وإحصائياتها الحية:",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(pages) { page ->
                                PageItemCard(
                                    page = page,
                                    isSelected = activePage.id == page.id,
                                    onSelect = { activePage = page },
                                    onManageRoles = {
                                        activePage = page
                                        selectedTab = 2
                                    },
                                    onViewAnalytics = {
                                        activePage = page
                                        selectedTab = 3
                                    }
                                )
                            }
                        }
                    }

                    1 -> {
                        // Create New Page Workflow
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CardBackground, RoundedCornerShape(16.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "إنشاء صفحة تجارية أو عامة جديدة 🚀",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = newPageName,
                                onValueChange = { newPageName = it },
                                label = { Text("اسم الصفحة / العلامة التجارية", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            OutlinedTextField(
                                value = newPageCategory,
                                onValueChange = { newPageCategory = it },
                                label = { Text("فئة النشاط (تجارة، تقنية، تعليم...)", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            OutlinedTextField(
                                value = newPageBio,
                                onValueChange = { newPageBio = it },
                                label = { Text("نبذة مختصرة عن الصفحة والخدمات المقدمة", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    if (newPageName.isNotBlank()) {
                                        val created = NexaPage(
                                            id = "p_${System.currentTimeMillis()}",
                                            name = newPageName,
                                            category = newPageCategory,
                                            followers = "1",
                                            reach = "100",
                                            engagementRate = "100%",
                                            isVerified = true
                                        )
                                        pages.add(0, created)
                                        activePage = created
                                        showSuccessToast = "تم إنشاء صفحة '${newPageName}' بنجاح وتفعيل الشارة الموثقة! 🎉"
                                        newPageName = ""
                                        newPageBio = ""
                                        selectedTab = 0
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("نشر وتأكيد إنشاء الصفحة الرسمية", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    2 -> {
                        // Admin Roles Manager (Owner, Editor, Moderator)
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
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "إدارة الأدمن والصلحيات للصفحة الحالية: ${activePage.name}",
                                            color = NeonCyan,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "تعيين صلاحيات النشر، الموديريتور، وإدارة الحملات الإعلانية لموظفيك.",
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CardBackground, RoundedCornerShape(12.dp))
                                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("إضافة عضو أو مشرف جديد:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = newMemberHandle,
                                            onValueChange = { newMemberHandle = it },
                                            placeholder = { Text("@اسم_المستخدم", color = Color.Gray) },
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = NeonPurple,
                                                unfocusedBorderColor = CardBorder,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            )
                                        )
                                        Button(
                                            onClick = {
                                                if (newMemberHandle.isNotBlank()) {
                                                    adminRoles.add(
                                                        AdminRoleUser(
                                                            name = newMemberHandle.removePrefix("@"),
                                                            handle = if (newMemberHandle.startsWith("@")) newMemberHandle else "@$newMemberHandle",
                                                            role = newMemberRole
                                                        )
                                                    )
                                                    showSuccessToast = "تمت إضافة $newMemberHandle كـ $newMemberRole"
                                                    newMemberHandle = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("إضافة", color = Color.White)
                                        }
                                    }
                                }
                            }

                            items(adminRoles) { user ->
                                AdminUserRoleCard(
                                    user = user,
                                    onRoleChanged = { updatedRole ->
                                        user.role = updatedRole
                                        showSuccessToast = "تم تحديث دور ${user.name} إلى $updatedRole"
                                    }
                                )
                            }
                        }
                    }

                    3 -> {
                        // Page Performance & Growth Analytics
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                AnalyticsOverviewHeader(page = activePage)
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
                                            Text("معدل التفاعل والوصول الأسبوعي", color = Color.White, fontWeight = FontWeight.Bold)
                                            Text("+24.8% 📈", color = NeonCyan, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        // Visual chart simulation bars
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            val heights = listOf(0.4f, 0.6f, 0.35f, 0.85f, 0.7f, 0.95f, 0.8f)
                                            val days = listOf("سبت", "أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة")
                                            heights.forEachIndexed { i, h ->
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Bottom,
                                                    modifier = Modifier.fillMaxHeight()
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(18.dp)
                                                            .fillMaxHeight(h)
                                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                            .background(if (i == 5) NeonCyan else NeonPurple.copy(alpha = 0.6f))
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(days[i], color = Color.Gray, fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
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
fun PageItemCard(
    page: NexaPage,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onManageRoles: () -> Unit,
    onViewAnalytics: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NeonPurple.copy(alpha = 0.15f) else CardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonPurple else CardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = NeonPurple)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = page.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (page.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = NeonCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(text = "${page.category} • ${page.userRole}", color = Color.Gray, fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = page.followers + " متابع", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "الوصول (Reach)", color = Color.Gray, fontSize = 10.sp)
                    Text(text = page.reach, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "معدل التفاعل", color = Color.Gray, fontSize = 10.sp)
                    Text(text = page.engagementRate, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewAnalytics,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("التحليلات", color = Color.White, fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = onManageRoles,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("الأدمن والصلحيات", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUserRoleCard(
    user: AdminRoleUser,
    onRoleChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            .background(NeonPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = user.handle, color = Color.Gray, fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = user.role, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "صلاحية نشر المنشورات والحملات", color = Color.Gray, fontSize = 11.sp)
                Switch(
                    checked = user.canPublish,
                    onCheckedChange = { user.canPublish = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                )
            }
        }
    }
}

@Composable
fun AnalyticsOverviewHeader(page: NexaPage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("إجمالي المتابعين", color = Color.Gray, fontSize = 11.sp)
                Text(page.followers, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("+1,240 هذا الأسبوع", color = Color.Green, fontSize = 10.sp)
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("الوصول الإجمالي", color = Color.Gray, fontSize = 11.sp)
                Text(page.reach, color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("+18.4% نمو", color = NeonCyan, fontSize = 10.sp)
            }
        }
    }
}
