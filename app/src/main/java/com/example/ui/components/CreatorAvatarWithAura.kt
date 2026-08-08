package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CreatorBadgeTier

@Composable
fun CreatorAvatarWithAura(
    followersCount: Int,
    avatarUrl: String = "",
    authorInitial: String = "A",
    size: Dp = 64.dp,
    showBadgeChip: Boolean = true,
    modifier: Modifier = Modifier
) {
    val tier = CreatorBadgeTier.fromFollowers(followersCount)

    val infiniteTransition = rememberInfiniteTransition(label = "aura_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (tier) {
            CreatorBadgeTier.DIAMOND_VIP -> {
                // VIP Diamond Aura - Legendary Neon Turquoise & Magenta Frame
                Box(
                    modifier = Modifier
                        .size(size + 14.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF00F5FF),
                                    Color(0xFFFF007F),
                                    Color(0xFFFFD700),
                                    Color(0xFF00F5FF)
                                )
                            )
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0D0D18))
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarInnerImage(
                        avatarUrl = avatarUrl,
                        authorInitial = authorInitial,
                        size = size,
                        accentColor = Color(0xFF00F5FF)
                    )
                }

                if (showBadgeChip) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00F5FF), Color(0xFFFF007F))
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
 text =" VIP",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (size.value * 0.18f).coerceAtLeast(9f).sp
                        )
                    }
                }
            }

            CreatorBadgeTier.GOLD_CRYSTAL -> {
                // 3D Crystal Gold Aura Frame
                Box(
                    modifier = Modifier
                        .size(size + 10.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500),
                                    Color(0xFFFFF59D),
                                    Color(0xFFFFD700)
                                )
                            )
                        )
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141218))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarInnerImage(
                        avatarUrl = avatarUrl,
                        authorInitial = authorInitial,
                        size = size,
                        accentColor = Color(0xFFFFD700)
                    )
                }

                if (showBadgeChip) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFD700))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
 text =" 3D Gold",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = (size.value * 0.16f).coerceAtLeast(8f).sp
                        )
                    }
                }
            }

            CreatorBadgeTier.SILVER -> {
                // Silver Metallic Glow Frame
                Box(
                    modifier = Modifier
                        .size(size + 8.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFE0E0E0),
                                    Color(0xFFB0B0B0),
                                    Color(0xFFFFFFFF)
                                )
                            )
                        )
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141218))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarInnerImage(
                        avatarUrl = avatarUrl,
                        authorInitial = authorInitial,
                        size = size,
                        accentColor = Color(0xFFC0C0C0)
                    )
                }

                if (showBadgeChip) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFC0C0C0))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
 text =" Silver",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = (size.value * 0.15f).coerceAtLeast(8f).sp
                        )
                    }
                }
            }

            CreatorBadgeTier.NONE -> {
                // Standard Member Avatar
                AvatarInnerImage(
                    avatarUrl = avatarUrl,
                    authorInitial = authorInitial,
                    size = size,
                    accentColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun AvatarInnerImage(
    avatarUrl: String,
    authorInitial: String,
    size: Dp,
    accentColor: Color
) {
    if (avatarUrl.isNotBlank()) {
        Image(
            painter = painterResource(id = R.drawable.nexa_3d_icon_1785719681308),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(accentColor.copy(alpha = 0.4f), Color(0xFF181828))
                    )
                )
                .border(1.dp, accentColor.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = authorInitial.take(1).ifEmpty { "A" },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.42f).sp
            )
        }
    }
}
