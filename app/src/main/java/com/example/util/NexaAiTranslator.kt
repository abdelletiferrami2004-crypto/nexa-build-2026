package com.example.util

import com.example.data.remote.GeminiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NexaAiTranslator {

    enum class TargetLanguage(val code: String, val displayName: String, val flagEmoji: String) {
        ARABIC("ar", "العربية", "🇸🇦"),
        ENGLISH("en", "English", "🇬🇧"),
        FRENCH("fr", "Français", "🇫🇷")
    }

    suspend fun translateText(
        text: String,
        targetLanguage: TargetLanguage
    ): TranslationResult = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) {
            return@withContext TranslationResult(
                originalText = text,
                translatedText = text,
                sourceLanguage = "auto",
                targetLanguage = targetLanguage.code,
                confidence = 1.0f,
                isAiPowered = false,
                isSuccess = true,
                errorMessage = null
            )
        }

        val geminiResult = GeminiRepository.translateText(
            text = trimmed,
            targetLanguageName = targetLanguage.displayName
        )

        if (geminiResult.isSuccess) {
            val translated = geminiResult.getOrNull() ?: trimmed
            TranslationResult(
                originalText = text,
                translatedText = translated,
                sourceLanguage = detectSourceLanguage(trimmed),
                targetLanguage = targetLanguage.code,
                confidence = 1.0f,
                isAiPowered = true,
                isSuccess = true,
                errorMessage = null
            )
        } else {
            val errorMsg = geminiResult.exceptionOrNull()?.localizedMessage ?: "فشلت الترجمة عبر Gemini"
            TranslationResult(
                originalText = text,
                translatedText = "خطأ في الترجمة: $errorMsg",
                sourceLanguage = detectSourceLanguage(trimmed),
                targetLanguage = targetLanguage.code,
                confidence = 0.0f,
                isAiPowered = false,
                isSuccess = false,
                errorMessage = errorMsg
            )
        }
    }

    private fun detectSourceLanguage(text: String): String {
        val arabicCharCount = text.count { it in '\u0600'..'\u06FF' }
        val latinCharCount = text.count { (it in 'a'..'z') || (it in 'A'..'Z') }
        return when {
            arabicCharCount > latinCharCount -> "ar"
            text.contains("le ", ignoreCase = true) || text.contains("la ", ignoreCase = true) || text.contains("est ", ignoreCase = true) -> "fr"
            else -> "en"
        }
    }
}

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val confidence: Float,
    val isAiPowered: Boolean = false,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)
