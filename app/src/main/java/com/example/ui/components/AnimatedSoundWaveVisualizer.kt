package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlin.math.sin

@Composable
fun AnimatedSoundWaveVisualizer(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    barCount: Int = 36,
    height: Dp = 90.dp,
    primaryColor: Color = NeonCyan,
    secondaryColor: Color = NeonPurple,
    accentColor: Color = NeonPink
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveAnim")
    
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )

    val energyPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val width = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2f
        val barWidth = (width / (barCount * 1.6f)).coerceAtLeast(3f)
        val gap = (width - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

        val gradientBrush = Brush.linearGradient(
            colors = listOf(primaryColor, secondaryColor, accentColor),
            start = Offset(0f, centerY),
            end = Offset(width, centerY)
        )

        // Draw background smooth sine glow path
        if (isActive) {
            val wavePath = Path()
            val step = 4f
            var x = 0f
            wavePath.moveTo(0f, centerY)
            while (x <= width) {
                val normalizedX = x / width
                val sinValue = sin((normalizedX * 4 * Math.PI + phase1).toDouble()).toFloat()
                val cosValue = sin((normalizedX * 8 * Math.PI - phase2).toDouble()).toFloat()
                val y = centerY + (sinValue * 0.5f + cosValue * 0.3f) * (canvasHeight * 0.35f) * energyPulse
                wavePath.lineTo(x, y)
                x += step
            }
            drawPath(
                path = wavePath,
                brush = gradientBrush,
                style = Stroke(width = 2.5f, cap = StrokeCap.Round),
                alpha = 0.45f
            )
        }

        // Draw Equalizer Frequency Bars
        for (i in 0 until barCount) {
            val progress = i.toFloat() / barCount.toFloat()
            val distanceFromCenter = kotlin.math.abs(0.5f - progress) * 2f
            val baseMultiplier = (1f - distanceFromCenter * 0.45f)

            val dynamicHeight = if (isActive) {
                val wave1 = (sin((progress * 6 * Math.PI + phase1).toDouble()).toFloat() + 1f) / 2f
                val wave2 = (sin((progress * 12 * Math.PI - phase2 * 1.5).toDouble()).toFloat() + 1f) / 2f
                val combined = (wave1 * 0.6f + wave2 * 0.4f) * baseMultiplier * energyPulse
                (combined * canvasHeight * 0.85f).coerceIn(8f, canvasHeight * 0.95f)
            } else {
                (canvasHeight * 0.12f * baseMultiplier).coerceAtLeast(6f)
            }

            val barX = i * (barWidth + gap)
            val barY = centerY - (dynamicHeight / 2f)

            // Neon glowing individual bar
            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(barX, barY),
                size = Size(barWidth, dynamicHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
                alpha = if (isActive) (0.55f + (dynamicHeight / canvasHeight) * 0.45f).coerceIn(0.4f, 1f) else 0.3f
            )
        }
    }
}
