package com.example.sensors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sensors.ui.theme.SensorsTheme
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null

    private var xRotation by mutableStateOf(0f)
    private var yRotation by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        enableEdgeToEdge()
        setContent {
            SensorsTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(navController = navController)
                }
            }
        }
    }


    @Composable
    fun NavigationGraph(navController: NavHostController) {
        NavHost(navController = navController, startDestination = "start_screen") {
            composable("start_screen") {
                StartScreen(navController)
            }
            composable("gyro_visualization") {
                WaterInGlassVisualization(xRotation = yRotation, yRotation = xRotation)
            }
        }
    }

    @Composable
    fun StartScreen(navController: NavHostController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wykonał: Mateusz Nowak",
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tytuł: Sensory",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("gyro_visualization") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F684D))
            ) {
                Text(text = "Pokaż")
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        SensorsTheme {
            Greeting("Android")
        }
    }

    @Composable
    fun WaterInGlassVisualization(xRotation: Float, yRotation: Float) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                // Rozmiary szklanki
                val glassWidth = size.width * 0.6f
                val glassHeight = size.height * 0.8f
                val glassLeft = (size.width - glassWidth) / 2
                val glassTop = (size.height - glassHeight) / 2

                // Środek szklanki
                val centerX = size.width / 2
                val centerY = size.height / 2

                // Przesunięcie wody na podstawie żyroskopu
                val offsetX = xRotation * 50
                val offsetY = yRotation * 50

                // Pozycja wody w szklance (ograniczenie do wnętrza szklanki)
                val waterX = (centerX + offsetX).coerceIn(glassLeft + 20, glassLeft + glassWidth - 20)
                val waterY = (centerY - offsetY).coerceIn(glassTop + 20, glassTop + glassHeight - 20)

                // Rysowanie szklanki
                drawRect(
                    color = Color.Gray,
                    topLeft = androidx.compose.ui.geometry.Offset(glassLeft, glassTop),
                    size = androidx.compose.ui.geometry.Size(glassWidth, glassHeight),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                )

                // Rysowanie wody
                drawCircle(
                    color = Color.Blue,
                    radius = glassWidth / 6,
                    center = androidx.compose.ui.geometry.Offset(waterX, waterY)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            xRotation = event.values[0]
            yRotation = event.values[1]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}


