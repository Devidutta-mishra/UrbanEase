package com.example.urbanease.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.navigation.UrbanScreens
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BachelorHome(navController: NavHostController, viewModel: BachelorHomeViewModel = hiltViewModel()) {
    val ads = viewModel.ads.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(Unit) {
        viewModel.loadApprovedAds()
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "UrbanEase",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = Color(0xFF1A1C1E)
                            )
                        )
                        Text(
                            text = "Find your next home",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* Notification */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF42474E)
                            )
                        }
                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(UrbanScreens.LoginScreen.name) {
                                popUpTo(0)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color(0xFF42474E)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF38B6FF)
                )
            } else if (ads.isEmpty()) {
                Text(
                    text = "No properties found.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(ads) { ad ->
                        BachelorPropertyCard(ad) {
                            navController.navigate("${UrbanScreens.DetailScreen.name}/${ad.houseId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BachelorPropertyCard(ad: PropertyAd, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
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
