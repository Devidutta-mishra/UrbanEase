package com.example.urbanease.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.urbanease.components.OwnerAppbar
import com.example.urbanease.components.OwnerBottomNavigationBar
import com.example.urbanease.data.Booking
import com.example.urbanease.data.PropertyAd
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OwnerHome(navController: NavController, viewModel: OwnerHomeViewModel = hiltViewModel()) {
    val ads = viewModel.ads.value
    val bookings = viewModel.bookings.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(key1 = true) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            OwnerAppbar(navController = navController)
        },
        bottomBar = {
            OwnerBottomNavigationBar(
                onHomeClick = { viewModel.loadData() },
                onAddClick = { navController.navigate("post_ad_graph") },
                onSettingsClick = { /* Settings */ },
                currentScreen = "Home"
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section for Booking Requests
            if (bookings.isNotEmpty()) {
                item {
                    Text(
                        text = "Booking Requests",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
                items(bookings) { booking ->
                    BookingRequestCard(booking, viewModel)
                }
            }

            // Section for My Properties
            item {
                Text(
                    text = "My Properties",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (isLoading && ads.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF38B6FF))
                    }
                }
            } else if (ads.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No properties found", color = Color.Gray)
                    }
                }
            } else {
                items(ads) { ad ->
                    OwnerPropertyCard(ad)
                }
            }
        }
    }
}

@Composable
fun BookingRequestCard(booking: Booking, viewModel: OwnerHomeViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = booking.houseTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Interested: ${booking.bachelorEmail}", fontSize = 13.sp, color = Color.Gray)
                }
                Text(
                    text = booking.status.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when(booking.status) {
                        "approved" -> Color(0xFF2E7D32)
                        "rejected" -> Color.Red
                        else -> Color(0xFFF57C00)
                    }
                )
            }
            
            if (booking.status == "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = { viewModel.rejectBooking(booking) },
                        modifier = Modifier.background(Color(0xFFFFEBEE), CircleShape).size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { viewModel.approveBooking(booking) },
                        modifier = Modifier.background(Color(0xFFE8F5E9), CircleShape).size(36.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerPropertyCard(ad: PropertyAd) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (ad.imageUrls.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(ad.imageUrls.first()),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.istockphoto_856794670_612x612),
                        contentDescription = "Default Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = "${ad.rooms}BHK Apartment",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${ad.location}, Odisha",
                        fontSize = 16.sp,
                        color = Color(0xFF74777F)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "₹${String.format(Locale.getDefault(), "%,d", ad.rent)}/month",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006874)
                )
            }
        }
    }
}
