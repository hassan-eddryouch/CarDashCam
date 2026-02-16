package com.app.cardashcam.core.storage

import android.os.Environment
import android.os.StatFs

class StorageManager {

    fun freeSpaceMB(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val bytes = stat.availableBytes
        return bytes / (1024 * 1024)
    }
}
