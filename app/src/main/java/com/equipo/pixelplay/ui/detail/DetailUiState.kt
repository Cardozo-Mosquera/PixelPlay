package com.equipo.pixelplay.ui.detail

import com.equipo.pixelplay.domain.model.Game

/** Estados de la pantalla de Detalle. La UI resuelve con when(state). */
sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val game: Game) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
