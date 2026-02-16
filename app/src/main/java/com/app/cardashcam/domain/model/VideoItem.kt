package com.app.cardashcam.domain.model

import android.net.Uri

data class VideoItem(
    val uri: Uri,
    val name: String,
    val size: Long,
    val date: Long,
    val duration: Long = 0L,
    val isLocked: Boolean = false
)
