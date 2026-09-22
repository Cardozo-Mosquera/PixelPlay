package com.equipo.pixelplay.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.pixelplay.data.repository.FavoritesRepository
import com.equipo.pixelplay.domain.model.Game
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel de "Mis favoritos": consume la lista en vivo desde [FavoritesRepository]
 * y la expone como [FavoritesUiState]. La lista vacía llega como Success(emptyList()).
 */
class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository = FavoritesRepository()
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> =
        favoritesRepository.observarFavoritos()
            .map<List<Game>, FavoritesUiState> { games -> FavoritesUiState.Success(games) }
            .catch { emit(FavoritesUiState.Error("No se pudieron cargar tus favoritos.")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FavoritesUiState.Loading
            )
}
