package com.example.urbanease.navigation

import com.example.urbanease.screens.add.LocationScreen
import com.example.urbanease.screens.splash.SplashScreen

enum class UrbanScreens {
    LoginScreen,
    SplashScreen,
    CreateAccountScreen,
    HomeScreen,
    BachelorScreen,
    OwnerScreen,
    SearchScreen,
    DetailScreen,
    UpdateScreen,
    RentScreen,
    PhotoScreen,
    LocationScreen;

    companion object {
        fun fromRoute(route: String?) : UrbanScreens = when(route?.substringBefore("/")){
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            CreateAccountScreen.name -> CreateAccountScreen
            HomeScreen.name -> HomeScreen
            SearchScreen.name -> SearchScreen
            DetailScreen.name -> DetailScreen
            UpdateScreen.name -> UpdateScreen
            BachelorScreen.name -> BachelorScreen
            OwnerScreen.name -> OwnerScreen
            LocationScreen.name -> LocationScreen
            RentScreen.name -> RentScreen
            PhotoScreen.name -> PhotoScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}
