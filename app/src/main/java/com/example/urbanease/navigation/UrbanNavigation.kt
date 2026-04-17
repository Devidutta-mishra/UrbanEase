package com.example.urbanease.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.screens.admin.AdminHome
import com.example.urbanease.screens.bachelor.BachelorHome
import com.example.urbanease.screens.bachelor.details.DetailScreen
import com.example.urbanease.screens.login.LoginScreen
import com.example.urbanease.screens.owner.OwnerHome
import com.example.urbanease.screens.owner.PropertyDetailScreen
import com.example.urbanease.screens.owner.RequestDetailScreen
import com.example.urbanease.screens.owner.RequestsScreen
import com.example.urbanease.screens.owner.SettingsScreen
import com.example.urbanease.screens.owner.add.AdSummaryScreen
import com.example.urbanease.screens.owner.add.LocationScreen
import com.example.urbanease.screens.owner.add.PhotoScreen
import com.example.urbanease.screens.owner.add.RentScreen
import com.example.urbanease.screens.splash.SplashScreen

@Composable
fun UrbanNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = UrbanScreens.SplashScreen.name
    ) {
        composable(UrbanScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(UrbanScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }

        composable(UrbanScreens.BachelorScreen.name) {
            BachelorHome(navController = navController)
        }

        composable(UrbanScreens.OwnerScreen.name) {
            OwnerHome(navController = navController)
        }

        composable(UrbanScreens.AdminScreen.name) {
            AdminHome(navController = navController)
        }

        composable(UrbanScreens.RequestsScreen.name) {
            RequestsScreen(navController = navController)
        }

        composable(
            route = "${UrbanScreens.PropertyDetailScreen.name}/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(navController = navController, propertyId = propertyId)
        }

        composable(
            route = "${UrbanScreens.RequestDetailScreen.name}/{requestId}",
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            RequestDetailScreen(navController = navController, requestId = requestId)
        }

        composable(UrbanScreens.SettingsScreen.name) {
            SettingsScreen(navController = navController)
        }

        composable(
            route = "${UrbanScreens.DetailScreen.name}/{houseId}",
            arguments = listOf(navArgument("houseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId") ?: ""
            DetailScreen(navController = navController, houseId = houseId)
        }

        navigation(
            startDestination = UrbanScreens.LocationScreen.name,
            route = "post_ad_graph"
        ) {
            composable(UrbanScreens.LocationScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                LocationScreen(navController = navController, viewModel = viewModel)
            }

            composable(UrbanScreens.RentScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                RentScreen(navController = navController, viewModel = viewModel)
            }

            composable(UrbanScreens.PhotoScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                PhotoScreen(navController = navController, viewModel = viewModel)
            }

            composable(UrbanScreens.AdSummaryScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                AdSummaryScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}
