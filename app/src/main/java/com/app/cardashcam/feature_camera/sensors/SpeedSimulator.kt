package com.app.cardashcam.feature_camera.sensors

import kotlin.random.Random

class SpeedSimulator {

    private var speed = 0

    fun nextSpeed(): Int {
        // Gradual change for realistic simulation
        val change = Random.nextInt(-4, 7)
        speed += change

        if (speed < 0) speed = 0
        if (speed > 130) speed = 130

        return speed
    }
}
