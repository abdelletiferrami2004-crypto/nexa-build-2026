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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun DirectCheckoutModal(
    product: Product,
    userWalletBalance: Double,
    onDismiss: () -> Unit,
    onConfirmPurchase: (quantity: Int, paymentMethod: String) -> Boolean
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedPaymentMethod by remember { mutableStateOf("محفظة مجرة الرقمية") }
    var orderSuccessReceipt by remember { mutableStateOf<String?>(null) }

    val unitPrice = product.price
    val totalCost = unitPrice * quantity

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = BackgroundDark.copy(alpha = 0.95f),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                if (orderSuccessReceipt != null) {
                    // Success View
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(EncryptedGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(48.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

 Text("تم الشراء المباشر بنجاح!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("رقم الشحنة: $orderSuccessReceipt", color = NeonCyan, fontWeight = FontWeight.Medium, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("المنتج:", color = Color.Gray, fontSize = 12.sp)
                                    Text(product.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("المبلغ الإجمالي:", color = Color.Gray, fontSize = 12.sp)
                                    Text("${totalCost.toInt()} ${product.currency}", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("حالة الطلب:", color = Color.Gray, fontSize = 12.sp)
 Text("قيد التجهيز والتوصيل خلال 24 ساعة", color = EncryptedGreen, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("العودة للمتجر", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Checkout View
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
 Text("الشراء المباشر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Product Header Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${unitPrice.toInt()} ${product.currency}", color = NeonCyan, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }

                        // Quantity selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { if (quantity > 1) quantity-- }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("-", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Text("$quantity", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))

                            Box(
                                modifier = Modifier
                                    .clickable { quantity++ }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Method Selector
                    Text("اختر طريقة الدفع الفوري:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    val methods = listOf(
                        Triple("محفظة مجرة الرقمية", "الرصيد: ${userWalletBalance.toInt()} ر.س", Icons.Default.AccountBalanceWallet),
                        Triple("Apple Pay ", "دفع آمن بلمسة واحدة", Icons.Default.CreditCard),
                        Triple("بطاقة مدى mada", "خصم مباشر من حسابك", Icons.Default.CreditCard)
                    )

                    methods.forEach { (name, desc, icon) ->
                        val isSelected = selectedPaymentMethod == name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(14.dp))
                                .clickable { selectedPaymentMethod = name }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null, tint = if (isSelected) NeonCyan else Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(desc, color = Color.LightGray, fontSize = 10.sp)
                                }
                            }

                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Shipping Address
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("عنوان التوصيل:", color = Color.Gray, fontSize = 10.sp)
 Text("الرياض، المملكة العربية السعودية (توصيل مجاني )", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Confirm Direct Purchase Button
                    Button(
                        onClick = {
                            val success = onConfirmPurchase(quantity, selectedPaymentMethod)
                            if (success) {
                                val randomNum = (10000..99999).random()
                                orderSuccessReceipt = "MJ-$randomNum"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = BackgroundDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "تأكيد الشراء الفوري (${totalCost.toInt()} ${product.currency})",
                                color = BackgroundDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
