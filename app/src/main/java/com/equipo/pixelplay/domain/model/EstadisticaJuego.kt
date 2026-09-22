package com.equipo.pixelplay.domain.model

/**
 * Estadística de un juego para el panel de admin (Fase C).
 * Deriva de gameStats/{gameId} → { name, favoriteCount }. Solo lectura.
 */
data class EstadisticaJuego(
    val gameId: Int,
    val name: String,
    val favoriteCount: Int
)
