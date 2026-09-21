package com.equipo.pixelplay.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.equipo.pixelplay.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de Detalle: carga un juego por id vía [GameRepository]
 * y expone el estado como [DetailUiState].
 */
class DetailViewModel(
    private val gameId: Int,
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadGame()
    }

    fun retry() = loadGame()

    private fun loadGame() {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                DetailUiState.Success(repository.getGame(gameId))
            } catch (e: Exception) {
                DetailUiState.Error(
                    e.message ?: "No se pudo cargar la ficha del juego. Revisa tu conexión."
                )
            }
        }
    }

    companion object {
        /** Factory que inyecta el gameId en el ViewModel, sin dependencias extra. */
        fun provideFactory(gameId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetailViewModel(gameId) }
        }
    }
}
