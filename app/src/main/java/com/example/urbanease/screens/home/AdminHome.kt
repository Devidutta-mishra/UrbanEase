package com.example.urbanease.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.model.MUser
import com.example.urbanease.navigation.UrbanScreens
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHome(navController: NavHostController, viewModel: AdminHomeViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending Ads", "All Ads", "Owners", "Bachelors")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(UrbanScreens.LoginScreen.name) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 16.dp) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> PendingAdsTab(viewModel) { selectedTab = 1 }
                1 -> AllAdsTab(viewModel)
                2 -> UsersTab(viewModel.owners.value, "Owners")
                3 -> UsersTab(viewModel.bachelors.value, "Bachelors")
            }
        }
    }
}

@Composable
fun PendingAdsTab(viewModel: AdminHomeViewModel, onActionDone: () -> Unit) {
    val pendingAds = viewModel.ads.value.filter { !it.isApproved }
    if (pendingAds.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending ads")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(pendingAds) { ad ->
                AdminAdCard(
                    ad,
                    onApprove = {
                        viewModel.approveAd(ad.houseId)
                        onActionDone()
                    },
                    onReject = {
                        viewModel.rejectAd(ad.houseId)
                        onActionDone()
                    }
                )
            }
        }
    }
}

@Composable
fun AllAdsTab(viewModel: AdminHomeViewModel) {
    val allAds = viewModel.ads.value
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(allAds) { ad ->
            AdminAdCard(ad, showActions = false)
        }
    }
}

@Composable
fun UsersTab(users: List<MUser>, title: String) {
    if (users.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No $title found")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Name: ${user.displayName}", fontWeight = FontWeight.Bold)
                        Text(text = "Email: ${user.userId}")
                        Text(text = "Role: ${user.role}")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAdCard(ad: PropertyAd, onApprove: () -> Unit = {}, onReject: () -> Unit = {}, showActions: Boolean = true) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            if (ad.imageUrls.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(ad.imageUrls.first()),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = ad.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Location: ${ad.location}")
                Text(text = "Rent: ₹${ad.rent}")
                Text(text = "Status: ${if (ad.isApproved) "Approved" else "Pending"}")
                
                if (showActions && !ad.isApproved) {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                        Button(onClick = onReject, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("Reject")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onApprove, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                            Text("Approve")
                        }
                    }
                }
            }
        }
    }
}
