package com.equipo.pixelplay.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

/** Pantalla "Mis favoritos": la lista de favoritos de la cuenta, en vivo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onGameClick: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mis favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val current = state) {
            is FavoritesUiState.Loading -> LoadingView(Modifier.padding(innerPadding))

            is FavoritesUiState.Error -> ErrorView(
                message = current.message,
                onRetry = { /* La lista se re-observa sola; nada que reintentar manualmente. */ },
                modifier = Modifier.padding(innerPadding)
            )

            is FavoritesUiState.Success -> {
                if (current.games.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no tienes favoritos",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(current.games, key = { it.id }) { game ->
                            GameCard(game = game, onGameClick = onGameClick)
                        }
                    }
                }
            }
        }
    }
}
