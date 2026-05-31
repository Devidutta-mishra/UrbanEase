package com.example.urbanease.screens.owner.requests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.screens.owner.components.OwnerBottomNavigation
import com.example.urbanease.screens.owner.components.OwnerHeader
import com.example.urbanease.ui.theme.BrandGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RequestsScreen(
    navController: NavController,
    viewModel: RequestsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val requests = uiState.requests
    val isLoading = uiState.isLoading
    val errorMessage = uiState.errorMessage

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
                        text = "Bookings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Bookings and applications submitted for your properties.",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BrandGreen)
                        }
                    }
                } else if (errorMessage != null) {
                    item {
                        ErrorCard(
                            message = errorMessage,
                            onRetry = viewModel::loadRequests
                        )
                    }
                } else if (requests.isEmpty()) {
                    item {
                        EmptyBookingsState()
                    }
                } else {
                    item {
                        BookingSummaryCard(bookingCount = requests.size)
                    }

                    items(
                        items = requests,
                        key = { item -> item.request.requestId }
                    ) { item ->
                        BookingRequestCard(
                            requestDetail = item,
                            onClick = {
                                navController.navigate(
                                    "${UrbanScreens.RequestDetailScreen.name}/${item.request.requestId}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyBookingsState() {
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
                    Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = BrandGreen
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "No bookings yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Bookings submitted by bachelors will appear here.",
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun BookingSummaryCard(bookingCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF0D4660),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total Bookings",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Submitted applications across your listings",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 13.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.16f)
            ) {
                Text(
                    text = bookingCount.toString(),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFFFD6D6))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Could not load bookings", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
            Spacer(modifier = Modifier.height(6.dp))
            Text(message, color = Color.DarkGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun BookingRequestCard(
    requestDetail: RequestWithDetails,
    onClick: () -> Unit
) {
    val user = requestDetail.user
    val property = requestDetail.property

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE8EEF1))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                BookingAvatar(name = user?.displayName)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.displayName?.ifBlank { "Unknown Bachelor" } ?: "Unknown Bachelor",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = user?.role?.ifBlank { "Bachelor" } ?: "Bachelor",
                        color = Color(0xFF6B7280),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusPill(
                    label = requestDetail.request.status.replaceFirstChar { it.uppercase() },
                    containerColor = Color(0xFFE7F2F5),
                    textColor = Color(0xFF0C7B91)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF5F8FA)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    InfoRow(
                        icon = Icons.Default.Home,
                        label = "Property",
                        value = property?.title?.ifBlank { "Unknown Property" } ?: "Unknown Property"
                    )
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = property?.location?.ifBlank { "Not provided" } ?: "Not provided"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            InfoRow(
                icon = Icons.Default.Phone,
                label = "Phone",
                value = user?.phoneNumber?.ifBlank { "Not provided" } ?: "Not provided"
            )
            InfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = user?.email?.ifBlank { "Not provided" } ?: "Not provided"
            )
            InfoRow(
                icon = Icons.Default.DateRange,
                label = "Booked on",
                value = formatBookingDate(requestDetail.request.appliedAt)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Open booking",
                    color = BrandGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BookingAvatar(name: String?) {
    Box(
        modifier = Modifier
            .size(66.dp)
            .background(Color(0xFFE0F2F1), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val initials = name
            ?.trim()
            ?.split(" ")
            ?.filter { it.isNotBlank() }
            ?.take(2)
            ?.joinToString("") { it.first().uppercase() }
            ?.ifBlank { "B" }
            ?: "B"
        Text(initials, color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
}

@Composable
private fun StatusPill(label: String, containerColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = RoundedCornerShape(11.dp),
            color = Color(0xFFEAF4F6)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0C7B91),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color(0xFF6B7280),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                color = Color(0xFF111827),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatBookingDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
