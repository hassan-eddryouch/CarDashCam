package com.app.cardashcam.feature_camera.location

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TripStats(
    val distance: Float = 0f,
    val maxSpeed: Float = 0f,
    val duration: Long = 0L
)

class TripTracker {
    private var lastLocation: Location? = null
    private var lastUpdateTime: Long = 0L
    private var startTime: Long = 0L
    private var totalDistance: Float = 0f
    private var maxSpeed: Float = 0f
    private var isTracking = false
    private val speedHistory = mutableListOf<Float>()

    private val _stats = MutableStateFlow(TripStats())
    val stats = _stats.asStateFlow()

    fun start() {
        isTracking = true
        startTime = System.currentTimeMillis()
        lastUpdateTime = startTime
        totalDistance = 0f
        maxSpeed = 0f
        lastLocation = null
        speedHistory.clear()
        updateStats()
    }

    fun updateLocation(location: Location) {
        if (!isTracking) return
        
        if (location.accuracy > 20f) return

        val currentTime = System.currentTimeMillis()
        var speed = 0f

        if (location.hasSpeed() && location.speed > 0) {
            speed = location.speed * 3.6f
        } else {
            lastLocation?.let { last ->
                val distance = last.distanceTo(location)
                val timeDiff = (currentTime - lastUpdateTime) / 1000f
                if (timeDiff > 0 && distance >= 2f && distance < 100f) {
                    speed = (distance / timeDiff) * 3.6f
                }
            }
        }

        if (speed > 0) {
            speedHistory.add(speed)
            if (speedHistory.size > 5) speedHistory.removeAt(0)
            
            val smoothSpeed = speedHistory.average().toFloat()
            if (smoothSpeed > maxSpeed) {
                maxSpeed = smoothSpeed
            }
        }

        lastLocation?.let { last ->
            val distance = last.distanceTo(location)
            if (distance >= 2f && distance < 100f) {
                totalDistance += distance
            }
        }
        
        lastLocation = location
        lastUpdateTime = currentTime
        updateStats()
    }

    fun stop() {
        isTracking = false
        lastLocation = null
        speedHistory.clear()
    }

    private fun updateStats() {
        val duration = if (startTime > 0 && isTracking) {
            (System.currentTimeMillis() - startTime) / 1000
        } else 0L

        _stats.value = TripStats(
            distance = totalDistance / 1000f,
            maxSpeed = maxSpeed,
            duration = duration
        )
    }
}
