package com.equipo.pixelplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo.pixelplay.navigation.PixelPlayNavHost
import com.equipo.pixelplay.ui.auth.AuthViewModel
import com.equipo.pixelplay.ui.auth.LoginScreen
import com.equipo.pixelplay.ui.auth.RegisterScreen
import com.equipo.pixelplay.ui.theme.PixelPlayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelPlayTheme {
                AuthGate()
            }
        }
    }
}

/**
 * Puerta de autenticación: por encima de la app.
 * Con sesión → la app actual (PixelPlayNavHost, intacta).
 * Sin sesión → alterna Login / Register compartiendo el mismo AuthViewModel.
 */
@Composable
private fun AuthGate() {
    val authViewModel: AuthViewModel = viewModel()
    val isLogged by authViewModel.authState.collectAsStateWithLifecycle()

    if (isLogged) {
        PixelPlayNavHost(
            userEmail = authViewModel.currentUserEmail,
            onLogout = { authViewModel.logout() }
        )
    } else {
        var showRegister by remember { mutableStateOf(false) }
        if (showRegister) {
            RegisterScreen(
                viewModel = authViewModel,
                onGoLogin = { showRegister = false }
            )
        } else {
            LoginScreen(
                viewModel = authViewModel,
                onGoRegister = { showRegister = true }
            )
        }
    }
}
