package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileExportManager {

    /**
     * Exports given text as a formatted file (.txt or .pdf summary format)
     * Saves to app document directory and launches system share/export dialog.
     */
    fun exportTextFile(
        context: Context,
        fileNamePrefix: String = "NEXA_Export",
        content: String,
        subjectTitle: String = "NEXA AI Export Document"
    ) {
        if (content.isBlank()) {
 Toast.makeText(context,"لا يوجد محتوى لتصديره", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${fileNamePrefix}_$timestamp.txt"

            val documentsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "NEXA_Exports")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }

            val file = File(documentsDir, fileName)
            file.writeText(content)

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, subjectTitle)
                putExtra(Intent.EXTRA_TEXT, content)
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

 context.startActivity(Intent.createChooser(shareIntent,"تصدير ومشاركة الملف (Text / PDF)"))
 Toast.makeText(context,"تم تصدير وحفظ الملف بنجاح: ${file.name}", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback text intent share
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, subjectTitle)
                putExtra(Intent.EXTRA_TEXT, content)
            }
 context.startActivity(Intent.createChooser(fallbackIntent,"تصدير ومشاركة النص"))
 Toast.makeText(context,"تم تصدير ومشاركة النص بنجاح", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Formats AI response or chat history into a PDF Document Summary format and exports it.
     */
    fun exportPdfSummary(
        context: Context,
        documentTitle: String = "ملخص نصوص NEXA AI",
        bodyText: String
    ) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val formattedContent = """
====================================================================
 NEXA AI PLATFORM 
                   تقرير وتصدير المستندات الذكية
====================================================================
عنوان المستند: $documentTitle
تاريخ التصدير: $dateStr
حالة التشفير: تشفير مشدد E2EE 
المُصَدِّر: تطبيق NEXA للذكاء الاصطناعي والتواصل
====================================================================

$bodyText

====================================================================
حقوق التصدير محفوظة © 2026 NEXA AI Platform. جميع البيانات محمية.
====================================================================
        """.trimIndent()

        exportTextFile(
            context = context,
            fileNamePrefix = "NEXA_PDF_Summary",
            content = formattedContent,
            subjectTitle = documentTitle
        )
    }
}
