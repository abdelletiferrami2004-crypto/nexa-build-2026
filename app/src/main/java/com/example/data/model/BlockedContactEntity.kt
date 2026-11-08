package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_blocked_targets")
data class BlockedContactEntity(
    @PrimaryKey val targetId: String,
    val blockedAt: Long = System.currentTimeMillis()
)
