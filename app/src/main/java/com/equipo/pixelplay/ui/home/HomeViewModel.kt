package com.equipo.pixelplay.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.pixelplay.data.repository.GameRepository
import com.equipo.pixelplay.domain.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer

/** Quita diacríticos básicos y pasa a minúsculas para comparar sin acentos ni mayúsculas. */
private fun normalizar(texto: String): String =
    Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase()

/**
 * Filtra [games] por [query] sobre el nombre (case-insensitive, sin acentos).
 * Query en blanco -> lista completa. Función pura y testeable.
 */
internal fun filtrarJuegos(games: List<Game>, query: String): List<Game> {
    val q = normalizar(query.trim())
    if (q.isEmpty()) return games
    return games.filter { normalizar(it.name).contains(q) }
}

/**
 * ViewModel de Home: carga la lista de juegos vía [GameRepository]
 * y expone el estado FILTRADO por el query (filtrado en memoria, sin llamar a la API).
 */
class HomeViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    // Estado base (lo escriben loadGames()/retry()); el uiState público es derivado e inmutable.
    private val _baseState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    /** Estado que observa la UI: aplica el filtro solo cuando el estado base es Success. */
    val uiState: StateFlow<HomeUiState> =
        combine(_baseState, _query) { base, q ->
            when (base) {
                is HomeUiState.Success -> HomeUiState.Success(filtrarJuegos(base.games, q))
                is HomeUiState.Loading, is HomeUiState.Error -> base
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState.Loading
        )

    init {
        loadGames()
    }

    fun retry() = loadGames()

    fun onQueryChange(nuevo: String) {
        _query.value = nuevo
    }

    private fun loadGames() {
        _baseState.value = HomeUiState.Loading
        viewModelScope.launch {
            _baseState.value = try {
                HomeUiState.Success(repository.getGames())
            } catch (e: Exception) {
                HomeUiState.Error(
                    e.message ?: "No se pudo cargar el catálogo. Revisa tu conexión."
                )
            }
        }
    }
}
