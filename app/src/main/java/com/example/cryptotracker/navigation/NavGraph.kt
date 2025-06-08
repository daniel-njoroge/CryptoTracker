package com.example.cryptotracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cryptotracker.screens.details.DetailsScreen
import com.example.cryptotracker.screens.home.HomeScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable(
            route = "details/{coinId}",
            arguments = listOf(navArgument("coinId") { type = NavType.StringType })
        ) { backStackEntry ->
            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
            DetailsScreen(coinId = coinId)
        }
    }
}