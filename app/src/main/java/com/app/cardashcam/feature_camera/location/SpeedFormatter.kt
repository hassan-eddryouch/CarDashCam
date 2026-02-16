package com.app.cardashcam.feature_camera.location

object SpeedFormatter {
    
    fun format(speedKmh: Float?): String {
        return when {
            speedKmh == null -> "--"
            speedKmh < 1f -> "0"
            else -> speedKmh.toInt().toString()
        }
    }
}
