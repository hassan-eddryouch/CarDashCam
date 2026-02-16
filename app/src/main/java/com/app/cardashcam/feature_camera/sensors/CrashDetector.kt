package com.app.cardashcam.feature_camera.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class CrashDetector(
    context: Context,
    private val onCrash: () -> Unit
) : SensorEventListener {

    private val manager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastTrigger = 0L

    fun start() {
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt(x * x + y * y + z * z) / 9.81f

        // Detect crash or hard brake (> 3G)
        if (gForce > 3f) {

            val now = System.currentTimeMillis()

            if (now - lastTrigger > 5000) {
                lastTrigger = now
                onCrash()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
