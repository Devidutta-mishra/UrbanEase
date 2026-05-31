package com.example.urbanease.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.model.Property
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.BackgroundLight
import com.example.urbanease.ui.theme.PrimaryTeal
import com.example.urbanease.ui.theme.StatusApproved
import com.example.urbanease.ui.theme.StatusApprovedText
import com.example.urbanease.ui.theme.TextGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHome(navController: NavHostController, viewModel: AdminHomeViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState
    var selectedFilter by remember { mutableStateOf("All") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val filters = listOf("All", "Owners", "Bachelors")

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Open menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Black)
                    }
                    IconButton(onClick = {
                        // Profile click
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Stats Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatsCard(
                        icon = Icons.Default.Groups,
                        title = "Total Owners",
                        value = uiState.owners.size.toString(),
                        badge = "+12%",
                        iconBg = Color(0xFFE0F2F1)
                    )
                    StatsCard(
                        icon = Icons.Default.Person,
                        title = "Total Bachelors",
                        value = uiState.bachelors.size.toString(),
                        badge = "+5%",
                        iconBg = Color(0xFFE3F2FD)
                    )
                    StatsCard(
                        icon = Icons.Default.Home,
                        title = "Total Listings",
                        value = uiState.properties.size.toString(),
                        iconBg = Color(0xFFE8F5E9)
                    )
                }
            }

            // Recent Activity Section
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        "Recent Activity",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FilterChips(
                        filters = filters,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                }
            }

            // Property List
            val filteredProperties = when (selectedFilter) {
                "Owners" -> uiState.properties 
                "Bachelors" -> uiState.properties 
                else -> uiState.properties
            }

            items(filteredProperties.take(10)) { ad ->
                ListingCard(
                    ad = ad,
                    onClick = {
                        navController.navigate("${UrbanScreens.AdminDetailScreen.name}/${ad.propertyId}")
                    }
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(UrbanScreens.LoginScreen.name) {
                            popUpTo(0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun StatsCard(
    icon: ImageVector,
    title: String,
    value: String,
    badge: String? = null,
    iconBg: Color,
    badgeColor: Color = Color(0xFF4CAF50)
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = PrimaryTeal)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(title, color = TextGrey, fontSize = 14.sp)
                    Text(
                        value,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
                if (badge != null) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (badge.startsWith("+")) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = badgeColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                badge,
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                modifier = Modifier.clickable { onFilterSelected(filter) },
                color = if (isSelected) PrimaryTeal else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ListingCard(ad: Property, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.padding(12.dp)) {
                Image(
                    painter = if (ad.imageUrls.isNotEmpty()) rememberAsyncImagePainter(ad.imageUrls.first())
                    else painterResource(id = R.drawable.houseimage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    color = when (ad.approvalStatus) {
                        Property.APPROVAL_APPROVED -> StatusApproved
                        Property.APPROVAL_REJECTED -> Color(0xFFFFE0E0)
                        else -> Color(0xFFFFF4CC)
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = ad.approvalStatus.replace("_", " ").uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = when (ad.approvalStatus) {
                            Property.APPROVAL_APPROVED -> StatusApprovedText
                            Property.APPROVAL_REJECTED -> Color(0xFF991B1B)
                            else -> Color(0xFF8A4B08)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Text(
                    ad.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextGrey)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(ad.location, color = TextGrey, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Availability: ${ad.propertyStatus.replace("_", " ").uppercase()}",
                    color = TextGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${ad.rent} /mo",
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
