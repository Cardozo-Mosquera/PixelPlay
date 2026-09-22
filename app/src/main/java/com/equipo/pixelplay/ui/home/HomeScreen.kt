package com.equipo.pixelplay.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo.pixelplay.ui.components.ErrorView
import com.equipo.pixelplay.ui.components.GameCard
import com.equipo.pixelplay.ui.components.LoadingView

/** Pantalla Home: catálogo de juegos con los tres estados de UI y búsqueda en memoria. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGameClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val favoritosIds by viewModel.favoritosIds.collectAsStateWithLifecycle()
    val esAdmin by viewModel.esAdmin.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("PixelPlay") },
                actions = {
                    // Acceso al panel de administración: solo visible para admins.
                    if (esAdmin) {
                        IconButton(onClick = onAdminClick) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Administración"
                            )
                        }
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Mis favoritos"
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val current = state) {
            is HomeUiState.Loading -> LoadingView(Modifier.padding(innerPadding))

            is HomeUiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )

                if (current.games.isEmpty() && query.isNotBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron juegos",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(current.games, key = { it.id }) { game ->
                            GameCard(
                                game = game,
                                onGameClick = onGameClick,
                                esFavorito = favoritosIds.contains(game.id),
                                onToggleFavorito = viewModel::alternarFavorito
                            )
                        }
                    }
                }
            }

            is HomeUiState.Error -> ErrorView(
                message = current.message,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        placeholder = { Text("Buscar juegos") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Limpiar")
                }
            }
        },
        modifier = modifier
    )
}
