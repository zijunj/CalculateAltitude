package com.example.calculatealtitude

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null
    private val P0 = 1013.25f  // Sea level pressure (hPa)

    private var pressure by mutableStateOf(0f)
    private var altitude by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        setContent {
            AltimeterUI(pressure, altitude)
        }
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
            pressure = event.values[0]  // Get pressure value
            altitude = 44330 * (1 - (pressure / P0).toDouble().pow(1.0 / 5.255)).toFloat()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
}

@Composable
fun AltimeterUI(pressure: Float, altitude: Float) {
    val backgroundColor = when {
        altitude < 500 -> Color.parseColor("#87CEEB")  // Light Blue (Low altitude)
        altitude < 1000 -> Color.parseColor("#4682B4") // Steel Blue (Medium altitude)
        altitude < 2000 -> Color.parseColor("#4169E1") // Royal Blue (High altitude)
        else -> Color.parseColor("#00008B")           // Dark Blue (Very high altitude)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(backgroundColor))
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pressure: %.2f hPa".format(pressure), fontSize = 22.sp, color = androidx.compose.ui.graphics.Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Altitude: %.2f m".format(altitude), fontSize = 26.sp, color = androidx.compose.ui.graphics.Color.White)
    }
}
