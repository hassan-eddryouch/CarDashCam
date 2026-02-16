package com.app.cardashcam.feature_gallery.utils

import com.app.cardashcam.domain.model.VideoItem
import java.util.concurrent.TimeUnit

enum class VideoFilter {
    ALL, LOCKED, EVENTS
}

enum class TimelineGroup {
    TODAY, YESTERDAY, OLDER
}

data class GroupedVideos(
    val group: TimelineGroup,
    val videos: List<VideoItem>
)

object TimelineGrouper {
    
    fun groupByTimeline(videos: List<VideoItem>, filter: VideoFilter = VideoFilter.ALL): List<GroupedVideos> {
        val filtered = when (filter) {
            VideoFilter.ALL -> videos
            VideoFilter.LOCKED -> videos.filter { it.isLocked }
            VideoFilter.EVENTS -> videos.filter { it.isLocked }
        }

        val now = System.currentTimeMillis() / 1000
        val oneDayAgo = now - TimeUnit.DAYS.toSeconds(1)
        val twoDaysAgo = now - TimeUnit.DAYS.toSeconds(2)

        val today = filtered.filter { it.date >= oneDayAgo }
        val yesterday = filtered.filter { it.date in twoDaysAgo..<oneDayAgo }
        val older = filtered.filter { it.date < twoDaysAgo }

        return buildList {
            if (today.isNotEmpty()) add(GroupedVideos(TimelineGroup.TODAY, today))
            if (yesterday.isNotEmpty()) add(GroupedVideos(TimelineGroup.YESTERDAY, yesterday))
            if (older.isNotEmpty()) add(GroupedVideos(TimelineGroup.OLDER, older))
        }
    }
}
