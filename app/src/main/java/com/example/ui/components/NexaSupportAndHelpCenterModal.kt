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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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

data class FaqItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    var isExpanded: Boolean = false
)

@Composable
fun NexaSupportAndHelpCenterModal(
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0: الأسئلة الشائعة FAQ, 1: تقديم تذكرة دعم
    var searchQuery by remember { mutableStateOf("") }
    var supportSubject by remember { mutableStateOf("") }
    var supportMessage by remember { mutableStateOf("") }
    var supportCategory by remember { mutableStateOf("مشكلة تقنية") }
    var showTicketSuccess by remember { mutableStateOf<String?>(null) }

    val faqs = remember {
        mutableStateListOf(
            FaqItem(
                "f1",
                "كيف أستفيد من ميزات الذكاء الاصطناعي NEXA AI؟",
                "يمكنك التفاعل مع مساعد NEXA AI مباشرة في غرف الدردشة، منشورات مجرة، وتحسين الصور والريلز بنقرة زر واحدة عبر الأزرار المتاحة في الواجهة.",
                "الذكاء الاصطناعي"
            ),
            FaqItem(
                "f2",
                "كيف أستلم أرباح الإعلانات وصناعة المحتوى؟",
                "انتقل إلى مركز الإعلانات والأرباح من قائمة الخدمات، وقم بربط حسابك البنكي أو PayPal لسحب أرباحك عند الوصول للحد الأدنى (50$).",
                "الأرباح والتحويلات"
            ),
            FaqItem(
                "f3",
                "ما هي معايير حماية الخصوصية والمصادقة الثنائية (2FA)؟",
                "نحن نستخدم تشفير خوارزميات AES-256 والمصادقة الثنائية لحماية كافة الجلسات. يمكنك إدارة أجهزتك النشطة وتفعيل 2FA من إعدادات الأمان.",
                "الأمان والخصوصية"
            ),
            FaqItem(
                "f4",
                "كيف يمكنني إنشاء صفحة أعمال أو شركة في NEXA؟",
                "من قسم الخدمات -> إدارة الصفحات والأعمال، انقر على 'إنشاء صفحة جديدة' لتوثيق علامتك التجارية والبدء بإدارة الأدوار والتحليلات.",
                "الصفحات والأعمال"
            )
        )
    }

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
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = "Help Center",
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مركز المساعدة والدعم الفني 🎧",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "NEXA Help Center & Support Tickets",
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

                // Tabs Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf("الأسئلة الشائعة (FAQ)", "إرسال تذكرة دعم مباشر")
                    tabs.forEachIndexed { index, title ->
                        val isSel = activeTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) NeonCyan.copy(alpha = 0.25f) else CardBackground)
                                .border(1.dp, if (isSel) NeonCyan else CardBorder, RoundedCornerShape(12.dp))
                                .clickable { activeTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSel) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                showTicketSuccess?.let { toast ->
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
                            Text(toast, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                when (activeTab) {
                    0 -> {
                        // FAQ Search and List
                        Column(modifier = Modifier.fillMaxSize()) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("ابحث في الأسئلة الشائعة...", color = Color.Gray, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val filteredFaqs = faqs.filter {
                                searchQuery.isBlank() || it.question.contains(searchQuery, ignoreCase = true) || it.answer.contains(searchQuery, ignoreCase = true)
                            }

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredFaqs) { faq ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                faq.isExpanded = !faq.isExpanded
                                            },
                                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = faq.question,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Icon(
                                                    imageVector = if (faq.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                    contentDescription = null,
                                                    tint = NeonCyan
                                                )
                                            }

                                            AnimatedVisibility(visible = faq.isExpanded) {
                                                Column {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = faq.answer,
                                                        color = Color.LightGray,
                                                        fontSize = 12.sp,
                                                        lineHeight = 18.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Support Ticket Creation Form
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "تواصل مع فريق الدعم الفني المباشر (NEXA Support Ticket) 📩",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            item {
                                Text("نوع الاستفسار / المشكلة:", color = Color.Gray, fontSize = 12.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val categories = listOf("مشكلة تقنية", "الأرباح والمحفظة", "بلاغات وتوثيق")
                                    categories.forEach { cat ->
                                        val isSel = supportCategory == cat
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSel) NeonCyan.copy(alpha = 0.25f) else CardBackground)
                                                .border(1.dp, if (isSel) NeonCyan else CardBorder, RoundedCornerShape(10.dp))
                                                .clickable { supportCategory = cat }
                                                .padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(cat, color = if (isSel) Color.White else Color.Gray, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            item {
                                OutlinedTextField(
                                    value = supportSubject,
                                    onValueChange = { supportSubject = it },
                                    label = { Text("موضوع التذكرة", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = supportMessage,
                                    onValueChange = { supportMessage = it },
                                    label = { Text("تفاصيل المشكلة أو الرسالة", color = Color.Gray) },
                                    minLines = 4,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        if (supportSubject.isNotBlank() && supportMessage.isNotBlank()) {
                                            showTicketSuccess = "تم إرسال تذكرة الدعم بنجاح! رقم التذكرة #NX-${(1000..9999).random()} - سيتم الرد خلال 24 ساعة. 🚀"
                                            supportSubject = ""
                                            supportMessage = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("إرسال تذكرة الدعم الفني", color = Color.Black, fontWeight = FontWeight.Bold)
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
