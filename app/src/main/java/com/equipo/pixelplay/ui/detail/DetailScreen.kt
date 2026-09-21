package com.equipo.pixelplay.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Placeholder de la ficha de detalle. Solo muestra el id recibido;
 * la ficha real (con datos del juego) llega en la próxima tarea.
 */
@Composable
fun DetailScreen(
    gameId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Detalle del juego $gameId",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
