package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val price: Double,
    val currency: String = "ر.س",
    val category: String,
    val imageUrl: String,
    val rating: Float = 4.8f,
    val isTeenFriendly: Boolean = true,
    val isFeatured: Boolean = false,
    val description: String = ""
)
