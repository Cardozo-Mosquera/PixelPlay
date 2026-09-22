package com.equipo.pixelplay.ui.favorites

import com.equipo.pixelplay.domain.model.Game

/**
 * Estados de la pantalla "Mis favoritos". La UI resuelve con when(state).
 * El caso vacío NO es un Error: se representa como Success con lista vacía.
 */
sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Success(val games: List<Game>) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}
