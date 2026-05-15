package com.example.urbanease.screens.bachelor

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.data.Booking
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.model.MUser
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.BrandGreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BachelorHome(
    navController: NavHostController,
    viewModel: BachelorHomeViewModel = hiltViewModel()
) {
    var currentTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentTab) { currentTab = it }
        },
        containerColor = Color.White
    ) { paddingValues ->
        when (currentTab) {
            0 -> BachelorHomeScreen(navController, viewModel, paddingValues)
            1 -> DashboardScreen(paddingValues)
            2 -> MyBookingsScreen(paddingValues)
            3 -> BachelorProfileScreen(navController, paddingValues)
        }
    }
}

@Composable
fun BachelorHomeScreen(
    navController: NavHostController,
    viewModel: BachelorHomeViewModel,
    paddingValues: PaddingValues
) {
    val ads = viewModel.ads.value
    val isLoading = viewModel.isLoading.value
    var selectedFilter by remember { mutableStateOf("Location") }

    LaunchedEffect(Unit) {
        viewModel.loadApprovedAds()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(paddingValues)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF2C3E50),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "UrbanEase",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1D52)
                )
            }
        }

        // Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filters = listOf("Location", "Rent Range", "Rooms")
            items(filters) { filter ->
                FilterChip(
                    text = filter,
                    isSelected = selectedFilter == filter,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Content
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BrandGreen
                )
            } else if (ads.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.houseimage),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            contentScale = ContentScale.Crop,
                            alpha = 0.5f
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            "No properties found",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Try adjusting your filters or search criteria.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
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
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) BrandGreen else Color(0xFFF2F2F2),
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color(0xFF2C3E50),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BachelorPropertyCard(ad: PropertyAd, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = if (ad.imageUrls.isNotEmpty()) rememberAsyncImagePainter(ad.imageUrls.first())
                    else painterResource(id = R.drawable.houseimage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Favorite Icon
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = BrandGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Tag
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    color = BrandGreen,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "VERIFIED",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ad.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,d", ad.rent)}/mo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                }

                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ad.location,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoBadge(Icons.Default.Home, "${ad.rooms} BHK")
                    InfoBadge(Icons.Default.Info, "${ad.bathrooms} Bath")
                }
            }
        }
    }
}

@Composable
fun InfoBadge(icon: ImageVector, text: String) {
    Surface(
        color = Color(0xFFF8F9FA),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C3E50)
            )
        }
    }
}

@Composable
fun MyBookingsScreen(
    paddingValues: PaddingValues,
    viewModel: BachelorBookingsViewModel = hiltViewModel()
) {
    val approvedBookings = viewModel.bookings.value.filter { it.request.status.lowercase() == "approved" || it.request.status.lowercase() == "accepted" }
    val isLoading = viewModel.isLoading.value
    var selectedBooking by remember { mutableStateOf<BachelorBookingUIModel?>(null) }
    var ownerDetails by remember { mutableStateOf<MUser?>(null) }

    LaunchedEffect(selectedBooking) {
        if (selectedBooking != null && ownerDetails == null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(selectedBooking!!.request.ownerId)
                .get()
                .addOnSuccessListener { doc ->
                    ownerDetails = doc.toObject(MUser::class.java)
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .padding(paddingValues)
    ) {
        Text(
            text = "My Bookings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1D52),
            modifier = Modifier.padding(24.dp)
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (approvedBookings.isEmpty()) {
            EmptyState(
                icon = Icons.AutoMirrored.Filled.List,
                title = "No Approved Bookings",
                description = "Your approved bookings will appear here. Check 'All Bookings' for pending requests."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(approvedBookings) { booking ->
                    BookingItem(
                        booking = booking,
                        isApprovedBooking = true,
                        onApprovedBookingClick = {
                            selectedBooking = booking
                            ownerDetails = null
                        }
                    )
                }
            }
        }
    }

    // Owner Details Modal
    if (selectedBooking != null) {
        OwnerDetailsDialog(
            booking = selectedBooking!!,
            ownerDetails = ownerDetails,
            onDismiss = { selectedBooking = null }
        )
    }
}

@Composable
fun OwnerDetailsDialog(booking: BachelorBookingUIModel, ownerDetails: MUser?, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Owner Details",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1D52)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                if (ownerDetails == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                } else {
                    // Owner Profile Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (ownerDetails.avatarUrl.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(ownerDetails.avatarUrl),
                                    contentDescription = "Owner Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp),
                                    tint = BrandGreen
                                )
                            }
                        }
                    }

                    // Owner Details
                    Spacer(modifier = Modifier.height(16.dp))

                    // Name
                    DetailRow(
                        icon = Icons.Default.Person,
                        label = "Name",
                        value = ownerDetails.displayName
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone
                    DetailRow(
                        icon = Icons.Default.Call,
                        label = "Phone",
                        value = ownerDetails.phoneNumber
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    DetailRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = ownerDetails.userId
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Property Details
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = BrandGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Property Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = booking.property?.title ?: "N/A",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2C3E50)
                            )
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = booking.property?.location ?: "N/A", fontSize = 13.sp, color = Color.Gray)
                            }
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rent: ₹${booking.property?.rent ?: 0}/month",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandGreen
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun BookingItem(booking: BachelorBookingUIModel, isApprovedBooking: Boolean = false, onApprovedBookingClick: (() -> Unit)? = null) {
    val status = booking.request.status.lowercase()
    val statusColor = when (status) {
        "approved", "accepted" -> BrandGreen
        "rejected" -> Color.Red
        else -> Color(0xFFF39C12)
    }

    val modifier = if (isApprovedBooking) {
        Modifier
            .fillMaxWidth()
            .clickable { onApprovedBookingClick?.invoke() }
    } else {
        Modifier.fillMaxWidth()
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.property?.title ?: "Loading...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = booking.property?.location ?: "Loading...", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${booking.property?.rent ?: 0}/mo",
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    fontSize = 16.sp
                )

                if (isApprovedBooking) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(BrandGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Info, null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tap for details",
                            fontSize = 12.sp,
                            color = BrandGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(8.dp),
            color = BrandGreen.copy(alpha = 0.1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                tint = BrandGreen
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C3E50)
            )
        }
    }
}

@Composable
fun DashboardScreen(
    paddingValues: PaddingValues,
    viewModel: BachelorBookingsViewModel = hiltViewModel()
) {
    val bookings = viewModel.bookings.value
    val isLoading = viewModel.isLoading.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .padding(paddingValues)
    ) {
        Text(
            text = "Dashboard",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1D52),
            modifier = Modifier.padding(24.dp)
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (bookings.isEmpty()) {
            EmptyState(
                icon = Icons.Default.DateRange,
                title = "No Activity Yet",
                description = "Your booking requests and status updates will appear here."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bookings) { booking ->
                    BookingItem(booking)
                }
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, description: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BachelorProfileScreen(navController: NavHostController, paddingValues: PaddingValues) {
    var userProfile by remember { mutableStateOf<MUser?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    userProfile = doc.toObject(MUser::class.java)
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(paddingValues)
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandGreen, BrandGreen.copy(alpha = 0.8f))
                    )
                )
        ) {
            // Logout Icon at top right
            IconButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = if (!userProfile?.avatarUrl.isNullOrEmpty()) rememberAsyncImagePainter(userProfile?.avatarUrl)
                                 else painterResource(id = R.drawable.houseimage),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            null,
                            modifier = Modifier.padding(6.dp).size(20.dp),
                            tint = BrandGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = userProfile?.displayName ?: "User",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userProfile?.role?.replaceFirstChar { it.uppercase() } ?: "Bachelor",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // Profile Menu
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
        ) {
            ProfileMenuItem(Icons.Default.Person, "Personal Information", userProfile?.displayName ?: "N/A")
            ProfileMenuItem(Icons.Default.Email, "Email Address", currentUser?.email ?: "N/A")
            ProfileMenuItem(Icons.Default.Call, "Phone Number", userProfile?.phoneNumber ?: "N/A")
            ProfileMenuItem(Icons.Default.Notifications, "Notifications", "On")
            
            // Logout option in menu
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true },
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Red.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            null,
                            modifier = Modifier.padding(8.dp),
                            tint = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Logout",
                        color = Color.Red,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(56.dp)
                .clickable {
                    showLogoutDialog = true
                },
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Red)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(UrbanScreens.LoginScreen.name) {
                            popUpTo(0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
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
fun ProfileMenuItem(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF8F9FA)
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.padding(8.dp),
                tint = BrandGreen
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = Color(0xFF2C3E50), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Color.White,
        tonalElevation = 12.dp
    ) {
        val selectedColor = BrandGreen
        val unselectedColor = Color.LightGray

        val items = listOf(
            Triple("Home", Icons.Default.Home, 0),
            Triple("Dashboard", Icons.AutoMirrored.Filled.List, 1),
            Triple("My Bookings", Icons.Default.DateRange, 2),
            Triple("Profile", Icons.Default.Person, 3)
        )

        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = selectedColor.copy(alpha = 0.12f)
                )
            )
        }
    }
}
