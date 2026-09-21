package com.equipo.pixelplay.ui.auth

/** Estado del formulario de autenticación. */
sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
}
