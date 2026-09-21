package com.equipo.pixelplay.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.equipo.pixelplay.domain.model.Game
import com.equipo.pixelplay.ui.components.ErrorView
import com.equipo.pixelplay.ui.components.LoadingView

/** Ficha de detalle real de un juego. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gameId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.provideFactory(gameId))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val title = (state as? DetailUiState.Success)?.game?.name ?: "Detalle"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
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
            is DetailUiState.Loading -> LoadingView(Modifier.padding(innerPadding))

            is DetailUiState.Error -> ErrorView(
                message = current.message,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(innerPadding)
            )

            is DetailUiState.Success -> GameDetailContent(
                game = current.game,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun GameDetailContent(game: Game, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = game.thumbnail,
            contentDescription = game.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = game.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            LabeledRow(label = "Género", value = game.genre)
            Spacer(Modifier.height(4.dp))
            LabeledRow(label = "Plataforma", value = game.platform)
            Spacer(Modifier.height(16.dp))
            Text(
                // Fallback: si no hay descripción larga, usamos la corta.
                text = game.description ?: game.shortDescription,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(96.dp)
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
