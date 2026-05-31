package com.example.urbanease.screens.owner.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.urbanease.R
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.BrandGreen

@Composable
fun OwnerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("UrbanEase", fontWeight = FontWeight.Bold, color = Color(0xFF050234), fontSize = 20.sp)
        }
        Image(
            painter = painterResource(id = R.drawable.profile), // Placeholder
            contentDescription = "Profile",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun OwnerBottomNavigation(navController: NavController, currentScreen: String) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val selectedColor = BrandGreen
        val unselectedColor = Color.DarkGray

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontWeight = if(currentScreen == "home") FontWeight.Bold else FontWeight.Normal) },
            selected = currentScreen == "home",
            onClick = { navController.navigate(UrbanScreens.OwnerScreen.name) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = selectedColor.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add") },
            label = { Text("Add", fontWeight = if(currentScreen == "add") FontWeight.Bold else FontWeight.Normal) },
            selected = currentScreen == "add",
            onClick = { navController.navigate(UrbanScreens.LocationScreen.name) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = selectedColor.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.People, contentDescription = "Applicants") },
            label = { Text("Applicants", fontWeight = if(currentScreen == "requests") FontWeight.Bold else FontWeight.Normal) },
            selected = currentScreen == "requests",
            onClick = { navController.navigate(UrbanScreens.RequestsScreen.name) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = selectedColor.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings", fontWeight = if(currentScreen == "settings") FontWeight.Bold else FontWeight.Normal) },
            selected = currentScreen == "settings",
            onClick = { navController.navigate(UrbanScreens.SettingsScreen.name) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = selectedColor.copy(alpha = 0.1f)
            )
        )
    }
}
