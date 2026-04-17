package com.example.urbanease.screens.bachelor.details

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.data.Booking
import com.example.urbanease.data.PropertyAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailScreen(navController: NavController, houseId: String) {
    val context = LocalContext.current
    var ad by remember { mutableStateOf<PropertyAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isBooking by remember { mutableStateOf(false) }

    LaunchedEffect(houseId) {
        FirebaseFirestore.getInstance().collection("properties")
            .document(houseId)
            .get()
            .addOnSuccessListener { document ->
                ad = document.toObject(PropertyAd::class.java)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to load property details", Toast.LENGTH_SHORT).show()
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF38B6FF))
        }
    } else if (ad == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Property not found")
        }
    } else {
        val property = ad!!
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Property Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Price", color = Color.Gray, fontSize = 14.sp)
                            Text(
                                text = "₹${property.rent}/month",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF006874)
                            )
                        }
                        Button(
                            onClick = {
                                isBooking = true
                                bookProperty(property) { success ->
                                    isBooking = false
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "Booking Request Sent!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to book",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .width(150.dp),
                            enabled = !isBooking
                        ) {
                            if (isBooking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("Book Now", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)) {
                    if (property.imageUrls.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(property.imageUrls.first()),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.istockphoto_856794670_612x612),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = property.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = property.location,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoChip(Icons.Default.Info, "${property.rooms} BHK")
                        InfoChip(Icons.Default.Info, "${property.bathrooms} Bath")
                        InfoChip(Icons.Default.Info, "Floor ${property.floorNo}")
                        InfoChip(Icons.Default.Info, property.furnishing)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = property.description, color = Color.DarkGray, lineHeight = 20.sp)

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF0F4F8),
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = Color(0xFF006874)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, fontSize = 12.sp, color = Color.Gray)
    }
}

fun bookProperty(ad: PropertyAd, onResult: (Boolean) -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser == null) {
        Log.e("REQ_DEBUG", "Auth failed: currentUser is null")
        onResult(false)
        return
    }

    val uid = currentUser.uid
    val bookingId = UUID.randomUUID().toString()

    // Create hashmap to match Firestore Rule: request.resource.data.userId == request.auth.uid
    val requestMap = hashMapOf(
        "requestId" to bookingId,
        "userId" to uid,           // This must match request.auth.uid
        "ownerId" to ad.ownerId,
        "propertyId" to ad.houseId,
        "status" to "pending",
        "createdAt" to System.currentTimeMillis()
    )

    Log.d("REQ_DEBUG", "Auth UID = $uid")
    Log.d("REQ_DEBUG", "userId sent = ${requestMap["userId"]}")
    Log.d("REQ_DEBUG", "Full request = $requestMap")

    FirebaseFirestore.getInstance().collection("requests")
        .document(bookingId)
        .set(requestMap)
        .addOnSuccessListener { 
            Log.d("REQ_DEBUG", "Successfully created booking: $bookingId")
            onResult(true) 
        }
        .addOnFailureListener { e -> 
            Log.e("FIREBASE_ERROR", "Request failed", e)
            onResult(false) 
        }
}
