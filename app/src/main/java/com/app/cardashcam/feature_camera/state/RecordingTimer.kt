package com.app.cardashcam.feature_camera.state

class RecordingTimer {

    private var startTime = 0L

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun stop() {
        startTime = 0L
    }

    fun getTime(): String {

        if (startTime == 0L) return "00:00"

        val sec = (System.currentTimeMillis() - startTime) / 1000
        val min = sec / 60
        val s = sec % 60

        return "%02d:%02d".format(min, s)
    }
}
