package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan
import java.util.Locale

// Format seconds into MM:SS or HH:MM:SS
fun formatTimeDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

/**
 * 1⃣ Gentle Screen Time Break Alert Dialog (Full-Screen / Modal Popup)
 * Triggered when active screen time reaches 2 to 3 hours.
 * Displays a 5-minute grace period timer.
 */
@Composable
fun SmartScreenTimeBreakModal(
    activeHoursText: String = "ساعتان متواصلتان",
    gracePeriodSeconds: Int,
    onAcceptBreak: () -> Unit,
    onSnoozeGrace: () -> Unit,
    onTestUnlock: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, TeenProtectionCyan, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101B2B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon with Shield Glow
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(TeenProtectionCyan.copy(alpha = 0.2f))
                        .border(2.dp, TeenProtectionCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = "Rest Eyes",
                        tint = TeenProtectionCyan,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
 text ="تنبيه الاستراحة الصحية للناشئة",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mandatory Polite Warning Message
                Text(
                    text = "تنبيه: لقد أمضيت [$activeHoursText] في استخدام الشاشة المتواصل! يُرجى أخذ استراحة لراحة عينيك، والتركيز على دراستك، أو قراءة كتاب.",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 5-Minute Grace Period Countdown Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(TeenProtectionCyan.copy(alpha = 0.15f))
                        .border(1.dp, TeenProtectionCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "مهلة السماح المؤقتة (إغلاق تلقائي)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = TeenProtectionCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatTimeDuration(gracePeriodSeconds.toLong()),
                                color = TeenProtectionCyan,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Positive Guidance Tips (Rest, Study, Read)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = TeenProtectionCyan, modifier = Modifier.size(22.dp))
 Text("راحة العينين", color = Color.Gray, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(22.dp))
 Text("مذاكرة دروسك", color = Color.Gray, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Book, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
 Text("قراءة كتاب", color = Color.Gray, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Accept Break & Grace Buttons
                Button(
                    onClick = onAcceptBreak,
                    colors = ButtonDefaults.buttonColors(containerColor = TeenProtectionCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
 text ="أخذ استراحة وإغلاق التطبيق الآن",
                        color = BackgroundDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onSnoozeGrace,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("متابعة المهلة (5 دقائق)", color = Color.White, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onTestUnlock,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
 Text("فك القفل للتجربة", color = NeonCyan, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

/**
 * 2⃣ Re-Entry Warning Dialog (Anti-Abuse Protection)
 * Triggered if the user re-opens the app shortly after being forced out.
 * Shows a short 3-minute grace warning before strict 3-hour lock.
 */
@Composable
fun SmartReEntryWarningModal(
    reEntryGraceSeconds: Int,
    onExitNow: () -> Unit,
    onIgnoreTriggerLock: () -> Unit,
    onTestUnlock: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, NeonAmber, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF221805))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(NeonAmber.copy(alpha = 0.2f))
                        .border(2.dp, NeonAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Reentry Warning",
                        tint = NeonAmber,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
 text ="تحذير إعادة الدخول المبكر",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "لقد قمت بإعادة فتح التطبيق فور الخروج التلقائي! لديك مهلة استثناء قصيرة (3 دقائق) فقط قبل تفعيل القفل الشامل 3 ساعات لحماية عينيك وتركيزك.",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 3-Minute Re-Entry Warning Countdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(NeonAmber.copy(alpha = 0.15f))
                        .border(1.dp, NeonAmber, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
 text ="الوقت المتبقي قبل تفعيل قفل الـ 3 ساعات",
                            color = NeonAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatTimeDuration(reEntryGraceSeconds.toLong()),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onExitNow,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
 text ="الخروج والاستراحة الآمنة الآن",
                        color = BackgroundDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(
                        onClick = onIgnoreTriggerLock,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تفعيل القفل الآن", color = Color.White, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onTestUnlock,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
 Text("إلغاء للتجربة", color = NeonCyan, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

/**
 * 3⃣ Dedicated 3-Hour Lock Screen (`screen_time_lock = 3_hours`)
 * Full-screen restriction view displayed when 3-hour cooling lock is active.
 * Reminds user to rest and study before accessing the app again.
 */
@Composable
fun Under18ScreenTimeLockScreen(
    remainingSeconds: Long,
    onTestUnlock: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0A21),
                        Color(0xFF1B1139),
                        Color(0xFF0A0716)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Shield Lock Icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(TeenProtectionCyan.copy(alpha = 0.2f))
                    .border(2.5.dp, TeenProtectionCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock active",
                    tint = TeenProtectionCyan,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
 text ="قفل الاستراحة والتركيز الدراسي مفعل",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 21.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "تطبيقاً لقواعد حماية الناشئة والأشبال، التطبيق مقفل مؤقتاً لمساعدتك في أخذ قسط كافٍ من الراحة والتركيز على دروسك وقراءتك.",
                color = Color.LightGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Live 3-Hour Countdown Timer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, TeenProtectionCyan, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الوقت المتبقي لانتهاء قفل الـ 3 ساعات (screen_time_lock)",
                        color = TeenProtectionCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTimeDuration(remainingSeconds),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 38.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Healthy Motivational Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NeonPurple.copy(alpha = 0.2f))
                    .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Study",
                        tint = NeonCyan,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
 text ="نصيحة NEXA للناشئة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "استغل هذا الوقت لقراءة كتاب ممتع، أو المذاكرة، أو ممارسة رياضة خفيفة. عينيك وصحتك هي الأهم دائماً!",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Tester Override Button
            OutlinedButton(
                onClick = onTestUnlock,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = "Unlock Test",
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
 text ="فتح القفل مؤقتاً لأغراض التجربة",
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * 4⃣ Automatic Exit Apology Overlay
 * Appears when 5-minute grace timer expires before force closing app.
 */
@Composable
fun CoolingExitApologyModal(
    onConfirmClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, TeenProtectionCyan, RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A26))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(TeenProtectionCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cooling Exit",
                        tint = TeenProtectionCyan,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
 text ="نعتذر منك! جاري إغلاق التطبيق مؤقتاً",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "انتهت مهلة 5 الدقائق. حُفظت بياناتك وأنشطتك بسلامة، وسيتم توجيهك الآن للاستراحة والمذاكرة.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onConfirmClose,
                    colors = ButtonDefaults.buttonColors(containerColor = TeenProtectionCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
 Text("حسناً، فهمت", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
