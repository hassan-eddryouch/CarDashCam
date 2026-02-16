package com.app.cardashcam.feature_camera.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.cardashcam.core.storage.TempFileManager
import com.app.cardashcam.feature_camera.location.LocationRepository
import com.app.cardashcam.feature_camera.location.TripTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CameraUiState(
    val isRecording: Boolean = false,
    val recordingTime: String = "00:00",
    val speed: Float? = null,
    val maxSpeed: Float = 0f,
    val tripDuration: String = "00:00",
    val distance: Float = 0f,
    val isLocked: Boolean = false,
    val isMicEnabled: Boolean = true,
    val hasGPS: Boolean = false,
    val showSaveDialog: Boolean = false
)

class CameraViewModel(app: Application) : AndroidViewModel(app) {

    private val locationRepo = LocationRepository(app)
    private val tripTracker = TripTracker()
    private val tempFileManager = TempFileManager(app)

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tripTracker.stats.collect { stats ->
                _uiState.value = _uiState.value.copy(
                    maxSpeed = stats.maxSpeed,
                    distance = stats.distance
                )
            }
        }
        tempFileManager.cleanupOrphanFiles()
    }

    fun startLocationUpdates() {
        viewModelScope.launch {
            locationRepo.getSpeedFlow().collect { speed ->
                val hasGPS = speed != null
                _uiState.value = _uiState.value.copy(
                    speed = speed,
                    hasGPS = hasGPS
                )
            }
        }
        
        viewModelScope.launch {
            locationRepo.getLocationFlow().collect { location ->
                if (_uiState.value.isRecording && location != null) {
                    tripTracker.updateLocation(location)
                }
            }
        }
    }

    fun startRecording() {
        tripTracker.start()
        _uiState.value = _uiState.value.copy(
            isRecording = true,
            isLocked = false
        )
    }

    fun stopRecording() {
        tripTracker.stop()
        val shouldShowDialog = !_uiState.value.isLocked
        _uiState.value = _uiState.value.copy(
            isRecording = false,
            showSaveDialog = shouldShowDialog
        )
    }

    fun confirmSave() {
        resetStats()
    }

    fun confirmDelete() {
        resetStats()
    }

    fun dismissSaveDialog() {
        _uiState.value = _uiState.value.copy(showSaveDialog = false)
    }

    private fun resetStats() {
        _uiState.value = _uiState.value.copy(
            showSaveDialog = false,
            maxSpeed = 0f,
            distance = 0f,
            tripDuration = "00:00",
            isLocked = false
        )
    }

    fun updateRecordingTime(time: String) {
        _uiState.value = _uiState.value.copy(recordingTime = time, tripDuration = time)
    }

    fun setLocked(locked: Boolean) {
        _uiState.value = _uiState.value.copy(isLocked = locked)
    }

    fun toggleMicrophone() {
        _uiState.value = _uiState.value.copy(isMicEnabled = !_uiState.value.isMicEnabled)
    }
}
