package com.example.data.model

data class ModerationReportResult(
    val contentId: String,
    val targetAuthorName: String,
    val category: String,
    val isViolationVerified: Boolean,
    val aiConfidence: Int = 98,
    val aiReason: String,
    val actionTaken: String,
    val timestamp: Long = System.currentTimeMillis()
)
