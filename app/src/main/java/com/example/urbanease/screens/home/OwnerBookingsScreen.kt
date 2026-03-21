package com.example.urbanease.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.urbanease.data.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OwnerBookingsScreen(navController: NavController) {
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val ownerId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(key1 = true) {
        if (ownerId != null) {
            FirebaseFirestore.getInstance().collection("bookings")
                .whereEqualTo("ownerId", ownerId)
                .addSnapshotListener { snapshot, _ ->
                    bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF8F9FA))) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF38B6FF))
            } else if (bookings.isEmpty()) {
                Text(text = "No booking requests yet.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookings) { booking ->
                        BookingItem(booking)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = booking.houseTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "From: ${booking.bachelorEmail}", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Status: ${booking.status.uppercase()}",
                    fontWeight = FontWeight.Bold,
                    color = when(booking.status) {
                        "approved" -> Color(0xFF2E7D32)
                        "rejected" -> Color.Red
                        else -> Color(0xFFF57C00)
                    }
                )
                
                if (booking.status == "pending") {
                    Row {
                        IconButton(onClick = { updateBookingStatus(booking.bookingId, "rejected") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", tint = Color.Red)
                        }
                        IconButton(onClick = { updateBookingStatus(booking.bookingId, "approved") }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Approve", tint = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }
    }
}

fun updateBookingStatus(bookingId: String, status: String) {
    FirebaseFirestore.getInstance().collection("bookings")
        .document(bookingId)
        .update("status", status)
}
