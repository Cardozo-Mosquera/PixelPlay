package com.equipo.pixelplay.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo.pixelplay.ui.components.ErrorView
import com.equipo.pixelplay.ui.components.GameCard
import com.equipo.pixelplay.ui.components.LoadingView

/** Pantalla Home: catálogo de juegos con los tres estados de UI. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGameClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("PixelPlay") })
        }
    ) { innerPadding ->
        when (val current = state) {
            is HomeUiState.Loading -> LoadingView(Modifier.padding(innerPadding))

            is HomeUiState.Success -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(current.games, key = { it.id }) { game ->
                    GameCard(game = game, onGameClick = onGameClick)
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
