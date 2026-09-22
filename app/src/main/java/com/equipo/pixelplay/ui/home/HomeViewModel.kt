package com.equipo.pixelplay.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.pixelplay.data.repository.FavoritesRepository
import com.equipo.pixelplay.data.repository.GameRepository
import com.equipo.pixelplay.data.repository.UserRepository
import com.equipo.pixelplay.domain.model.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    private val repository: GameRepository = GameRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    // Rol leído una vez al abrir Home; solo controla mostrar/ocultar el acceso admin.
    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    /**
     * Ids de los juegos favoritos de la cuenta, en vivo. Se expone aparte del uiState
     * para no tocar el filtro de búsqueda existente (HomeUiState sigue siendo solo la lista).
     */
    val favoritosIds: StateFlow<Set<Int>> =
        favoritesRepository.observarFavoritos()
            .map { games -> games.map { it.id }.toSet() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

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
        viewModelScope.launch {
            _esAdmin.value = userRepository.rolUsuarioActual() == "admin"
        }
    }

    fun retry() = loadGames()

    fun onQueryChange(nuevo: String) {
        _query.value = nuevo
    }

    /** Marca/desmarca un juego como favorito desde la grilla del Home. */
    fun alternarFavorito(game: Game) {
        val esFavorito = favoritosIds.value.contains(game.id)
        viewModelScope.launch {
            favoritesRepository.alternarFavorito(game, esFavorito)
        }
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
