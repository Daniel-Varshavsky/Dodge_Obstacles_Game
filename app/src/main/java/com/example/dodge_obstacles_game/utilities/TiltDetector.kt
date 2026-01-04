package com.example.dodge_obstacles_game.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.dodge_obstacles_game.interfaces.TiltCallback
import kotlin.math.abs

class TiltDetector(
    context: Context,
    private val tiltCallback: TiltCallback
) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastTiltTime = 0L
    private val tiltCooldown = 500L
    private val tiltThreshold = 3.0f

    private val sensorEventListener = object : SensorEventListener {

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[2]

            val now = System.currentTimeMillis()

            if (now - lastTiltTime < tiltCooldown) return

            when {
                x > tiltThreshold -> {
                    lastTiltTime = now
                    tiltCallback.onTiltLeft()
                }

                x < -tiltThreshold -> {
                    lastTiltTime = now
                    tiltCallback.onTiltRight()
                }

                y < -tiltThreshold -> {
                    lastTiltTime = now
                    tiltCallback.onTiltBackward()
                }

                y > tiltThreshold -> {
                    lastTiltTime = now
                    tiltCallback.onTiltForward()
                }
            }
        }
    }

    fun start() {
        sensorManager.registerListener(
            sensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}
