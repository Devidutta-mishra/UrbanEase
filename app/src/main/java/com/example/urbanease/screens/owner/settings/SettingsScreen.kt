package com.example.urbanease.screens.owner.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.screens.owner.components.OwnerBottomNavigation
import com.example.urbanease.screens.owner.components.OwnerHeader
import com.example.urbanease.ui.theme.BrandGreen

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                OwnerHeader()
            }
        },
        bottomBar = { OwnerBottomNavigation(navController, "settings") }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.error, color = Color.Red)
                }
            }
            uiState.user != null -> {
                val user = uiState.user
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF8F9FA))
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = if (user.avatarUrl.isNotEmpty())
                                        rememberAsyncImagePainter(user.avatarUrl)
                                    else painterResource(id = R.drawable.profile),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = user.displayName.ifBlank { "User" },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "${user.role.replaceFirstChar { it.uppercase() }} • ${user.email}",
                                    color = Color.DarkGray,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Settings Options
                        SettingsItem(Icons.Default.Person, "Edit Profile") {
                            navController.navigate(UrbanScreens.EditProfileScreen.name)
                        }
                        SettingsItem(Icons.Default.Notifications, "Notification Preferences")
                        SettingsItem(Icons.Default.AccountBox, "Payment Methods")
                        SettingsItem(Icons.Default.Info, "Help & Support")

                        Spacer(modifier = Modifier.height(32.dp))

                        // Logout Button
                        Button(
                            onClick = {
                                viewModel.logout()
                                navController.navigate(UrbanScreens.LoginScreen.name) {
                                    popUpTo(0)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Logout", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Black)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
