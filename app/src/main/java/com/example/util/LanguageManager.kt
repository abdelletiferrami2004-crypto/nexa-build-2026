package com.example.util

import com.example.data.model.AppLanguage
import java.util.Locale

object LanguageManager {

    fun getSystemLanguageCode(): String {
        return Locale.getDefault().language.lowercase()
    }

    fun getEffectiveLanguage(selectedLanguage: AppLanguage): AppLanguage {
        if (selectedLanguage != AppLanguage.AUTO) {
            return selectedLanguage
        }

        // Auto-detect system language
        return when (getSystemLanguageCode()) {
            "en" -> AppLanguage.ENGLISH
            "es" -> AppLanguage.SPANISH
            "fr" -> AppLanguage.FRENCH
            "de" -> AppLanguage.GERMAN
            "zh" -> AppLanguage.CHINESE
            "ja" -> AppLanguage.JAPANESE
            "ru" -> AppLanguage.RUSSIAN
            "pt" -> AppLanguage.PORTUGUESE
            "tr" -> AppLanguage.TURKISH
            "hi" -> AppLanguage.HINDI
            "ar" -> AppLanguage.ARABIC
            else -> {
                // Default to English if non-Arabic system locale, otherwise Arabic
                if (getSystemLanguageCode().startsWith("ar")) AppLanguage.ARABIC else AppLanguage.ENGLISH
            }
        }
    }

    fun getString(key: String, lang: AppLanguage): String {
        val effective = getEffectiveLanguage(lang)
        return when (key) {
            "app_name" -> "NEXA"
            "home_title" -> when (effective) {
                AppLanguage.ENGLISH -> "NEXA Smart Platform"
                AppLanguage.SPANISH -> "Plataforma Inteligente NEXA"
                AppLanguage.FRENCH -> "Plateforme Intelligente NEXA"
                AppLanguage.GERMAN -> "NEXA Smarte Plattform"
                AppLanguage.CHINESE -> "NEXA 智能平台"
                AppLanguage.JAPANESE -> "NEXA スマートプラットフォーム"
                AppLanguage.RUSSIAN -> "Умная платформа NEXA"
                AppLanguage.PORTUGUESE -> "Plataforma Inteligente NEXA"
                AppLanguage.TURKISH -> "NEXA Akıllı Platform"
                AppLanguage.HINDI -> "NEXA स्मार्ट प्लेटफॉर्म"
                else -> "منصة NEXA الذكية"
            }
            "profile_title" -> when (effective) {
 AppLanguage.ENGLISH ->"Profile & Protection Settings"
 AppLanguage.SPANISH ->"Perfil y Ajustes de Protección"
 AppLanguage.FRENCH ->"Profil & Paramètres de Protection"
 AppLanguage.GERMAN ->"Profil & Schutz-Einstellungen"
 AppLanguage.CHINESE ->"个人资料与安全设置"
 AppLanguage.JAPANESE ->"プロフィールと保護設定"
 AppLanguage.RUSSIAN ->"Профиль и настройки защиты"
 else ->"الملف الشخصي والحماية"
            }
            "lang_settings_title" -> when (effective) {
 AppLanguage.ENGLISH ->"App Language & Auto-Detection"
 AppLanguage.SPANISH ->"Idioma de la App y Detección Automática"
 AppLanguage.FRENCH ->"Langue de l'App & Détection Auto"
 AppLanguage.GERMAN ->"App-Sprache & Automatische Erkennung"
 AppLanguage.CHINESE ->"应用语言与自动检测"
 AppLanguage.JAPANESE ->"アプリ言語と自動検出"
 AppLanguage.RUSSIAN ->"Язык приложения и автоопределение"
 else ->"لغة التطبيق والكشف التلقائي"
            }
            "system_detected" -> when (effective) {
                AppLanguage.ENGLISH -> "System Language Detected:"
                AppLanguage.SPANISH -> "Idioma del Sistema Detectado:"
                AppLanguage.FRENCH -> "Langue du Système Détectée:"
                AppLanguage.GERMAN -> "Systemsprache Erkannt:"
                AppLanguage.CHINESE -> "系统语言自动检测："
                AppLanguage.JAPANESE -> "検出されたシステム言語:"
                AppLanguage.RUSSIAN -> "Обнаружен язык системы:"
                else -> "تم الكشف تلقائياً عن لغة النظام:"
            }
            "create_story" -> when (effective) {
                AppLanguage.ENGLISH -> "Create Story"
                AppLanguage.SPANISH -> "Crear Historia"
                AppLanguage.FRENCH -> "Créer une Story"
                AppLanguage.GERMAN -> "Story Erstellen"
                AppLanguage.CHINESE -> "发布动态"
                AppLanguage.JAPANESE -> "ストーリー作成"
                AppLanguage.RUSSIAN -> "Создать историю"
                else -> "إنشاء ستوري"
            }
            "crowns_and_comments" -> when (effective) {
 AppLanguage.ENGLISH ->"Crowns & Comments"
 AppLanguage.SPANISH ->"Coronas y Comentarios"
 AppLanguage.FRENCH ->"Couronnes & Commentaires"
 AppLanguage.GERMAN ->"Kronen & Kommentare"
 AppLanguage.CHINESE ->"皇冠与评论"
 AppLanguage.JAPANESE ->"クラウンとコメント"
 AppLanguage.RUSSIAN ->"Короны и комментарии"
 else ->"التيجان والتعليقات"
            }
            "teen_mode_title" -> when (effective) {
 AppLanguage.ENGLISH ->"Teen Protection Mode"
 AppLanguage.SPANISH ->"Modo Protección Juvenil"
 AppLanguage.FRENCH ->"Mode Protection Ados"
 AppLanguage.GERMAN ->"Jugendschutz-Modus"
 AppLanguage.CHINESE ->"青少年保护模式"
 AppLanguage.JAPANESE ->"ティーン保護モード"
 AppLanguage.RUSSIAN ->"Режим защиты подростков"
 else ->"تفعيل وضع الناشئة"
            }
            "ai_assistant" -> when (effective) {
 AppLanguage.ENGLISH ->"NEXA AI Assistant"
 AppLanguage.SPANISH ->"Asistente NEXA AI"
 AppLanguage.FRENCH ->"Assistant NEXA AI"
 AppLanguage.GERMAN ->"NEXA AI Assistent"
 AppLanguage.CHINESE ->"NEXA AI 助手"
 AppLanguage.JAPANESE ->"NEXA AI アシスタント"
 AppLanguage.RUSSIAN ->"NEXA AI Ассистент"
 else ->"ذكاء NEXA AI"
            }
            "pin_chat" -> when (effective) {
 AppLanguage.ENGLISH ->"PIN Encrypted Vault"
 AppLanguage.SPANISH ->"Bóveda Cifrada por PIN"
 AppLanguage.FRENCH ->"Coffre Chiffré par PIN"
 AppLanguage.GERMAN ->"PIN Verschlüsselter Tresor"
 else ->"رمز PIN للدردشة المشفرة"
            }
            else -> key
        }
    }
}
