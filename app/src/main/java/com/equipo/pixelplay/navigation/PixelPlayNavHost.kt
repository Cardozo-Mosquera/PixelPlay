package com.equipo.pixelplay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.equipo.pixelplay.ui.detail.DetailScreen
import com.equipo.pixelplay.ui.home.HomeScreen

/** Único NavHost de la app. */
@Composable
fun PixelPlayNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onGameClick = { id -> navController.navigate(Routes.detailRoute(id)) }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(Routes.ARG_GAME_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt(Routes.ARG_GAME_ID) ?: 0
            DetailScreen(
                gameId = gameId,
                onBack = { navController.navigateUp() }
            )
        }
    }
}
