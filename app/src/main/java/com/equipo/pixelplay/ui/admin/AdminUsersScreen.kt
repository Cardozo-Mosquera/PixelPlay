package com.equipo.pixelplay.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo.pixelplay.domain.model.UsuarioAdmin
import com.equipo.pixelplay.ui.components.ErrorView
import com.equipo.pixelplay.ui.components.LoadingView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/** Panel de administración: lista de usuarios registrados (solo lectura). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminUsersViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Usuarios") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tarjeta de estadísticas (Fase C): resumen arriba de la lista, estado propio.
            AdminStatsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            when (val current = state) {
                is AdminUsersUiState.Loading -> LoadingView(Modifier.weight(1f))

                is AdminUsersUiState.Error -> ErrorView(
                    message = current.message,
                    onRetry = viewModel::retry,
                    modifier = Modifier.weight(1f)
                )

                is AdminUsersUiState.Success -> {
                    if (current.usuarios.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay usuarios registrados",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(current.usuarios, key = { it.uid }) { usuario ->
                                UsuarioCard(usuario)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta autocontenida con las estadísticas del admin (Fase C).
 * ViewModel/estado propios: un fallo aquí no rompe la lista de usuarios.
 */
@Composable
private fun AdminStatsCard(
    modifier: Modifier = Modifier,
    viewModel: AdminStatsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            when (val current = state) {
                is AdminStatsUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )

                is AdminStatsUiState.Error -> Column {
                    Text(
                        text = current.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = viewModel::retry) {
                        Text("Reintentar")
                    }
                }

                is AdminStatsUiState.Success -> Column {
                    Text(
                        text = "Usuarios registrados: ${current.totalUsuarios}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    val masMarcado = current.masMarcado
                    Text(
                        text = if (masMarcado != null) {
                            "Juego más marcado: ${masMarcado.name} (${masMarcado.favoriteCount})"
                        } else {
                            "Juego más marcado: Sin datos todavía"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun UsuarioCard(usuario: UsuarioAdmin) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.email.ifBlank { "(sin correo)" },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Registrado: ${formatearFecha(usuario.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            RoleChip(usuario.role)
        }
    }
}

@Composable
private fun RoleChip(role: String) {
    val esAdmin = role == "admin"
    val bg = if (esAdmin) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (esAdmin) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text(
            text = role,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

private val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

/** Fecha legible del createdAt; "—" si es null (doc antiguo o timestamp sin resolver). */
private fun formatearFecha(timestamp: Timestamp?): String =
    timestamp?.toDate()?.let { formatoFecha.format(it) } ?: "—"
