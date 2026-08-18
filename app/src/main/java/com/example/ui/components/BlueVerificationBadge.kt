package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan

val BlueBadgePrimary = Color(0xFF00B2FE)
val BlueBadgeSecondary = Color(0xFF0075FF)

/**
 * Blue Verification Badge (العلامة الزرقاء الرسمية)
 * Displayed next to verified user accounts, creators, and public figures.
 */
@Composable
fun BlueVerificationBadge(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    contentDescription: String = "حساب موثق بالعلامة الزرقاء"
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(BlueBadgePrimary, BlueBadgeSecondary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size * 0.7f)
        )
    }
}

/**
 * Detailed Verification Chip with text and icon
 */
@Composable
fun BlueVerificationChip(
    modifier: Modifier = Modifier,
    label: String = "حساب موثق رسمي",
    badgeSize: Dp = 14.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BlueBadgeSecondary.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BlueVerificationBadge(size = badgeSize)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = BlueBadgePrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
