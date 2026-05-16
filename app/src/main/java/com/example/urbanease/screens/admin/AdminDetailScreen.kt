package com.example.urbanease.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.model.House
import com.example.urbanease.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDetailScreen(
    navController: NavController,
    propertyId: String,
    viewModel: AdminHomeViewModel = hiltViewModel()
) {
    val property = viewModel.ads.value.find { it.houseId == propertyId }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                title = {
                    Text(
                        "Property Details",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.Black, fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { padding ->
        if (property != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Image Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = if (property.imageUrls.isNotEmpty()) rememberAsyncImagePainter(property.imageUrls[0])
                        else painterResource(id = R.drawable.houseimage),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = if (property.imageUrls.size > 1) rememberAsyncImagePainter(property.imageUrls[1])
                            else painterResource(id = R.drawable.houseimage),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Image(
                            painter = if (property.imageUrls.size > 2) rememberAsyncImagePainter(property.imageUrls[2])
                            else painterResource(id = R.drawable.houseimage),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    property.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = PrimaryTeal)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(property.location, color = TextGrey, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("MONTHLY RENT", color = TextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "₹${property.rent}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feature Chips
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeatureChip(icon = Icons.Default.Bed, label = "${property.rooms} Beds")
                    FeatureChip(icon = Icons.Default.Bathtub, label = "${property.bathrooms} Baths")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeatureChip(icon = Icons.Default.Chair, label = property.furnishing)
                    FeatureChip(icon = Icons.Default.SquareFoot, label = "Available") 
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Property Description",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        property.description,
                        modifier = Modifier.padding(20.dp),
                        color = Color.DarkGray,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FeatureChip(icon: ImageVector, label: String) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
