package com.example.urbanease.navigation

enum class UrbanScreens {
    LoginScreen,
    SplashScreen,
    CreateAccountScreen,
    HomeScreen,
    BachelorScreen,
    OwnerScreen,
    AdminScreen,
    AdminDetailScreen,
    SearchScreen,
    DetailScreen,
    UpdateScreen,
    RentScreen,
    PhotoScreen,
    LocationScreen,
    AdSummaryScreen,
    PropertyDetailScreen,
    EditPropertyScreen,
    RequestsScreen,
    RequestDetailScreen,
    SettingsScreen;

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
            AdminDetailScreen.name -> AdminDetailScreen
            LocationScreen.name -> LocationScreen
            RentScreen.name -> RentScreen
            PhotoScreen.name -> PhotoScreen
            AdSummaryScreen.name -> AdSummaryScreen
            PropertyDetailScreen.name -> PropertyDetailScreen
            EditPropertyScreen.name -> EditPropertyScreen
            RequestsScreen.name -> RequestsScreen
            RequestDetailScreen.name -> RequestDetailScreen
            SettingsScreen.name -> SettingsScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}
