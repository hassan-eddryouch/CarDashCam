package com.app.cardashcam.feature_gallery.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object VideoFormatters {
    
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }
    
    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
    
    fun formatSize(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return "%.1f MB".format(mb)
    }
    
    fun formatDuration(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return "%02d:%02d".format(minutes, seconds)
    }
    
    fun isLocked(filename: String): Boolean {
        return filename.contains("_LOCK", ignoreCase = true)
    }
}
