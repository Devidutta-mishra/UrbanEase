package com.example.urbanease.screens.owner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import com.example.urbanease.R
import com.example.urbanease.navigation.UrbanScreens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        bottomBar = { OwnerBottomNavigation(navController, "settings") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
        ) {
            OwnerHeader()
            
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
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = user?.displayName ?: "Alex Mercer",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Owner • 3 Properties", // Dynamic properties count can be added
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Settings Options
                SettingsItem(Icons.Default.Person, "Edit Profile")
                SettingsItem(Icons.Default.Notifications, "Notification Preferences")
                SettingsItem(Icons.Default.AccountBox, "Payment Methods")
                SettingsItem(Icons.Default.Info, "Help & Support")
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Logout Button
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(UrbanScreens.LoginScreen.name) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFC62828))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Logout", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
                    color = Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
