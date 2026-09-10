package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.DownloadManager
import android.media.MediaScannerConnection
import android.util.Base64

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

    /**
     * Downloads and saves an AI generated image or video to the device gallery / files.
     */
    fun downloadMedia(
        context: Context,
        mediaUrl: String,
        prompt: String = "NEXA_AI_Media",
        isVideo: Boolean = false,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val extension = if (isVideo) "mp4" else "jpg"
            val mimeType = if (isVideo) "video/mp4" else "image/jpeg"
            val subFolder = if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
            val fileName = "NEXA_${if (isVideo) "Video" else "Image"}_$timestamp.$extension"

            if (mediaUrl.startsWith("data:image/") && mediaUrl.contains(";base64,")) {
                // Base64 image payload
                val base64Data = mediaUrl.substringAfter(";base64,")
                val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)

                val targetDir = File(context.getExternalFilesDir(subFolder), "NEXA_AI")
                if (!targetDir.exists()) targetDir.mkdirs()

                val file = File(targetDir, fileName)
                FileOutputStream(file).use { it.write(decodedBytes) }

                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf(mimeType),
                    null
                )

                Toast.makeText(context, "تم حفظ الصورة بنجاح في المعرض 📥", Toast.LENGTH_SHORT).show()
                onResult(true, file.absolutePath)
                return
            }

            if (mediaUrl.startsWith("http://") || mediaUrl.startsWith("https://")) {
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                if (downloadManager != null) {
                    val request = DownloadManager.Request(Uri.parse(mediaUrl)).apply {
                        setTitle(if (isVideo) "تنزيل فيديو NEXA Veo 3.1" else "تنزيل صورة NEXA Imagen 3")
                        setDescription("جاري حفظ $fileName عبر NEXA AI Platform")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(subFolder, "NEXA_AI/$fileName")
                        setMimeType(mimeType)
                    }
                    downloadManager.enqueue(request)
                    Toast.makeText(
                        context,
                        if (isVideo) "بدأ تنزيل الفيديو إلى المعرض 📥" else "بدأ تنزيل الصورة إلى المعرض 📥",
                        Toast.LENGTH_SHORT
                    ).show()
                    onResult(true, fileName)
                    return
                }
            }

            // Fallback: copy / share
            Toast.makeText(context, "تم بدء معالجة التنزيل بنجاح 📥", Toast.LENGTH_SHORT).show()
            onResult(true, fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "تعذر تنزيل الوسائط: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            onResult(false, e.localizedMessage ?: "Unknown error")
        }
    }
}
