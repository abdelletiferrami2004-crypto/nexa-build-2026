package com.example.util

object NexaSafetyModerator {

    enum class SafetyLevel {
        SAFE,
        WARNING_PROMOTIONAL,
        DANGEROUS_PHISHING,
        DANGEROUS_HARMFUL,
        BLOCKED_MALWARE
    }

    data class ModerationResult(
        val isAllowed: Boolean,
        val safetyLevel: SafetyLevel,
        val reason: String,
        val sanitizedText: String,
        val flaggedUrl: String? = null,
        val actionTaken: String // "approved", "warned", "quarantined", "blocked"
    )

    private val suspiciousDomains = listOf(
        "free-crypto-giveaway.xyz",
        "nexa-login-verify.top",
        "claim-free-vip-coins.ru",
        "bank-security-update.click",
        "phishing-test-domain.biz",
        "free-giftcard-now.club"
    )

    private val harmfulKeywordsArabic = listOf(
        "احتيال",
        "سرقة حساب",
        "كلمة المرور مجانا",
        "رابط اختراق",
        "بطاقة بنكية مسروقة"
    )

    private val harmfulKeywordsEnglish = listOf(
        "free crypto giveaway",
        "send your private key",
        "hack account",
        "stolen credit card",
        "unauthorized password reset"
    )

    fun evaluateContent(text: String): ModerationResult {
        val lower = text.lowercase()

        // 1. Phishing & Malicious URL detection
        for (domain in suspiciousDomains) {
            if (lower.contains(domain)) {
                return ModerationResult(
                    isAllowed = false,
                    safetyLevel = SafetyLevel.DANGEROUS_PHISHING,
                    reason = "تم اكتشاف رابط احتيالي مشبوه ($domain) يحاول سرقة بيانات الحساب.",
                    sanitizedText = "[⚠️ تم حظر الرابط الضار بواسطة درع الأمان NEXA Guard]",
                    flaggedUrl = domain,
                    actionTaken = "blocked"
                )
            }
        }

        // Generic suspicious URL schema detection (raw ip links or unsafe schemes)
        val urlRegex = Regex("""(http[s]?://(?:[0-9]{1,3}\.){3}[0-9]{1,3}[^\s]*)""")
        val match = urlRegex.find(text)
        if (match != null) {
            return ModerationResult(
                isAllowed = false,
                safetyLevel = SafetyLevel.DANGEROUS_PHISHING,
                reason = "تم حظر الرابط لاحتوائه على عنوان IP مباشر غير موثق.",
                sanitizedText = "[⚠️ عنوان IP مباشر مشبوه تم حظره]",
                flaggedUrl = match.value,
                actionTaken = "quarantined"
            )
        }

        // 2. Harmful Keywords / Scams Check
        for (keyword in harmfulKeywordsArabic) {
            if (lower.contains(keyword)) {
                return ModerationResult(
                    isAllowed = false,
                    safetyLevel = SafetyLevel.DANGEROUS_HARMFUL,
                    reason = "المحتوى ينتهك إرشادات الأمان الرقمي ومكافحة الاحتيال في مجرة.",
                    sanitizedText = "[⚠️ محتوى تم حظره لانتهاكه معايير الأمان]",
                    actionTaken = "blocked"
                )
            }
        }

        for (keyword in harmfulKeywordsEnglish) {
            if (lower.contains(keyword)) {
                return ModerationResult(
                    isAllowed = false,
                    safetyLevel = SafetyLevel.DANGEROUS_HARMFUL,
                    reason = "Content flagged by NEXA AI moderation engine for financial scam/phishing patterns.",
                    sanitizedText = "[⚠️ Blocked due to suspicious scam keyword pattern]",
                    actionTaken = "blocked"
                )
            }
        }

        // 3. Spam / Repetitive pattern warning
        if (text.length > 50 && (lower.count { it == '!' } > 15 || lower.count { it == '?' } > 15)) {
            return ModerationResult(
                isAllowed = true,
                safetyLevel = SafetyLevel.WARNING_PROMOTIONAL,
                reason = "تم رصد علامات ترقيم ترويجية متكررة (رسالة شبه مزعجة).",
                sanitizedText = text,
                actionTaken = "warned"
            )
        }

        return ModerationResult(
            isAllowed = true,
            safetyLevel = SafetyLevel.SAFE,
            reason = "محتوى آمن ومتحقق منه بالكامل عبر درع NEXA Guard.",
            sanitizedText = text,
            actionTaken = "approved"
        )
    }
}
