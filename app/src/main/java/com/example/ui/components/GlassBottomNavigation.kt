package com.example.ui.components

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

enum class BottomTab {
    HOME,
    FRIENDS,
    REELS,
    MARKETPLACE,
    MENU
}

@Composable
fun GlassBottomNavigation(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    onFloatingAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Glass Bar Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(24.dp, RoundedCornerShape(34.dp), spotColor = NeonPurple.copy(alpha = 0.5f))
                .clip(RoundedCornerShape(34.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC1A1238),
                            Color(0xEE0D0820)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.5f),
                            GlassBorderDark,
                            NeonPurple.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(34.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: الرئيسية
                NavItem(
                    label = "الرئيسية",
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == BottomTab.HOME,
                    onClick = { onTabSelected(BottomTab.HOME) }
                )

                // Tab 2: الأصدقاء
                NavItem(
                    label = "الأصدقاء",
                    icon = Icons.Default.People,
                    isSelected = selectedTab == BottomTab.FRIENDS,
                    onClick = { onTabSelected(BottomTab.FRIENDS) }
                )

                // Space for Center Floating Glass (+) Button
                Spacer(modifier = Modifier.size(52.dp))

                // Tab 3: الريلز
                NavItem(
                    label = "الريلز",
                    icon = Icons.Default.Movie,
                    isSelected = selectedTab == BottomTab.REELS,
                    onClick = { onTabSelected(BottomTab.REELS) }
                )

                // Tab 4: المتجر
                NavItem(
                    label = "المتجر",
                    icon = Icons.Default.ShoppingBag,
                    isSelected = selectedTab == BottomTab.MARKETPLACE,
                    onClick = { onTabSelected(BottomTab.MARKETPLACE) }
                )

 // Tab 5: قائمة الخدمات 
                NavItem(
 label ="الخدمات",
                    icon = Icons.Default.Menu,
                    isSelected = selectedTab == BottomTab.MENU,
                    onClick = { onTabSelected(BottomTab.MENU) }
                )
            }
        }

        // Center Floating Glass Add (+) Button
        Box(
            modifier = Modifier
                .offset(y = (-18).dp)
                .size(58.dp)
                .shadow(16.dp, CircleShape, spotColor = NeonCyan)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonCyan, NeonPurple)
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                .clickable { onFloatingAddClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Post / Story",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) NeonCyan else Color.Gray,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (isSelected) NeonCyan else Color.Gray,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
