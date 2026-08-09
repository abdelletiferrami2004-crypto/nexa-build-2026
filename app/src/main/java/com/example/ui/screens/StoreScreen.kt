package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.MajarrahViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TeenProtectionCyan

@Composable
fun StoreScreen(
    viewModel: MajarrahViewModel,
    onProductSelected: (Product) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()

    val isTeenMode = profile?.isTeenMode ?: true

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }
    var directCheckoutProduct by remember { mutableStateOf<Product?>(null) }

    val categories = listOf(
        "الكل",
        "إلكترونيات",
        "أزياء",
        "ألعاب وتعليم",
        "عطور فاخرة"
    )

    // Direct Buy Checkout Dialog
    directCheckoutProduct?.let { prod ->
        com.example.ui.components.DirectCheckoutModal(
            product = prod,
            userWalletBalance = walletBalance,
            onDismiss = {
                directCheckoutProduct = null
            },
            onConfirmPurchase = { qty, paymentMethod ->
                viewModel.purchaseProductDirectly(
                    prod,
                    qty,
                    paymentMethod
                )
            }
        )
    }

    // Filter products
    val availableProducts = products.filter { product ->
        val matchesCategory =
            selectedCategory == "الكل" ||
                    product.category == selectedCategory

        val matchesSearch =
            searchQuery.isBlank() ||
                    product.title.contains(
                        searchQuery,
                        ignoreCase = true
                    )

        val matchesTeenFilter =
            if (isTeenMode) {
                product.isTeenFriendly
            } else {
                true
            }

        matchesCategory &&
                matchesSearch &&
                matchesTeenFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "متجر مجرة Marketplace",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "الرصيد: ${walletBalance.toInt()} ر.س | شحن مباشر سريع",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonCyan
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        NeonPurple.copy(alpha = 0.2f)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Cart",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = "${cartItems.size}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Teen Protection Alert Banner
        if (isTeenMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        TeenProtectionCyan.copy(alpha = 0.15f)
                    )
                    .border(
                        1.dp,
                        TeenProtectionCyan.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Teen Shield",
                        tint = TeenProtectionCyan,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "وضع الناشئة مفعّل: تظهر فقط المنتجات المعتمدة والآمنة للشباب",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            placeholder = {
                Text(
                    text = "ابحث في متجر مجرة...",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NeonCyan
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(
                    alpha = 0.2f
                ),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Category Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(4).forEach { cat ->

                val isSelected =
                    selectedCategory == cat

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) {
                                NeonCyan
                            } else {
                                Color.White.copy(alpha = 0.08f)
                            }
                        )
                        .clickable {
                            selectedCategory = cat
                        }
                        .padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        )
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) {
                            BackgroundDark
                        } else {
                            Color.White
                        },
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                bottom = 100.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(availableProducts) { product ->

                ProductGridItem(
                    product = product,
                    onAddToCart = {
                        viewModel.addToCart(product)
                    },
                    onDirectBuy = {
                        directCheckoutProduct = product
                    },
                    onClick = {
                        onProductSelected(product)
                    }
                )
            }
        }
    }
}

/**
 * Product Grid Item
 *
 * IMPORTANT:
 * Keep only ONE ProductGridItem function in this file.
 */
@Composable
fun ProductGridItem(
    product: Product,
    onAddToCart: () -> Unit,
    onDirectBuy: () -> Unit,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Color.White.copy(alpha = 0.05f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.isFeatured) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    ) {
                        GlassBadge(
                            text = "مميز",
                            accentColor = NeonAmber
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // Rating & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.category,
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = NeonAmber,
                        modifier = Modifier.size(12.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(2.dp)
                    )

                    Text(
                        text = "${product.rating}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // Title
            Text(
                text = product.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // Price & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "${product.price.toInt()} ${product.currency}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Direct Buy Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan)
                            .clickable {
                                onDirectBuy()
                            }
                            .padding(
                                horizontal = 6.dp,
                                vertical = 4.dp
                            )
                    ) {
                        Text(
                            text = "شراء",
                            color = BackgroundDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    // Add To Cart
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(NeonPurple)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
