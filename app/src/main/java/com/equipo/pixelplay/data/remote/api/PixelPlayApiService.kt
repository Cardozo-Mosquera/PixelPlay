package com.equipo.pixelplay.data.remote.api

import com.equipo.pixelplay.data.remote.dto.GameDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Endpoints de FreeToGame (https://www.freetogame.com/api/).
 * La lista devuelve un array plano de juegos, sin envoltorio.
 */
interface PixelPlayApiService {

    @GET("games")
    suspend fun getGames(): List<GameDto>

    @GET("game")
    suspend fun getGame(@Query("id") id: Int): GameDto
}
