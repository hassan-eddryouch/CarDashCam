package com.app.cardashcam.feature_gallery.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.cardashcam.data.media.VideoRepository
import com.app.cardashcam.feature_gallery.utils.GroupedVideos
import com.app.cardashcam.feature_gallery.utils.TimelineGrouper
import com.app.cardashcam.feature_gallery.utils.VideoFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = VideoRepository(app)

    private val _groupedVideos = MutableStateFlow<List<GroupedVideos>>(emptyList())
    val groupedVideos = _groupedVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentFilter = MutableStateFlow(VideoFilter.ALL)
    val currentFilter = _currentFilter.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val videos = withContext(Dispatchers.IO) {
                    repo.getVideos()
                }
                _groupedVideos.value = TimelineGrouper.groupByTimeline(videos, _currentFilter.value)
            } catch (e: Exception) {
                _groupedVideos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: VideoFilter) {
        _currentFilter.value = filter
        load()
    }

    fun delete(uri: Uri) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repo.deleteVideo(uri)
                }
                load()
            } catch (e: Exception) {
            }
        }
    }
}
