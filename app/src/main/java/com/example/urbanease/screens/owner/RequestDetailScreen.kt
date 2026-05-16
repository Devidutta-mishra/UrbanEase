package com.example.urbanease.screens.owner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.urbanease.ui.theme.BrandGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    navController: NavController,
    requestId: String,
    viewModel: RequestDetailViewModel = hiltViewModel()
) {
    val detail = viewModel.requestDetail.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(requestId) {
        viewModel.loadRequestDetail(requestId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text("Applicant Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = BrandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (detail != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bachelor Profile Section
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2F1)),
                    contentAlignment = Alignment.Center
                ) {
                    if (detail.user?.avatarUrl?.isNotEmpty() == true) {
                        Image(
                            painter = rememberAsyncImagePainter(detail.user.avatarUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val initials = detail.user?.displayName?.take(2)?.uppercase() ?: "U"
                        Text(initials, color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 40.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    detail.user?.displayName ?: "Unknown User",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Tenant Inquiry", color = Color.Gray)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contact Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = BrandGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(detail.user?.phoneNumber ?: "Not provided", fontSize = 16.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = BrandGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(detail.user?.email?.ifBlank { "Not provided" } ?: "Not provided", fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Property Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Requested Property", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = BrandGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(detail.property?.title ?: "Unknown Property", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Text(detail.property?.location ?: "Unknown Location", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Text(
                    "You can contact this applicant directly using the information provided above.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
