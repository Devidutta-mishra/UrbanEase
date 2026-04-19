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
import com.example.urbanease.model.MUser
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.ui.theme.BrandGreen
import com.example.urbanease.navigation.UrbanScreens

@Composable
fun RequestsScreen(
    navController: NavController,
    viewModel: RequestsViewModel = hiltViewModel()
) {
    val requests = viewModel.requests.value
    val isLoading = viewModel.isLoading.value

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
        bottomBar = { OwnerBottomNavigation(navController, "requests") }
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
                        text = "Requests",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Review pending booking inquiries.",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (requests.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp), 
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .background(Color(0xFFE8F5E9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.MailOutline, 
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp),
                                        tint = BrandGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    "No requests yet",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Booking requests from tenants will show up here.",
                                    color = Color.DarkGray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(requests) { item ->
                        RequestCard(
                            requestDetail = item,
                            onAccept = { viewModel.updateRequestStatus(item.request.requestId, "accepted") },
                            onDecline = { viewModel.updateRequestStatus(item.request.requestId, "rejected") },
                            onClick = { navController.navigate("${UrbanScreens.RequestDetailScreen.name}/${item.request.requestId}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCard(
    requestDetail: RequestWithDetails,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image or placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2F1)),
                contentAlignment = Alignment.Center
            ) {
                if (requestDetail.user?.avatarUrl?.isNotEmpty() == true) {
                    Image(
                        painter = rememberAsyncImagePainter(requestDetail.user.avatarUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val initials = requestDetail.user?.displayName?.take(2)?.uppercase() ?: "U"
                    Text(initials, color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        requestDetail.user?.displayName ?: "Unknown User",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    if (requestDetail.request.status == "pending") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "NEW",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF6C00)
                            )
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(14.dp)) // Using Build as placeholder for Building icon
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(requestDetail.property?.title ?: "Unknown Property", color = Color.DarkGray, fontSize = 14.sp)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Oct 12, 2023", color = Color.Gray, fontSize = 12.sp) // Should be formatted from request.createdAt
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(requestDetail.user?.phoneNumber ?: "N/A", color = Color.Gray, fontSize = 12.sp)
                }

                if (requestDetail.request.status == "pending") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDecline,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Decline", color = Color.Black)
                        }
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen, // Industry Standard Green
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Accept", color = Color.White)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Status: ${requestDetail.request.status.uppercase()}",
                        color = if (requestDetail.request.status == "accepted") Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
