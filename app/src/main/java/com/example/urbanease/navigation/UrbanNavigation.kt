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
import com.example.urbanease.ui.animations.ScreenTransitions

@Composable
fun UrbanNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = UrbanScreens.SplashScreen.name
    ) {
        composable(UrbanScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(
            route = UrbanScreens.LoginScreen.name,
            enterTransition = { ScreenTransitions.fadeInTransition() },
            exitTransition = { ScreenTransitions.fadeOutTransition() }
        ) {
            LoginScreen(navController = navController)
        }

        composable(
            route = UrbanScreens.BachelorScreen.name,
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) {
            BachelorHome(navController = navController)
        }

        composable(
            route = UrbanScreens.OwnerScreen.name,
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) {
            OwnerHome(navController = navController)
        }

        composable(
            route = UrbanScreens.AdminScreen.name,
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) {
            AdminHome(navController = navController)
        }

        composable(
            route = UrbanScreens.RequestsScreen.name,
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) {
            RequestsScreen(navController = navController)
        }

        composable(
            route = "${UrbanScreens.PropertyDetailScreen.name}/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType }),
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(navController = navController, propertyId = propertyId)
        }

        composable(
            route = "${UrbanScreens.RequestDetailScreen.name}/{requestId}",
            arguments = listOf(navArgument("requestId") { type = NavType.StringType }),
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            RequestDetailScreen(navController = navController, requestId = requestId)
        }

        composable(
            route = UrbanScreens.SettingsScreen.name,
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) {
            SettingsScreen(navController = navController)
        }

        composable(
            route = "${UrbanScreens.DetailScreen.name}/{houseId}",
            arguments = listOf(navArgument("houseId") { type = NavType.StringType }),
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId") ?: ""
            DetailScreen(navController = navController, houseId = houseId)
        }

        navigation(
            startDestination = UrbanScreens.LocationScreen.name,
            route = "post_ad_graph",
            enterTransition = { ScreenTransitions.slideInLeftTransition() },
            exitTransition = { ScreenTransitions.slideOutLeftTransition() }
        ) {
            composable(
                route = UrbanScreens.LocationScreen.name,
                enterTransition = { ScreenTransitions.fadeInTransition() },
                exitTransition = { ScreenTransitions.fadeOutTransition() }
            ) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                LocationScreen(navController = navController, viewModel = viewModel)
            }

            composable(
                route = UrbanScreens.RentScreen.name,
                enterTransition = { ScreenTransitions.slideInLeftTransition() },
                exitTransition = { ScreenTransitions.slideOutLeftTransition() }
            ) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                RentScreen(navController = navController, viewModel = viewModel)
            }

            composable(
                route = UrbanScreens.PhotoScreen.name,
                enterTransition = { ScreenTransitions.slideInLeftTransition() },
                exitTransition = { ScreenTransitions.slideOutLeftTransition() }
            ) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                PhotoScreen(navController = navController, viewModel = viewModel)
            }

            composable(
                route = UrbanScreens.AdSummaryScreen.name,
                enterTransition = { ScreenTransitions.slideInLeftTransition() },
                exitTransition = { ScreenTransitions.slideOutLeftTransition() }
            ) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                AdSummaryScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}
