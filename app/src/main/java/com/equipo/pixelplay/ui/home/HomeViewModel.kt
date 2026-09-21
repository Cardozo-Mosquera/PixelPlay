package com.equipo.pixelplay.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.pixelplay.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de Home: carga la lista de juegos vía [GameRepository]
 * y expone el estado como [HomeUiState].
 */
class HomeViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    fun retry() = loadGames()

    private fun loadGames() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                HomeUiState.Success(repository.getGames())
            } catch (e: Exception) {
                HomeUiState.Error(
                    e.message ?: "No se pudo cargar el catálogo. Revisa tu conexión."
                )
            }
        }
    }
}
