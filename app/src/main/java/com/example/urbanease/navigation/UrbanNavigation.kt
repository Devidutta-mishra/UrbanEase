package com.example.urbanease.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.screens.add.AdSummaryScreen
import com.example.urbanease.screens.add.LocationScreen
import com.example.urbanease.screens.add.PhotoScreen
import com.example.urbanease.screens.add.RentScreen
import com.example.urbanease.screens.details.DetailScreen
import com.example.urbanease.screens.home.AdminHome
import com.example.urbanease.screens.home.BachelorHome
import com.example.urbanease.screens.home.HomeScreen
import com.example.urbanease.screens.home.OwnerHome
import com.example.urbanease.screens.login.LoginScreen
import com.example.urbanease.screens.splash.SplashScreen


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun UrbanNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = UrbanScreens.SplashScreen.name) {

        composable(UrbanScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(UrbanScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }

        composable(UrbanScreens.HomeScreen.name) {
            HomeScreen(navController = navController)
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

        composable(
            route = "${UrbanScreens.DetailScreen.name}/{adId}",
            arguments = listOf(navArgument("adId") { type = NavType.StringType })
        ) { backStackEntry ->
            val adId = backStackEntry.arguments?.getString("adId")
            DetailScreen(navController = navController, adId = adId)
        }

        navigation(
            startDestination = UrbanScreens.LocationScreen.name,
            route = "post_ad_graph"
        ) {
            composable(UrbanScreens.LocationScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                LocationScreen(navController = navController, viewModel = viewModel)
            }

            composable(UrbanScreens.RentScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                RentScreen(navController = navController, viewModel = viewModel)
            }

            composable(UrbanScreens.PhotoScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                PhotoScreen(navController = navController, viewModel = viewModel)
            }

            composable(UrbanScreens.AdSummaryScreen.name) { navBackStackEntry ->
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("post_ad_graph")
                }
                val viewModel = hiltViewModel<PostAdViewModel>(parentEntry)
                AdSummaryScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}
