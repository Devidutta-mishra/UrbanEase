package com.example.urbanease.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.urbanease.components.Appbar
import com.example.urbanease.components.BottomNavigationBar
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.utils.Colors
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OwnerHome(navController: NavController) {
    val ownerName = remember {
        FirebaseAuth.getInstance().currentUser?.email?.let { extractNameFromEmail(it) } ?: "Owner"
    }
    Box(
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            topBar = {
                Appbar(title = "UrbanEase", navController = navController)
            },
            bottomBar = {
                BottomNavigationBar(
                    onMyAdsClick = {
                        // Navigate to My Ads screen
                        // navController.navigate(UrbanScreens.MyAdsScreen.name)
                    },
                    onAddClick = {
                        navController.navigate(UrbanScreens.LocationScreen.name)
                    },
                    onSettingsClick = {
                        // Navigate to Settings screen
                        // navController.navigate(UrbanScreens.SettingsScreen.name)
                    }, myAdsSelected = true
                )
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            HorizontalDivider(modifier = Modifier.padding(top = 50.dp))
            Surface(
                modifier = Modifier.padding(top = 60.dp, start = 15.dp),
                color = Color.Transparent
            ) {
                Column() {

                }
            }
        }
    }
}

private fun extractNameFromEmail(email: String): String {
    val namePart = email.substringBefore('@')
    return namePart
        .replace(".", " ")
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
}