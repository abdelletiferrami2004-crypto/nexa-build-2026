package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserProfile
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

data class ActiveContactItem(
    val id: String,
    val conversationId: String,
    val name: String,
    val avatarUrl: String = "",
    val note: String = "",
    val isAi: Boolean = false,
    val isOnline: Boolean = true
)

/**
 * Modern Facebook Messenger style Horizontal Active Contacts Bar (Stories & Notes)
 */
@Composable
fun MessengerActiveContactsBar(
    userProfile: UserProfile?,
    onAddNoteOrStory: () -> Unit,
    onSelectActiveUser: (String) -> Unit,
    onOpenAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeContacts = listOf(
        ActiveContactItem(
            id = "ai_nexa",
            conversationId = "nexa_ai",
            name = "ذكاء NEXA",
            note = "جاهز للمساعدة 🤖",
            isAi = true
        ),
        ActiveContactItem(
            id = "user_1",
            conversationId = "conv_1",
            name = "سارة النمر",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            note = "مشروع مجرة الجديد 🚀"
        ),
        ActiveContactItem(
            id = "user_2",
            conversationId = "conv_2",
            name = "فيصل العتيبي",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            note = "متاح للدردشة ✨"
        ),
        ActiveContactItem(
            id = "user_3",
            conversationId = "conv_3",
            name = "نورا القحطاني",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
            note = "أستمع للموسيقى 🎵"
        ),
        ActiveContactItem(
            id = "user_4",
            conversationId = "conv_4",
            name = "محمد الدوسري",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
            note = "في العمل 💻"
        ),
        ActiveContactItem(
            id = "user_5",
            conversationId = "conv_5",
            name = "ريم الخالد",
            avatarUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
            note = "رحلة برية 🌴"
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. "Your Story / Add Note" Item
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(62.dp)
                        .clickable { onAddNoteOrStory() }
                ) {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Avatar or Default User
                        if (!userProfile?.avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = userProfile?.avatarUrl,
                                contentDescription = "Your Story",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF334155), Color(0xFF1E293B))
                                        )
                                    )
                                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        // Plus Badge at Bottom End
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(NeonCyan)
                                .border(2.dp, Color(0xFF090D16), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Note",
                                tint = BackgroundDark,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ملاحظتك",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 2. Active Online Contacts & AI
            items(activeContacts) { contact ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(62.dp)
                        .clickable {
                            if (contact.isAi) {
                                onOpenAi()
                            } else {
                                onSelectActiveUser(contact.conversationId)
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Note Speech Bubble floating above avatar
                        if (contact.note.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .offset(y = (-20).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B).copy(alpha = 0.95f))
                                    .border(0.8.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = contact.note,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Avatar
                        if (contact.isAi) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(NeonCyan, NeonPurple)
                                        )
                                    )
                                    .border(1.5.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI",
                                    tint = BackgroundDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else if (contact.avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = contact.avatarUrl,
                                contentDescription = contact.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        // Glowing Green Online Dot
                        if (contact.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(13.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(EncryptedGreen)
                                    .border(2.dp, Color(0xFF090D16), CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = contact.name.split(" ").firstOrNull() ?: contact.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
