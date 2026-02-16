package com.app.cardashcam.core.storage

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TempFileManager(private val context: Context) {

    private val cacheDir = File(context.cacheDir, "recordings").apply { mkdirs() }

    fun createTempFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        return File(cacheDir, "temp_$timestamp.mp4")
    }

    fun moveToMediaStore(tempFile: File, isLocked: Boolean): Uri? {
        if (!tempFile.exists()) return null

        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis())
            val suffix = if (isLocked) "_LOCK" else ""
            val displayName = "video_${timestamp}${suffix}.mp4"

            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, displayName)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CarDashCam")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: return null

            context.contentResolver.openOutputStream(uri)?.use { output ->
                tempFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Video.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            }

            tempFile.delete()
            return uri

        } catch (e: Exception) {
            Log.e("TempFileManager", "Failed to move to MediaStore", e)
            return null
        }
    }

    fun deleteTempFile(file: File) {
        try {
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.e("TempFileManager", "Failed to delete temp file", e)
        }
    }

    fun recoverCrashFile(file: File, isLocked: Boolean): Uri? {
        if (!file.exists() || file.length() < 1024) {
            file.delete()
            return null
        }
        return moveToMediaStore(file, isLocked)
    }

    fun cleanupOrphanFiles() {
        try {
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("temp_") && file.extension == "mp4") {
                    val ageMinutes = (System.currentTimeMillis() - file.lastModified()) / (1000 * 60)
                    if (ageMinutes > 5) {
                        if (file.length() > 1024) {
                            recoverCrashFile(file, false)
                            Log.d("TempFileManager", "Recovered crash file: ${file.name}")
                        } else {
                            file.delete()
                            Log.d("TempFileManager", "Deleted corrupt file: ${file.name}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TempFileManager", "Cleanup failed", e)
        }
    }

    fun getTempFileSize(file: File): Long {
        return if (file.exists()) file.length() else 0L
    }
}
