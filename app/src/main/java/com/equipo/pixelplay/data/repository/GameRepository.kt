package com.equipo.pixelplay.data.repository

import com.equipo.pixelplay.data.remote.api.PixelPlayApiService
import com.equipo.pixelplay.data.remote.api.RetrofitClient
import com.equipo.pixelplay.domain.model.Game
import com.equipo.pixelplay.domain.model.toGame

/**
 * Punto único de acceso a los datos de juegos.
 * Convierte los DTO de red al modelo de dominio antes de exponerlos.
 */
class GameRepository(
    private val api: PixelPlayApiService = RetrofitClient.api
) {
    suspend fun getGames(): List<Game> = api.getGames().map { it.toGame() }

    suspend fun getGame(id: Int): Game = api.getGame(id).toGame()
}
