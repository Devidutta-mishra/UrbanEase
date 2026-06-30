package com.example.urbanease.screens.owner.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.urbanease.model.Property
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.screens.owner.components.OwnerBottomNavigation
import com.example.urbanease.screens.owner.components.OwnerHeader
import com.example.urbanease.ui.theme.BrandGreen

@Composable
fun OwnerHome(
    navController: NavController,
    viewModel: OwnerHomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val properties = uiState.properties
    val isLoading = uiState.isLoading

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
        bottomBar = { OwnerBottomNavigation(navController, "home") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "My Properties",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Manage your property listings and view interested applicants.",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                item {
                    StatCard("TOTAL PROPERTIES", uiState.totalProperties.toString(), Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(2.dp))
                }

                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BrandGreen)
                        }
                    }
                } else if (properties.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.houseimage),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(24.dp)),
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.6f
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No properties yet",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Your listed properties will appear here.",
                                    color = Color.DarkGray,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { navController.navigate(UrbanScreens.LocationScreen.name) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandGreen
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("List Property", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(properties) { ad ->
                        PropertyCard(ad = ad) {
                            navController.navigate("${UrbanScreens.PropertyDetailScreen.name}/${ad.propertyId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, bgColor: Color = Color.White, textColor: Color = Color.Black) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.6f),
                lineHeight = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Composable
fun PropertyCard(ad: Property, onClick: () -> Unit) {
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
                    painter = if (ad.imageUrls.isNotEmpty()) rememberAsyncImagePainter(ad.imageUrls.first()) 
                              else painterResource(id = R.drawable.houseimage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                OwnerListingStatusBadge(
                    approvalStatus = ad.approvalStatus,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ad.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Black)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(ad.location, color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("MONTHLY RENT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("₹${ad.rent}/mo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrandGreen)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandGreen)
                        Text(" ${ad.rooms} BHK", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private data class ListingStatusVisual(
    val label: String,
    val background: Color,
    val content: Color,
    val icon: ImageVector
)

@Composable
fun OwnerListingStatusBadge(approvalStatus: String, modifier: Modifier = Modifier) {
    val visual = when (approvalStatus) {
        Property.APPROVAL_APPROVED ->
            ListingStatusVisual("Verified", Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.Verified)
        Property.APPROVAL_CHANGES_REQUESTED ->
            ListingStatusVisual("Action required", Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Warning)
        Property.APPROVAL_REJECTED ->
            ListingStatusVisual("Rejected", Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Warning)
        Property.APPROVAL_PENDING, Property.APPROVAL_UNDER_REVIEW ->
            ListingStatusVisual("In review", Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Default.Pending)
        Property.APPROVAL_HIDDEN ->
            ListingStatusVisual("Hidden", Color(0xFFF1F5F9), Color(0xFF455A64), Icons.Default.Pending)
        Property.APPROVAL_ARCHIVED ->
            ListingStatusVisual("Archived", Color(0xFFF1F5F9), Color(0xFF455A64), Icons.Default.Pending)
        else ->
            ListingStatusVisual("Draft", Color(0xFFF1F5F9), Color(0xFF455A64), Icons.Default.Pending)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(visual.background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = visual.label,
            tint = visual.content,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = visual.label,
            color = visual.content,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
