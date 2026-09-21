package com.equipo.pixelplay.ui.home

import com.equipo.pixelplay.domain.model.Game

/** Estados de la pantalla Home. La UI resuelve con when(state). */
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val games: List<Game>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
