package com.example.urbanease.navigation

enum class UrbanScreens {
    LoginScreen,
    SplashScreen,
    CreateAccountScreen,
    HomeScreen,
    BachelorScreen,
    OwnerScreen,
    AdminScreen,
    SearchScreen,
    DetailScreen,
    UpdateScreen,
    RentScreen,
    PhotoScreen,
    LocationScreen,
    AdSummaryScreen,
    OwnerBookingsScreen;

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
            AdminScreen.name -> AdminScreen
            LocationScreen.name -> LocationScreen
            RentScreen.name -> RentScreen
            PhotoScreen.name -> PhotoScreen
            AdSummaryScreen.name -> AdSummaryScreen
            OwnerBookingsScreen.name -> OwnerBookingsScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}
