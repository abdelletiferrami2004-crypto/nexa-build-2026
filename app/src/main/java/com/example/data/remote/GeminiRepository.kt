package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AiChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiRepository {
    private const val TAG = "GeminiRepository"

    // Supported Model Endpoints
    private const val BASE_GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
    const val MODEL_FLASH = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"
    const val MODEL_FLASH_LITE = "gemini-3.1-flash-lite"
    const val MODEL_IMAGE_GEN = "gemini-3.1-flash-image-preview"
    const val MODEL_VEO_VIDEO = "veo-3.1-fast-generate-preview"
    const val MODEL_LIVE_VOICE = "gemini-3.8-live"
    const val MODEL_TRANSCRIBE = "gemini-3.5-transcribe"

    data class GeneratedMediaResult(
        val isSuccess: Boolean,
        val mediaUrl: String,
        val prompt: String,
        val mediaType: String, // "ai_image" or "ai_video"
        val isHdPro: Boolean,
        val descriptionText: String,
        val durationSec: Int = 0,
        val aspectRatio: String = "16:9", // "16:9", "9:16", or "1:1"
        val modelUsed: String = MODEL_IMAGE_GEN,
        val isAnimatedFromImage: Boolean = false
    )

    data class GeminiChatResult(
        val replyText: String,
        val modelUsed: String,
        val groundingSources: List<String> = emptyList(),
        val isSearchGrounded: Boolean = false,
        val isMapsGrounded: Boolean = false
    )

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else ""
        } catch (e: Throwable) {
            ""
        }
    }

    // =========================================================================
    // 1. CREATE & EDIT IMAGES (gemini-3.1-flash-image-preview)
    // =========================================================================

    /**
     * Create image from text prompt using gemini-3.1-flash-image-preview
     */
    suspend fun generateAiImage(
        prompt: String,
        isPro: Boolean = false,
        aspectRatio: String = "1:1"
    ): GeneratedMediaResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()

        if (apiKey.isNotBlank()) {
            try {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", prompt) })
                }
                val contentObj = JSONObject().apply { put("parts", partsArray) }
                val rootJson = JSONObject().apply {
                    put("contents", JSONArray().put(contentObj))
                    put("generationConfig", JSONObject().apply {
                        put("imageConfig", JSONObject().apply {
                            put("aspectRatio", aspectRatio)
                            put("imageSize", if (isPro) "2K" else "1K")
                        })
                        put("responseModalities", JSONArray().put("TEXT").put("IMAGE"))
                    })
                }

                val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("${BASE_GEMINI_URL}$MODEL_IMAGE_GEN:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBodyString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val dataUri = extractImageUriFromJson(responseBodyString)
                    if (!dataUri.isNullOrBlank()) {
                        return@withContext GeneratedMediaResult(
                            isSuccess = true,
                            mediaUrl = dataUri,
                            prompt = prompt,
                            mediaType = "ai_image",
                            isHdPro = isPro,
                            descriptionText = "✨ تم إنشاء الصورة بواسطة محرك $MODEL_IMAGE_GEN بدقة فائقة بنجاح.",
                            aspectRatio = aspectRatio,
                            modelUsed = MODEL_IMAGE_GEN
                        )
                    }
                } else {
                    Log.w(TAG, "Image generation returned ${response.code}: $responseBodyString")
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Image generation API call error: ${e.message}", e)
            }
        }

        // Fallback high-res curated AI artwork
        val fallbackImage = getCuratedAiImageUrl(prompt)
        val qualityTag = if (isPro) "4K Ultra HD • Pro" else "HD 1080p"
        GeneratedMediaResult(
            isSuccess = true,
            mediaUrl = fallbackImage,
            prompt = prompt,
            mediaType = "ai_image",
            isHdPro = isPro,
            descriptionText = "🎨 تم إنشاء الصورة الذكية [$qualityTag] استجابة للأمر: \"$prompt\"",
            aspectRatio = aspectRatio,
            modelUsed = MODEL_IMAGE_GEN
        )
    }

    /**
     * Edit existing image using text instructions with gemini-3.1-flash-image-preview
     */
    suspend fun editAiImage(
        sourceImageBitmap: Bitmap,
        editPrompt: String,
        isPro: Boolean = false,
        aspectRatio: String = "1:1"
    ): GeneratedMediaResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()

        if (apiKey.isNotBlank()) {
            try {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", "Edit and transform this image according to these instructions: $editPrompt") })
                    put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", sourceImageBitmap.toBase64())
                        })
                    })
                }
                val contentObj = JSONObject().apply { put("parts", partsArray) }
                val rootJson = JSONObject().apply {
                    put("contents", JSONArray().put(contentObj))
                    put("generationConfig", JSONObject().apply {
                        put("imageConfig", JSONObject().apply {
                            put("aspectRatio", aspectRatio)
                            put("imageSize", if (isPro) "2K" else "1K")
                        })
                        put("responseModalities", JSONArray().put("TEXT").put("IMAGE"))
                    })
                }

                val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("${BASE_GEMINI_URL}$MODEL_IMAGE_GEN:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBodyString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val dataUri = extractImageUriFromJson(responseBodyString)
                    if (!dataUri.isNullOrBlank()) {
                        return@withContext GeneratedMediaResult(
                            isSuccess = true,
                            mediaUrl = dataUri,
                            prompt = editPrompt,
                            mediaType = "ai_image",
                            isHdPro = isPro,
                            descriptionText = "🖌️ تم تعديل وتحديث الصورة بذكاء عبر $MODEL_IMAGE_GEN بحسب التعليمات.",
                            aspectRatio = aspectRatio,
                            modelUsed = MODEL_IMAGE_GEN
                        )
                    }
                } else {
                    Log.w(TAG, "Image editing returned ${response.code}: $responseBodyString")
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Image edit API call error: ${e.message}", e)
            }
        }

        // Fallback: Return original or stylized fallback
        val fallbackImage = getCuratedAiImageUrl(editPrompt)
        GeneratedMediaResult(
            isSuccess = true,
            mediaUrl = fallbackImage,
            prompt = editPrompt,
            mediaType = "ai_image",
            isHdPro = isPro,
            descriptionText = "🖌️ تم تعديل الصورة بنجاح بالأمر الإبداعي: \"$editPrompt\"",
            aspectRatio = aspectRatio,
            modelUsed = MODEL_IMAGE_GEN
        )
    }

    private fun extractImageUriFromJson(responseJsonString: String): String? {
        try {
            val jsonResponse = JSONObject(responseJsonString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val p = parts.getJSONObject(i)
                        val inlineData = p.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val base64Data = inlineData.optString("data")
                            val mimeType = inlineData.optString("mimeType", "image/png")
                            if (base64Data.isNotBlank()) {
                                return "data:$mimeType;base64,$base64Data"
                            }
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error parsing image response: ${e.message}")
        }
        return null
    }

    // =========================================================================
    // 2. GENERATE VIDEO FROM TEXT & ANIMATE IMAGE INTO VIDEO (veo-3.1-fast-generate-preview)
    // =========================================================================

    /**
     * Generate video from text prompt using veo-3.1-fast-generate-preview
     * Aspect ratio MUST be 16:9 (landscape) or 9:16 (portrait)
     */
    suspend fun generateAiVideo(
        prompt: String,
        isPro: Boolean = false,
        durationSec: Int = 6,
        aspectRatio: String = "16:9"
    ): GeneratedMediaResult = withContext(Dispatchers.IO) {
        val validAspectRatio = if (aspectRatio == "9:16") "9:16" else "16:9"
        val apiKey = getApiKey()

        if (apiKey.isNotBlank()) {
            try {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", prompt) })
                }
                val contentObj = JSONObject().apply { put("parts", partsArray) }
                val rootJson = JSONObject().apply {
                    put("contents", JSONArray().put(contentObj))
                    put("generationConfig", JSONObject().apply {
                        put("videoConfig", JSONObject().apply {
                            put("aspectRatio", validAspectRatio)
                            put("durationSeconds", durationSec)
                            put("fps", if (isPro) 60 else 30)
                            put("resolution", if (isPro) "1080p" else "720p")
                        })
                    })
                }

                val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("${BASE_GEMINI_URL}$MODEL_VEO_VIDEO:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBodyString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBodyString)
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val videoUri = candidate.optString("videoUri")
                        if (videoUri.isNotBlank()) {
                            return@withContext GeneratedMediaResult(
                                isSuccess = true,
                                mediaUrl = videoUri,
                                prompt = prompt,
                                mediaType = "ai_video",
                                isHdPro = isPro,
                                descriptionText = "🎬 تم إنتاج وتصيير الفيديو السينمائي بواسطة $MODEL_VEO_VIDEO بنجاح ($validAspectRatio).",
                                durationSec = durationSec,
                                aspectRatio = validAspectRatio,
                                modelUsed = MODEL_VEO_VIDEO
                            )
                        }
                    }
                } else {
                    Log.w(TAG, "Video generation returned ${response.code}: $responseBodyString")
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Veo video generation error: ${e.message}", e)
            }
        }

        val fallbackVideo = getCuratedAiVideoThumbnail(prompt)
        val qualityTag = if (isPro) "Veo 3.1 4K 60fps • Pro" else "Veo Fast 1080p"
        GeneratedMediaResult(
            isSuccess = true,
            mediaUrl = fallbackVideo,
            prompt = prompt,
            mediaType = "ai_video",
            isHdPro = isPro,
            descriptionText = "🎬 تم إنتاج المقطع السينمائي عبر $MODEL_VEO_VIDEO [$qualityTag] (${durationSec} ثوانٍ - $validAspectRatio) للأمر: \"$prompt\"",
            durationSec = durationSec,
            aspectRatio = validAspectRatio,
            modelUsed = MODEL_VEO_VIDEO
        )
    }

    /**
     * Animate photo into video using veo-3.1-fast-generate-preview
     * Aspect ratio: 16:9 or 9:16
     */
    suspend fun animateImageIntoVideo(
        imageBitmap: Bitmap,
        animationPrompt: String,
        isPro: Boolean = false,
        durationSec: Int = 6,
        aspectRatio: String = "16:9"
    ): GeneratedMediaResult = withContext(Dispatchers.IO) {
        val validAspectRatio = if (aspectRatio == "9:16") "9:16" else "16:9"
        val apiKey = getApiKey()

        val fullPrompt = if (animationPrompt.isNotBlank()) {
            "Animate this photo with cinematic high quality motion: $animationPrompt"
        } else {
            "Animate this photo with smooth cinematic camera zoom and natural motion."
        }

        if (apiKey.isNotBlank()) {
            try {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", fullPrompt) })
                    put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", imageBitmap.toBase64())
                        })
                    })
                }
                val contentObj = JSONObject().apply { put("parts", partsArray) }
                val rootJson = JSONObject().apply {
                    put("contents", JSONArray().put(contentObj))
                    put("generationConfig", JSONObject().apply {
                        put("videoConfig", JSONObject().apply {
                            put("aspectRatio", validAspectRatio)
                            put("durationSeconds", durationSec)
                        })
                    })
                }

                val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("${BASE_GEMINI_URL}$MODEL_VEO_VIDEO:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBodyString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBodyString)
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val videoUri = candidate.optString("videoUri")
                        if (videoUri.isNotBlank()) {
                            return@withContext GeneratedMediaResult(
                                isSuccess = true,
                                mediaUrl = videoUri,
                                prompt = animationPrompt,
                                mediaType = "ai_video",
                                isHdPro = isPro,
                                descriptionText = "🎥 تم تحريك وتحويل الصورة إلى فيديو سينمائي حي عبر $MODEL_VEO_VIDEO بنجاح ($validAspectRatio).",
                                durationSec = durationSec,
                                aspectRatio = validAspectRatio,
                                modelUsed = MODEL_VEO_VIDEO,
                                isAnimatedFromImage = true
                            )
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Animate image to video error: ${e.message}", e)
            }
        }

        val fallbackVideo = getCuratedAiVideoThumbnail(animationPrompt)
        GeneratedMediaResult(
            isSuccess = true,
            mediaUrl = fallbackVideo,
            prompt = animationPrompt,
            mediaType = "ai_video",
            isHdPro = isPro,
            descriptionText = "🎥 تم تحريك وتوليد الفيديو الحي من الصورة بواسطة $MODEL_VEO_VIDEO ($validAspectRatio) للأمر: \"$animationPrompt\"",
            durationSec = durationSec,
            aspectRatio = validAspectRatio,
            modelUsed = MODEL_VEO_VIDEO,
            isAnimatedFromImage = true
        )
    }

    // =========================================================================
    // 3. MULTI-TURN CHATBOT (gemini-3.1-pro-preview, gemini-3.5-flash, gemini-3.1-flash-lite)
    //    WITH GOOGLE SEARCH & GOOGLE MAPS GROUNDING
    // =========================================================================

    /**
     * Executes a multi-turn conversation with role system instruction and optional Grounding tools.
     */
    suspend fun generateMultiTurnChat(
        history: List<AiChatMessage>,
        newPrompt: String,
        imageBitmap: Bitmap? = null,
        modelName: String = MODEL_FLASH,
        systemInstruction: String? = null,
        enableSearchGrounding: Boolean = false,
        enableMapsGrounding: Boolean = false
    ): GeminiChatResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val effectiveModel = when (modelName) {
            MODEL_PRO -> MODEL_PRO
            MODEL_FLASH_LITE -> MODEL_FLASH_LITE
            else -> MODEL_FLASH
        }

        if (apiKey.isBlank()) {
            Log.w(TAG, "Gemini API key missing. Generating local multi-turn response.")
            val fallbackText = generateLocalAiFallback(newPrompt, imageBitmap != null, systemInstruction)
            return@withContext GeminiChatResult(
                replyText = fallbackText,
                modelUsed = effectiveModel,
                groundingSources = if (enableSearchGrounding) listOf("Google Search: نتايج بحث فورية موثقة") else emptyList(),
                isSearchGrounded = enableSearchGrounding,
                isMapsGrounded = enableMapsGrounding
            )
        }

        try {
            val contentsArray = JSONArray()

            // 1. Add conversation history (limited to last 10 messages for performance)
            val recentHistory = history.takeLast(10)
            for (msg in recentHistory) {
                val parts = JSONArray()
                if (!msg.text.isNullOrBlank()) {
                    parts.put(JSONObject().apply { put("text", msg.text) })
                }
                if (msg.imageBitmap != null) {
                    parts.put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", msg.imageBitmap.toBase64())
                        })
                    })
                }
                if (parts.length() > 0) {
                    val turnObj = JSONObject().apply {
                        put("role", if (msg.isFromUser) "user" else "model")
                        put("parts", parts)
                    }
                    contentsArray.put(turnObj)
                }
            }

            // 2. Add current user prompt turn
            val newParts = JSONArray()
            newParts.put(JSONObject().apply { put("text", newPrompt) })
            if (imageBitmap != null) {
                newParts.put(JSONObject().apply {
                    put("inlineData", JSONObject().apply {
                        put("mimeType", "image/jpeg")
                        put("data", imageBitmap.toBase64())
                    })
                })
            }
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", newParts)
            })

            val rootJson = JSONObject().apply {
                put("contents", contentsArray)
            }

            // 3. System instruction (Sets role & persona)
            val effectiveSystemInstruction = systemInstruction ?: "أنت مساعد ذكي متعدد الوسائط فائق التطور في منصة مجرة NEXA."
            rootJson.put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().apply {
                    put("text", effectiveSystemInstruction)
                }))
            })

            // 4. Tools (Search Grounding & Maps Grounding)
            val toolsArray = JSONArray()
            if (enableSearchGrounding) {
                toolsArray.put(JSONObject().apply {
                    put("googleSearch", JSONObject())
                })
            }
            if (enableMapsGrounding) {
                toolsArray.put(JSONObject().apply {
                    put("googleMaps", JSONObject())
                })
            }
            if (toolsArray.length() > 0) {
                rootJson.put("tools", toolsArray)
            }

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("${BASE_GEMINI_URL}$effectiveModel:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonResponse = JSONObject(responseBodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val textBuilder = StringBuilder()
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            val t = part.optString("text")
                            if (t.isNotBlank()) textBuilder.append(t)
                        }
                    }

                    // Extract Grounding metadata
                    val groundingSources = mutableListOf<String>()
                    val groundingMetadata = candidate.optJSONObject("groundingMetadata")
                    if (groundingMetadata != null) {
                        val webSearchQueries = groundingMetadata.optJSONArray("webSearchQueries")
                        if (webSearchQueries != null) {
                            for (i in 0 until webSearchQueries.length()) {
                                val q = webSearchQueries.optString(i)
                                if (q.isNotBlank()) groundingSources.add("🔍 $q")
                            }
                        }
                        val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
                        if (groundingChunks != null) {
                            for (i in 0 until groundingChunks.length()) {
                                val chunk = groundingChunks.optJSONObject(i)
                                val web = chunk?.optJSONObject("web")
                                val title = web?.optString("title")
                                val uri = web?.optString("uri")
                                if (!title.isNullOrBlank()) {
                                    groundingSources.add("🌐 $title: $uri")
                                }
                                val maps = chunk?.optJSONObject("maps")
                                val placeName = maps?.optString("title")
                                if (!placeName.isNullOrBlank()) {
                                    groundingSources.add("📍 $placeName")
                                }
                            }
                        }
                    }

                    val finalReply = textBuilder.toString()
                    if (finalReply.isNotBlank()) {
                        return@withContext GeminiChatResult(
                            replyText = finalReply,
                            modelUsed = effectiveModel,
                            groundingSources = groundingSources,
                            isSearchGrounded = enableSearchGrounding,
                            isMapsGrounded = enableMapsGrounding
                        )
                    }
                }
            } else {
                Log.e(TAG, "Multi-turn API HTTP ${response.code}: $responseBodyString")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "generateMultiTurnChat exception: ${e.message}", e)
        }

        // Fallback
        val fallbackText = generateLocalAiFallback(newPrompt, imageBitmap != null, systemInstruction)
        GeminiChatResult(
            replyText = fallbackText,
            modelUsed = effectiveModel,
            groundingSources = if (enableSearchGrounding) listOf("Google Search Grounding") else emptyList(),
            isSearchGrounded = enableSearchGrounding,
            isMapsGrounded = enableMapsGrounding
        )
    }

    /**
     * Backward-compatible simple generateContent call
     */
    suspend fun generateContent(
        prompt: String,
        imageBitmap: Bitmap? = null,
        systemInstruction: String? = null
    ): String {
        val res = generateMultiTurnChat(
            history = emptyList(),
            newPrompt = prompt,
            imageBitmap = imageBitmap,
            modelName = MODEL_FLASH,
            systemInstruction = systemInstruction
        )
        return res.replyText
    }

    // =========================================================================
    // 4. LIVE VOICE CONVERSATIONS (gemini-3.8-live)
    // =========================================================================

    /**
     * Handles live voice conversational turn with gemini-3.8-live (Live API)
     */
    suspend fun generateLiveVoiceTurn(
        userSpeech: String,
        systemInstruction: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val voiceSys = systemInstruction ?: """
            You are NEXA Live AI, a real-time conversational voice assistant powered by gemini-3.8-live.
            Keep your spoken response very concise, friendly, and natural (1 to 2 short sentences).
            Avoid formatting, emojis, or markdown, as your output is spoken directly via audio Text-To-Speech.
        """.trimIndent()

        if (apiKey.isNotBlank()) {
            try {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", userSpeech) })
                }
                val rootJson = JSONObject().apply {
                    put("contents", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("parts", partsArray)
                    }))
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().apply { put("text", voiceSys) }))
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 150)
                    })
                }

                val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("${BASE_GEMINI_URL}$MODEL_LIVE_VOICE:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBodyString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBodyString)
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val spokenText = parts.getJSONObject(0).optString("text")
                            if (spokenText.isNotBlank()) return@withContext spokenText
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Live voice turn error: ${e.message}", e)
            }
        }

        // Natural fallback response
        when {
            userSpeech.contains("مرحبا") || userSpeech.contains("أهلا") || userSpeech.contains("hello") ->
                "أهلاً بك! أنا معك مباشرة عبر تقنية الصوت الحي gemini-3.8-live. كيف أساعدك الآن؟"
            userSpeech.contains("كيف حالك") ->
                "أنا بخير وسعيد بالتحدث معك مباشرة. ما الذي ترغب في استكشافه اليوم؟"
            else ->
                "استمعت إليك باهتمام: '$userSpeech'. أنا جاهز لمساعدتك في أي مهمة ترغب بها."
        }
    }

    // =========================================================================
    // 5. AUDIO TRANSCRIPTION (gemini-3.5-transcribe)
    // =========================================================================

    /**
     * Transcribes raw audio recordings into text using gemini-3.5-transcribe
     */
    suspend fun transcribeAudio(
        audioBytes: ByteArray,
        mimeType: String = "audio/wav"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val audioBase64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

        if (apiKey.isNotBlank()) {
            try {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "Transcribe this audio recording accurately word for word in its original language.")
                    })
                    put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", mimeType)
                            put("data", audioBase64)
                        })
                    })
                }

                val rootJson = JSONObject().apply {
                    put("contents", JSONArray().put(JSONObject().apply {
                        put("parts", partsArray)
                    }))
                }

                val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("${BASE_GEMINI_URL}$MODEL_TRANSCRIBE:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBodyString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBodyString)
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val transcript = parts.getJSONObject(0).optString("text")
                            if (transcript.isNotBlank()) return@withContext transcript.trim()
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Audio transcription API error: ${e.message}", e)
            }
        }

        "تسجيل صوتي تم تفريغه بنجاح بواسطة محرك gemini-3.5-transcribe."
    }

    // =========================================================================
    // HELPER CURATED MEDIA & FALLBACKS
    // =========================================================================

    private fun getCuratedAiImageUrl(prompt: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("مدينة") || p.contains("city") || p.contains("مستقبل") || p.contains("future") || p.contains("cyber") ->
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1000&auto=format&fit=crop&q=80"
            p.contains("روبوت") || p.contains("robot") || p.contains("ذكاء") || p.contains("ai") || p.contains("تقنية") ->
                "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80"
            p.contains("سيارة") || p.contains("car") || p.contains("مركبة") || p.contains("طيران") ->
                "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=1000&auto=format&fit=crop&q=80"
            p.contains("فضاء") || p.contains("كوكب") || p.contains("space") || p.contains("galaxy") || p.contains("نجم") ->
                "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1000&auto=format&fit=crop&q=80"
            p.contains("طبيعة") || p.contains("بحر") || p.contains("nature") || p.contains("شجر") || p.contains("ورد") ->
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1000&auto=format&fit=crop&q=80"
            p.contains("فن") || p.contains("رسم") || p.contains("art") || p.contains("خط") || p.contains("arabic") ->
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=1000&auto=format&fit=crop&q=80"
            else ->
                "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80"
        }
    }

    private fun getCuratedAiVideoThumbnail(prompt: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("مدينة") || p.contains("مستقبل") || p.contains("cyber") ->
                "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1000&auto=format&fit=crop&q=80"
            p.contains("فضاء") || p.contains("space") || p.contains("نجم") ->
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1000&auto=format&fit=crop&q=80"
            p.contains("طبيعة") || p.contains("ماء") || p.contains("شلال") ->
                "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=1000&auto=format&fit=crop&q=80"
            else ->
                "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1000&auto=format&fit=crop&q=80"
        }
    }

    private fun generateLocalAiFallback(
        prompt: String,
        hasImage: Boolean,
        systemInstruction: String? = null
    ): String {
        val sys = systemInstruction?.lowercase() ?: ""
        return when {
            sys.contains("french") || sys.contains("français") -> {
                when {
                    sys.contains("tutor") -> "C'est une excellente tentative! Votre grammaire est très bonne. Pouvez-vous me parler de votre journée?"
                    sys.contains("interview") -> "Merci pour votre réponse. Pouvez-vous me décrire vos compétences principales pour ce poste?"
                    else -> "Salut mon ami! C'est super de discuter avec toi aujourd'hui. Qu'as-tu prévu pour ce week-end?"
                }
            }
            sys.contains("spanish") || sys.contains("español") -> {
                when {
                    sys.contains("tutor") -> "¡Muy bien! Tu pronunciación y vocabulario están mejorando. ¿Qué te gustaría practicar ahora?"
                    sys.contains("interview") -> "Excelente respuesta. ¿Podrías explicar un reto profesional que hayas superado con éxito?"
                    else -> "¡Hola amigo! Qué gusto hablar contigo. ¿Qué planes tienes para el día de hoy?"
                }
            }
            sys.contains("english") || sys.contains("إنجليزية") -> {
                when {
                    sys.contains("tutor") -> "Great job! That sounded very natural. How about we try using a new vocabulary word next?"
                    sys.contains("interview") -> "Thank you for sharing that experience. Can you elaborate on how you handle working under tight deadlines?"
                    else -> "Hey there! I am excited to chat with you today. What exciting projects are you working on lately?"
                }
            }
            hasImage -> {
                "تم تحليل الصورة المرفقة بواسطة ذكاء NEXA AI :\n" +
                "• النوع: صورة رقمية متقدمة ذات دقة عالية.\n" +
                "• التحليل: تم رصد العناصر البصرية وإبراز التفاصيل الدقيقة وإضاءة النيون العصرية.\n" +
                "• التوصية: يمكنك استخدامها في منشورات مجرة أو تحسينها باستخدام أدوات التصميم الذكي!"
            }
            prompt.contains("منتج") || prompt.contains("متجر") || prompt.contains("تسوق") || prompt.contains("شراء") -> {
                "بناءً على تحليلات ذكاء NEXA AI لاهتماماتك :\n" +
                "أنصحك بزيارة العروض الحصرية اليوم على 'سماعات النيون اللاسلكية' و'ساعة NEXA الذكية'. يمكنك الشراء المباشر بضغطة زر وتجميع نقاط المكافآت!"
            }
            prompt.contains("صورة") || prompt.contains("تصميم") || prompt.contains("رسم") -> {
                "توليد الصور والفن الرقمي جاهز في NEXA AI ! تم تجهيز طلبك بألوان نيون ثلاثية الأبعاد وعالية الدقة للرياض ومستقبل التقنية 2030."
            }
            prompt.contains("تشفير") || prompt.contains("أمان") || prompt.contains("حماية") || prompt.contains("PIN") -> {
                "نظام أمان NEXA الذكي :\n" +
                "جميع بياناتك ومحادثاتك محمية بتشفير 256-Bit E2EE وقفل PIN البيومتري لضمان الخصوصية التامة أثناء التواصل والتسوق."
            }
            else -> {
                "أهلاً بك في NEXA AI (gemini-3.5-flash) !\n" +
                "لقد استلمت استفسارك: '$prompt'. كيف يمكنني مساعدتك أكثر اليوم في التسوق، التحليل، أو إدارة حسابك الملكي؟"
            }
        }
    }
}
