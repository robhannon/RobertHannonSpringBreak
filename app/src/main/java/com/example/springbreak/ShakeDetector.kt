package com.example.springbreak

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.pow
import kotlin.math.sqrt

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private var lastTime: Long = 0
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    private val shakeThreshold = 100

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastTime) > 100) {
            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // I got this logic from ChatGPT
            val speed = sqrt(((x - lastX).toDouble().pow(2.0) + (y - lastY).toDouble().pow(2.0) + (z - lastZ).toDouble().pow(2.0)) / (currentTime - lastTime) * 10000).toInt()

            if (speed > shakeThreshold) {
                onShake()
            }

            lastTime = currentTime
            lastX = x
            lastY = y
            lastZ = z
        }
    }
}