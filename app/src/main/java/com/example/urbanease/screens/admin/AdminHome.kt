package com.example.urbanease.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                0 -> PendingAdsTab(viewModel)
                1 -> AllAdsTab(viewModel)
                2 -> UsersTab(viewModel.owners.value, "Owners")
                3 -> UsersTab(viewModel.bachelors.value, "Bachelors")
            }
        }
    }
}

@Composable
fun PendingAdsTab(viewModel: AdminHomeViewModel) {
    val pendingAds = viewModel.ads.value.filter { it.status == "available" && !it.isApproved }
    if (pendingAds.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending ads")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(pendingAds) { ad ->
                AdminAdCard(
                    ad,
                    onApprove = { viewModel.approveAd(ad.houseId) },
                    onReject = { viewModel.rejectAd(ad.houseId) }
                )
            }
        }
    }
}

@Composable
fun AllAdsTab(viewModel: AdminHomeViewModel) {
    val allAds = viewModel.ads.value
    if (allAds.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No ads found")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(allAds) { ad ->
                AdminAdCard(ad, showActions = true, onApprove = { viewModel.approveAd(ad.houseId) }, onReject = { viewModel.rejectAd(ad.houseId) })
            }
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
            Box {
                if (ad.imageUrls.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(ad.imageUrls.first()),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Status Badge
                Surface(
                    color = when {
                        ad.isApproved -> Color(0xFFE8F5E9)
                        ad.status == "rejected" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF3E0)
                    },
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = when {
                            ad.isApproved -> "Approved"
                            ad.status == "rejected" -> "Rejected"
                            else -> "Pending"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = when {
                            ad.isApproved -> Color(0xFF2E7D32)
                            ad.status == "rejected" -> Color.Red
                            else -> Color(0xFFEF6C00)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = ad.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Location: ${ad.location}", color = Color.Gray)
                Text(text = "Rent: ₹${ad.rent}", fontWeight = FontWeight.SemiBold, color = Color(0xFF006874))
                
                if (showActions) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (ad.status != "rejected" && !ad.isApproved) {
                            Button(
                                onClick = onReject,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reject")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onApprove,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Approve")
                            }
                        } else if (ad.status == "rejected") {
                             Button(
                                onClick = onApprove,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Approve Anyway")
                            }
                        } else if (ad.isApproved) {
                            Button(
                                onClick = onReject,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Revoke Approval")
                            }
                        }
                    }
                }
            }
        }
    }
}
