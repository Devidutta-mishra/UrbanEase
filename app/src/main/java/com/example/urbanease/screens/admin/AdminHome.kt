package com.example.urbanease.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.urbanease.model.House
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHome(navController: NavHostController, viewModel: AdminHomeViewModel = hiltViewModel()) {
    var selectedFilter by remember { mutableStateOf("All") }
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
                        value = viewModel.owners.value.size.toString(),
                        badge = "+12%",
                        iconBg = Color(0xFFE0F2F1)
                    )
                    StatsCard(
                        icon = Icons.Default.Person,
                        title = "Total Bachelors",
                        value = viewModel.bachelors.value.size.toString(),
                        badge = "+5%",
                        iconBg = Color(0xFFE3F2FD)
                    )
                    StatsCard(
                        icon = Icons.Default.Home,
                        title = "Total Listings",
                        value = viewModel.ads.value.size.toString(),
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
            val filteredAds = when (selectedFilter) {
                "Owners" -> viewModel.ads.value 
                "Bachelors" -> viewModel.ads.value 
                else -> viewModel.ads.value
            }

            items(filteredAds.take(10)) { ad ->
                ListingCard(
                    ad = ad,
                    onClick = {
                        navController.navigate("${UrbanScreens.AdminDetailScreen.name}/${ad.houseId}")
                    }
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
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
fun ListingCard(ad: House, onClick: () -> Unit) {
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
                    color = StatusApproved,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Live",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = StatusApprovedText,
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
