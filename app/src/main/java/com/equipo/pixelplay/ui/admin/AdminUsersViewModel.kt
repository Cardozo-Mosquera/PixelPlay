package com.equipo.pixelplay.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.pixelplay.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel del panel de usuarios: carga la lista una vez vía [AdminRepository]
 * y la expone como [AdminUsersUiState]. Permite reintentar tras un Error.
 */
class AdminUsersViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUsersUiState>(AdminUsersUiState.Loading)
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun retry() = cargar()

    private fun cargar() {
        _uiState.value = AdminUsersUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                AdminUsersUiState.Success(repository.listarUsuarios())
            } catch (e: Exception) {
                AdminUsersUiState.Error(
                    e.message ?: "No se pudo cargar la lista de usuarios."
                )
            }
        }
    }
}
