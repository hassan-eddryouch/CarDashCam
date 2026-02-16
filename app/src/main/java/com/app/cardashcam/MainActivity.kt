package com.app.cardashcam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.cardashcam.app.navigation.AppNavHost
import com.app.cardashcam.core.ui.theme.CarDashCamTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CarDashCamTheme {
                AppNavHost()
            }
        }
    }
}
