package com.equipo.pixelplay.ui.admin

import com.equipo.pixelplay.domain.model.EstadisticaJuego

/**
 * Estados de la tarjeta de estadísticas del admin. La UI resuelve con when(state).
 * "Sin datos todavía" NO es Error: es Success con masMarcado == null.
 */
sealed interface AdminStatsUiState {
    data object Loading : AdminStatsUiState
    data class Success(
        val totalUsuarios: Int,
        val masMarcado: EstadisticaJuego?
    ) : AdminStatsUiState
    data class Error(val message: String) : AdminStatsUiState
}
