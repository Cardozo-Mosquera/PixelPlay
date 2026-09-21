package com.equipo.pixelplay

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.equipo.pixelplay.navigation.PixelPlayNavHost
import com.equipo.pixelplay.ui.theme.PixelPlayTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Spike de plomería: verifica que Firebase inicializa (no cambia la UI/flujo).
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("FirebaseSpike", "currentUser = ${user?.uid ?: "null"}")

        enableEdgeToEdge()
        setContent {
            PixelPlayTheme {
                PixelPlayNavHost()
            }
        }
    }
}
