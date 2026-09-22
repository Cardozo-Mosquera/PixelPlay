package com.equipo.pixelplay.ui.admin

import com.equipo.pixelplay.domain.model.UsuarioAdmin

/**
 * Estados del panel de usuarios. La UI resuelve con when(state).
 * El caso vacío NO es Error: se representa como Success con lista vacía.
 */
sealed interface AdminUsersUiState {
    data object Loading : AdminUsersUiState
    data class Success(val usuarios: List<UsuarioAdmin>) : AdminUsersUiState
    data class Error(val message: String) : AdminUsersUiState
}
