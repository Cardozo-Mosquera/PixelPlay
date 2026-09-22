package com.equipo.pixelplay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.equipo.pixelplay.ui.detail.DetailScreen
import com.equipo.pixelplay.ui.favorites.FavoritesScreen
import com.equipo.pixelplay.ui.home.HomeScreen
import com.equipo.pixelplay.ui.profile.ProfileScreen

/** Único NavHost de la app. */
@Composable
fun PixelPlayNavHost(
    userEmail: String?,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onGameClick = { id -> navController.navigate(Routes.detailRoute(id)) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onFavoritesClick = { navController.navigate(Routes.FAVORITES) }
            )
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(
                onGameClick = { id -> navController.navigate(Routes.detailRoute(id)) },
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                userEmail = userEmail,
                onLogout = onLogout,
                onBack = { navController.navigateUp() }
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
