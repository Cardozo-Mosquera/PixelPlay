package com.equipo.pixelplay.navigation

/** Rutas centralizadas de navegación. Sin strings de ruta sueltos por la app. */
object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val ARG_GAME_ID = "gameId"
    const val DETAIL = "detail/{$ARG_GAME_ID}"

    fun detailRoute(id: Int) = "detail/$id"
}
