package com.equipo.pixelplay.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.pixelplay.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de las estadísticas de admin: carga total de usuarios y juego más marcado
 * vía [StatsRepository], una vez, y lo expone como [AdminStatsUiState]. Permite reintentar.
 * Independiente de [AdminUsersViewModel]: un fallo aquí no rompe la lista de usuarios.
 */
class AdminStatsViewModel(
    private val repository: StatsRepository = StatsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminStatsUiState>(AdminStatsUiState.Loading)
    val uiState: StateFlow<AdminStatsUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun retry() = cargar()

    private fun cargar() {
        _uiState.value = AdminStatsUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                AdminStatsUiState.Success(
                    totalUsuarios = repository.contarUsuarios(),
                    masMarcado = repository.juegoMasMarcado()
                )
            } catch (e: Exception) {
                AdminStatsUiState.Error(
                    e.message ?: "No se pudieron cargar las estadísticas."
                )
            }
        }
    }
}
