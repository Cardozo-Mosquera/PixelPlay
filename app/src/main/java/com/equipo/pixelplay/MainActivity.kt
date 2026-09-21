package com.equipo.pixelplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.equipo.pixelplay.navigation.PixelPlayNavHost
import com.equipo.pixelplay.ui.theme.PixelPlayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelPlayTheme {
                PixelPlayNavHost()
            }
        }
    }
}
