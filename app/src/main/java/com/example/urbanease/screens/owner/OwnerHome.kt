package com.example.urbanease.screens.owner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.navigation.UrbanScreens

@Composable
fun OwnerHome(
    navController: NavController,
    viewModel: OwnerHomeViewModel = hiltViewModel()
) {
    val ads = viewModel.ads.value
    val isLoading = viewModel.isLoading.value

    Scaffold(
        bottomBar = { OwnerBottomNavigation(navController, "home") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            OwnerHeader()
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "My Properties",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage your portfolio and track listing statuses.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    StatsGrid(viewModel)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (ads.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No properties found.")
                        }
                    }
                } else {
                    items(ads) { ad ->
                        PropertyCard(ad = ad) {
                            navController.navigate("${UrbanScreens.PropertyDetailScreen.name}/${ad.houseId}")
                        }
                    }
                }
            }
        }
    }
}

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
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF00796B))
            Spacer(modifier = Modifier.width(8.dp))
            Text("UrbanEase", fontWeight = FontWeight.Bold, color = Color(0xFF00796B), fontSize = 20.sp)
        }
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Placeholder
            contentDescription = "Profile",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun StatsGrid(viewModel: OwnerHomeViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("TOTAL\nPROPERTIES", viewModel.totalProperties.value.toString(), Modifier.weight(1f))
            StatCard("APPROVED", viewModel.approvedProperties.value.toString(), Modifier.weight(1f), Color(0xFFE8F5E9), Color(0xFF2E7D32))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("PENDING", viewModel.pendingProperties.value.toString(), Modifier.weight(1f), Color(0xFFFFF3E0), Color(0xFFEF6C00))
            StatCard("REJECTED", viewModel.rejectedProperties.value.toString(), Modifier.weight(1f), Color(0xFFFFEBEE), Color(0xFFC62828))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, bgColor: Color = Color.White, textColor: Color = Color.Black) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Composable
fun PropertyCard(ad: PropertyAd, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(ad.imageUrls.firstOrNull()),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                StatusBadge(ad)
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ad.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(ad.location, color = Color.Gray, fontSize = 12.sp)
                }

                if (ad.status == "rejected" && ad.adminNote.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Reason: ${ad.adminNote}",
                            modifier = Modifier.padding(8.dp),
                            color = Color(0xFFC62828),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("MONTHLY RENT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("$${ad.rent}/mo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00796B))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(16.dp)) // Placeholder for bed icon
                        Text(" ${ad.rooms}", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(16.dp)) // Placeholder for bath icon
                        Text(" ${ad.bathrooms}", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(ad: PropertyAd) {
    val (text, color, bgColor) = when {
        ad.isApproved -> Triple("APPROVED", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        ad.status == "rejected" -> Triple("REJECTED", Color(0xFFC62828), Color(0xFFFFEBEE))
        else -> Triple("PENDING", Color(0xFFEF6C00), Color(0xFFFFF3E0))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OwnerBottomNavigation(navController: NavController, currentScreen: String) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == "home",
            onClick = { navController.navigate(UrbanScreens.OwnerScreen.name) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add") },
            label = { Text("Add") },
            selected = currentScreen == "add",
            onClick = { navController.navigate(UrbanScreens.LocationScreen.name) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Requests") },
            label = { Text("Requests") },
            selected = currentScreen == "requests",
            onClick = { navController.navigate(UrbanScreens.RequestsScreen.name) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentScreen == "settings",
            onClick = { navController.navigate(UrbanScreens.SettingsScreen.name) }
        )
    }
}
