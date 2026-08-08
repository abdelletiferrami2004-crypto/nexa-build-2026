package com.example.data.model

enum class AppLanguage(
    val code: String,
    val displayNameNative: String,
    val flagEmoji: String
) {
 AUTO("AUTO","تلقائي (لغة الجهاز) / Auto (System)",""),
 ARABIC("AR","العربية (Arabic)",""),
 ENGLISH("EN","English",""),
 SPANISH("ES","Español (Spanish)",""),
 FRENCH("FR","Français (French)",""),
 GERMAN("DE","Deutsch (German)",""),
 CHINESE("ZH","中文 (Chinese)",""),
 JAPANESE("JA","日本語 (Japanese)",""),
 RUSSIAN("RU","Русский (Russian)",""),
 PORTUGUESE("PT","Português (Portuguese)",""),
 TURKISH("TR","Türkçe (Turkish)",""),
 HINDI("HI","हिन्दी (Hindi)","")
}
