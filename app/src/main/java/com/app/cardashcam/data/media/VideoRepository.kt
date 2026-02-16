package com.app.cardashcam.data.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.app.cardashcam.domain.model.VideoItem

class VideoRepository(private val context: Context) {

    fun getVideos(): List<VideoItem> {

        val list = mutableListOf<VideoItem>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.RELATIVE_PATH
        )

        val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
        val args = arrayOf("%Movies/CarDashCam%")

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            args,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )

        cursor?.use {

            val idCol = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

            while (it.moveToNext()) {

                val id = it.getLong(idCol)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val name = it.getString(nameCol)

                list.add(
                    VideoItem(
                        uri = uri,
                        name = name,
                        size = it.getLong(sizeCol),
                        date = it.getLong(dateCol),
                        duration = it.getLong(durationCol),
                        isLocked = name.contains("_LOCK")
                    )
                )
            }
        }

        return list
    }

    fun deleteVideo(uri: Uri) {
        context.contentResolver.delete(uri, null, null)
    }

    fun deleteOldestUnlocked() {

        val videos = getVideos()
            .filter { !it.isLocked }
            .sortedBy { it.date }

        if (videos.isNotEmpty()) {
            context.contentResolver.delete(videos.first().uri, null, null)
        }
    }
}
