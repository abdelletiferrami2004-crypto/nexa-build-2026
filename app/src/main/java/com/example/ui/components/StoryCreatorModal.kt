package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraIos
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StoryItem
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

enum class StoryCreationMode {
    MAIN_GALLERY,
    TEXT_EDITOR,
    MUSIC_SELECTOR,
    CAMERA_SIMULATOR,
    MEDIA_PREVIEW
}

data class GalleryItem(
    val id: Int,
    val title: String,
    val isVideo: Boolean = false,
    val durationText: String? = null,
    val gradientColors: List<Color>
)

@Composable
fun StoryCreatorModal(
    initialReplyAuthor: String? = null,
    initialReplyText: String? = null,
    onDismiss: () -> Unit,
    onPublishStory: (StoryItem) -> Unit
) {
    var activeMode by remember { mutableStateOf(StoryCreationMode.MAIN_GALLERY) }
    var isMultiSelectEnabled by remember { mutableStateOf(false) }
    val selectedMediaIndices = remember { mutableStateListOf<Int>() }
    var selectedSingleMedia by remember { mutableStateOf<GalleryItem?>(null) }

    // Text Story Editor states
    var storyText by remember { mutableStateOf("") }
    var selectedGradientIndex by remember { mutableStateOf(0) }
    var selectedFontIndex by remember { mutableStateOf(0) }
    var selectedMusicTrack by remember { mutableStateOf<String?>(null) }

    val gradientPresets = listOf(
        listOf(Color(0xFF8B5CF6), Color(0xFF00F5FF), Color(0xFFFF2A85)),
        listOf(Color(0xFFFF512F), Color(0xFFDD2476)),
        listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
        listOf(Color(0xFFFC466B), Color(0xFF3F5EFB)),
        listOf(Color(0xFF00B4DB), Color(0xFF0083B0)),
        listOf(Color(0xFF4568DC), Color(0xFFB06AB3))
    )

    val fontStyles = listOf("عادي", "عريض", "حديث", "كلاسيكي", "نيون")

    // Simulated Device Gallery Media Items
    val galleryItems = remember {
        listOf(
            GalleryItem(1, "صورة غروب الشمس", false, null, listOf(Color(0xFFFF7E5F), Color(0xFFFEB47B))),
            GalleryItem(2, "فيديو الحفلة", true, "0:15", listOf(Color(0xFF6A11CB), Color(0xFF2575FC))),
            GalleryItem(3, "لقطة الطبيعة", false, null, listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
            GalleryItem(4, "مقطع التصميم", true, "0:30", listOf(Color(0xFFFC466B), Color(0xFF3F5EFB))),
            GalleryItem(5, "صورة المقهى", false, null, listOf(Color(0xFFF7971E), Color(0xFFFFD200))),
            GalleryItem(6, "فيديو السفر", true, "0:22", listOf(Color(0xFF8A2387), Color(0xFFE94057))),
            GalleryItem(7, "خلفية نيون", false, null, listOf(Color(0xFF00F5FF), Color(0xFF8B5CF6))),
            GalleryItem(8, "فيديو التدريب", true, "0:45", listOf(Color(0xFF12C2E9), Color(0xFFC471ED))),
            GalleryItem(9, "صورة الفن الرقمي", false, null, listOf(Color(0xFFB92B27), Color(0xFF1565C0)))
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (activeMode) {
                    StoryCreationMode.MAIN_GALLERY -> {
                        MainGalleryScreen(
                            galleryItems = galleryItems,
                            isMultiSelectEnabled = isMultiSelectEnabled,
                            selectedMediaIndices = selectedMediaIndices,
                            onToggleMultiSelect = {
                                isMultiSelectEnabled = !isMultiSelectEnabled
                                if (!isMultiSelectEnabled) selectedMediaIndices.clear()
                            },
                            onSelectCreationMode = { mode -> activeMode = mode },
                            onItemClick = { index, item ->
                                if (isMultiSelectEnabled) {
                                    if (selectedMediaIndices.contains(index)) {
                                        selectedMediaIndices.remove(index)
                                    } else {
                                        selectedMediaIndices.add(index)
                                    }
                                } else {
                                    selectedSingleMedia = item
                                    activeMode = StoryCreationMode.MEDIA_PREVIEW
                                }
                            },
                            onPublishMultiSelect = {
                                selectedMediaIndices.forEach { idx ->
                                    val item = galleryItems.getOrNull(idx)
                                    if (item != null) {
                                        val colorInts = item.gradientColors.map { (it.value shr 32).toLong() }
                                        onPublishStory(
                                            StoryItem(
                                                authorName = "أنت",
                                                text = item.title,
                                                isVideo = item.isVideo,
                                                bgGradient = colorInts
                                            )
                                        )
                                    }
                                }
                                onDismiss()
                            },
                            onClose = onDismiss
                        )
                    }

                    StoryCreationMode.TEXT_EDITOR -> {
                        TextStoryEditor(
                            storyText = storyText,
                            onTextChange = { storyText = it },
                            selectedGradientIndex = selectedGradientIndex,
                            onSelectGradient = { selectedGradientIndex = it },
                            gradientPresets = gradientPresets,
                            selectedFontIndex = selectedFontIndex,
                            onSelectFont = { selectedFontIndex = it },
                            fontStyles = fontStyles,
                            selectedMusicTrack = selectedMusicTrack,
                            onOpenMusicSelector = { activeMode = StoryCreationMode.MUSIC_SELECTOR },
                            onBack = { activeMode = StoryCreationMode.MAIN_GALLERY },
                            onPublish = {
                                val colorInts = gradientPresets[selectedGradientIndex].map { (it.value shr 32).toLong() }
                                onPublishStory(
                                    StoryItem(
                                        authorName = "أنت",
                                        text = if (storyText.isBlank()) "قصة جديدة" else storyText,
                                        isVideo = false,
                                        bgGradient = colorInts
                                    )
                                )
                                onDismiss()
                            }
                        )
                    }

                    StoryCreationMode.MUSIC_SELECTOR -> {
                        MusicSelectorOverlay(
                            selectedTrack = selectedMusicTrack,
                            onSelectTrack = { track ->
                                selectedMusicTrack = track
                                activeMode = if (selectedSingleMedia != null) StoryCreationMode.MEDIA_PREVIEW else StoryCreationMode.TEXT_EDITOR
                            },
                            onBack = {
                                activeMode = if (selectedSingleMedia != null) StoryCreationMode.MEDIA_PREVIEW else StoryCreationMode.TEXT_EDITOR
                            }
                        )
                    }

                    StoryCreationMode.CAMERA_SIMULATOR -> {
                        CameraSimulatorScreen(
                            onBack = { activeMode = StoryCreationMode.MAIN_GALLERY },
                            onCapture = { isVideo ->
                                onPublishStory(
                                    StoryItem(
                                        authorName = "أنت",
                                        text = if (isVideo) "مقطع كاميرا مباشر" else "صورة كاميرا مباشرة",
                                        isVideo = isVideo,
                                        bgGradient = listOf(0xFF00F5FF, 0xFF8B5CF6)
                                    )
                                )
                                onDismiss()
                            }
                        )
                    }

                    StoryCreationMode.MEDIA_PREVIEW -> {
                        MediaPreviewEditor(
                            item = selectedSingleMedia,
                            selectedMusicTrack = selectedMusicTrack,
                            onOpenMusicSelector = { activeMode = StoryCreationMode.MUSIC_SELECTOR },
                            onBack = {
                                selectedSingleMedia = null
                                activeMode = StoryCreationMode.MAIN_GALLERY
                            },
                            onPublish = { caption ->
                                val colors = selectedSingleMedia?.gradientColors?.map { (it.value shr 32).toLong() }
                                    ?: listOf(0xFF8B5CF6, 0xFF00F5FF)
                                onPublishStory(
                                    StoryItem(
                                        authorName = "أنت",
                                        text = if (caption.isBlank()) (selectedSingleMedia?.title ?: "قصة جديدة") else caption,
                                        isVideo = selectedSingleMedia?.isVideo ?: false,
                                        bgGradient = colors
                                    )
                                )
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainGalleryScreen(
    galleryItems: List<GalleryItem>,
    isMultiSelectEnabled: Boolean,
    selectedMediaIndices: List<Int>,
    onToggleMultiSelect: () -> Unit,
    onSelectCreationMode: (StoryCreationMode) -> Unit,
    onItemClick: (Int, GalleryItem) -> Unit,
    onPublishMultiSelect: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        // 1. Screen Header: "إنشاء قصة" with close icon (X)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "إنشاء قصة",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Top 3 Action Cards Grid (Equal Width, Rounded Cards): Text, Music, Camera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Text Story Card
            ActionCard(
                title = "نص",
                icon = Icons.Default.Edit,
                gradient = listOf(Color(0xFF8B5CF6), Color(0xFFD946EF)),
                modifier = Modifier.weight(1f),
                onClick = { onSelectCreationMode(StoryCreationMode.TEXT_EDITOR) }
            )

            // Music Story Card
            ActionCard(
                title = "موسيقى",
                icon = Icons.Default.MusicNote,
                gradient = listOf(Color(0xFF00F5FF), Color(0xFF06B6D4)),
                modifier = Modifier.weight(1f),
                onClick = { onSelectCreationMode(StoryCreationMode.MUSIC_SELECTOR) }
            )

            // Camera Story Card
            ActionCard(
                title = "كاميرا",
                icon = Icons.Default.CameraAlt,
                gradient = listOf(Color(0xFF2563EB), Color(0xFF3B82F6)),
                modifier = Modifier.weight(1f),
                onClick = { onSelectCreationMode(StoryCreationMode.CAMERA_SIMULATOR) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Mid Header Row: "المعرض" on right, and clickable button "تحديد عدة عناصر" on left
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "المعرض",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isMultiSelectEnabled) NeonCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                    .border(1.dp, if (isMultiSelectEnabled) NeonCyan else Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .clickable { onToggleMultiSelect() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isMultiSelectEnabled) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = if (isMultiSelectEnabled) NeonCyan else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تحديد عدة عناصر",
                    color = if (isMultiSelectEnabled) NeonCyan else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3-Column Photo & Video Grid View
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                gridItemsIndexed(galleryItems) { index, item ->
                    val isSelected = selectedMediaIndices.contains(index)
                    val selectedOrder = if (isSelected) selectedMediaIndices.indexOf(index) + 1 else null

                    Box(
                        modifier = Modifier
                            .aspectRatio(0.75f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.verticalGradient(item.gradientColors))
                            .border(
                                width = if (isSelected) 2.5.dp else 0.5.dp,
                                color = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onItemClick(index, item) }
                            .padding(6.dp)
                    ) {
                        // Title / Caption preview
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )

                        // Video Badge
                        if (item.isVideo) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                if (item.durationText != null) {
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = item.durationText,
                                        color = Color.White,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        // Numbered Checkbox in Multi-Select Mode
                        if (isMultiSelectEnabled) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(if (isSelected) NeonCyan else Color.Black.copy(alpha = 0.4f))
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedOrder != null) {
                                    Text(
                                        text = "$selectedOrder",
                                        color = BackgroundDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Multi-Select Post Action Button
            if (isMultiSelectEnabled && selectedMediaIndices.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = NeonCyan,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { onPublishMultiSelect() }
                            .padding(vertical = 14.dp, horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نشر القصص المحددة (${selectedMediaIndices.size})",
                            color = BackgroundDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = BackgroundDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(gradient))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TextStoryEditor(
    storyText: String,
    onTextChange: (String) -> Unit,
    selectedGradientIndex: Int,
    onSelectGradient: (Int) -> Unit,
    gradientPresets: List<List<Color>>,
    selectedFontIndex: Int,
    onSelectFont: (Int) -> Unit,
    fontStyles: List<String>,
    selectedMusicTrack: String?,
    onOpenMusicSelector: () -> Unit,
    onBack: () -> Unit,
    onPublish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientPresets[selectedGradientIndex]))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Add Music Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .clickable { onOpenMusicSelector() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedMusicTrack ?: "إضافة موسيقى",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onPublish,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("نشر", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Main Text Canvas
        OutlinedTextField(
            value = storyText,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = "اكتب النص هنا...",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // Controls Bottom Panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(12.dp)
        ) {
            // Font Selector Row
            Text("نوع الخط", color = Color.LightGray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(fontStyles) { idx, fontName ->
                    val isSelected = idx == selectedFontIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f))
                            .clickable { onSelectFont(idx) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = fontName,
                            color = if (isSelected) BackgroundDark else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Background Color Gradient Selector Row
            Text("خلفية القصة", color = Color.LightGray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(gradientPresets) { idx, colors ->
                    val isSelected = idx == selectedGradientIndex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(colors))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onSelectGradient(idx) }
                    )
                }
            }
        }
    }
}

@Composable
fun MusicSelectorOverlay(
    selectedTrack: String?,
    onSelectTrack: (String) -> Unit,
    onBack: () -> Unit
) {
    val musicTracks = listOf(
        "موسيقى هادئة",
        "حماسي نيون",
        "أنغام كلاسيكية",
        "إيقاع حاد",
        "صوت الطبيعة",
        "موسيقى سينمائية",
        "عزف عود شرقي"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("اختر الموسيقى", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        musicTracks.forEach { track ->
            val isSelected = track == selectedTrack
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                    .border(1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(14.dp))
                    .clickable { onSelectTrack(track) }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = track, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                }
            }
        }
    }
}

@Composable
fun CameraSimulatorScreen(
    onBack: () -> Unit,
    onCapture: (Boolean) -> Unit
) {
    var isRecordingVideo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Viewfinder Simulation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF1E293B), Color.Black)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRecordingVideo) "جاري تسجيل مقطع الفيديو..." else "معاينة الكاميرا المباشرة",
                color = if (isRecordingVideo) NeonPink else Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Top Actions Bar (Close, Flash, Switch Camera)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Row {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Flash", tint = Color.White)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FlipCameraIos, contentDescription = "Flip Camera", tint = Color.White)
                }
            }
        }

        // Bottom Capture Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Button
                Button(
                    onClick = { onCapture(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("التقاط صورة", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isRecordingVideo) NeonPink else Color.White)
                        .clickable {
                            isRecordingVideo = !isRecordingVideo
                            if (!isRecordingVideo) onCapture(true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(BackgroundDark)
                    )
                }

                // Video Record Button
                Button(
                    onClick = { onCapture(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("تسجيل فيديو", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MediaPreviewEditor(
    item: GalleryItem?,
    selectedMusicTrack: String?,
    onOpenMusicSelector: () -> Unit,
    onBack: () -> Unit,
    onPublish: (String) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    val colors = item?.gradientColors ?: listOf(Color(0xFF8B5CF6), Color(0xFF00F5FF))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors))
            .padding(16.dp)
    ) {
        // Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Music Selector Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { onOpenMusicSelector() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedMusicTrack ?: "إضافة موسيقى",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onPublish(caption) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("نشر", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Center Content Preview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item?.title ?: "معاينة العنصر",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (item?.isVideo == true) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مقطع فيديو", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Bottom Caption Input
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            placeholder = { Text("إضافة شرح للقصة...", color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
