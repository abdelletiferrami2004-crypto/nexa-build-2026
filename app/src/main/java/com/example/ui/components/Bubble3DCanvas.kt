package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bubble3D
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Bubble3DOrbit(
    bubbles: List<Bubble3D>,
    isTeenMode: Boolean,
    onBubbleClick: (Bubble3D) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredBubbles = if (isTeenMode) bubbles.filter { it.isTeenFriendly } else bubbles

    // Floating bobbing animation
    val infiniteTransition = rememberInfiniteTransition(label = "bubble_float")
    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "فقاعات مجرة 3D التفاعلية",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
 GlassBadge(text ="تطفو حياً", accentColor = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bubble Grid layout with dynamic float offsets
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = 3
        ) {
            filteredBubbles.forEachIndexed { index, bubble ->
                val floatY = if (index % 2 == 0) floatOffset1 else floatOffset2
                val floatX = sin((index + 1) * 0.8f) * 6f

                Bubble3DItem(
                    bubble = bubble,
                    offsetY = floatY,
                    offsetX = floatX,
                    onClick = { onBubbleClick(bubble) }
                )
            }
        }
    }
}

@Composable
fun Bubble3DItem(
    bubble: Bubble3D,
    offsetY: Float,
    offsetX: Float,
    onClick: () -> Unit
) {
    val primaryColor = Color(bubble.colorPrimaryHex)
    val secondaryColor = Color(bubble.colorSecondaryHex)

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .size(bubble.sizeDp.dp)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                spotColor = primaryColor.copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 3D Glass Sphere drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Outer Radial Gradient (3D Sphere Depth)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = 0.85f),
                        primaryColor.copy(alpha = 0.70f),
                        Color(0xFF0F0B24).copy(alpha = 0.95f)
                    ),
                    center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
                    radius = radius * 1.3f
                ),
                radius = radius,
                center = center
            )

            // 3D Light Specular Reflection Highlight Arc (top left glass glare)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.7f),
                        Color.White.copy(alpha = 0.0f)
                    ),
                    center = Offset(center.x - radius * 0.45f, center.y - radius * 0.45f),
                    radius = radius * 0.5f
                ),
                radius = radius * 0.45f,
                center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
            )

            // Glowing Neon Edge Rim
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        primaryColor.copy(alpha = 0.5f),
                        secondaryColor.copy(alpha = 0.8f)
                    )
                ),
                radius = radius - 1.5f,
                center = center,
                style = Stroke(width = 3f)
            )
        }

        // Content inside 3D Sphere
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            val icon = when (bubble.iconType) {
                "story" -> Icons.Default.Visibility
                "store" -> Icons.Default.ShoppingBag
                "fire" -> Icons.Default.Whatshot
                "chat" -> Icons.Default.Chat
                else -> Icons.Default.Star
            }

            Icon(
                imageVector = icon,
                contentDescription = bubble.title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = bubble.title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = bubble.category,
                color = NeonCyan,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
