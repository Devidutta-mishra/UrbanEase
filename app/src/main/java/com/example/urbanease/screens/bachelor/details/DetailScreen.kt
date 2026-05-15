package com.example.urbanease.screens.bachelor.details

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.model.BookingRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.urbanease.ui.theme.BrandGreen
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailScreen(navController: NavController, houseId: String) {
    val context = LocalContext.current
    var ad by remember { mutableStateOf<PropertyAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isBooking by remember { mutableStateOf(false) }
    var existingRequest by remember { mutableStateOf<BookingRequest?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(houseId) {
        // Fetch property details
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

        // Fetch existing booking request for this property by this user
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("propertyId", houseId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        // Take the latest request if multiple exist (though ideally should be handled)
                        existingRequest = snapshot.toObjects(BookingRequest::class.java)
                            .sortedByDescending { it.createdAt }
                            .firstOrNull()
                    } else {
                        existingRequest = null
                    }
                }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandGreen)
        }
    } else if (ad == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Property not found")
        }
    } else {
        val property = ad!!
        val status = existingRequest?.status?.lowercase() ?: ""
        val canBook = status.isEmpty() || status == "rejected"
        val buttonText = when (status) {
            "pending" -> "Applied"
            "approved", "accepted" -> "Booked"
            else -> "Book Now"
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            "Property Details",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1D52)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1A1D52)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Rent", color = Color.Gray, fontSize = 14.sp)
                            Text(
                                text = "₹${property.rent}/mo",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = BrandGreen
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
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to book",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canBook) BrandGreen else Color.Gray,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .width(160.dp),
                            enabled = !isBooking && canBook
                        ) {
                            if (isBooking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    buttonText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Header with Rounded Corners
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (property.imageUrls.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(property.imageUrls.first()),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.houseimage),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        // Verified Tag
                        Surface(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomStart),
                            color = BrandGreen,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "VERIFIED PROPERTY",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text(
                        text = property.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C3E50)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BrandGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = property.location,
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Property Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1D52)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ModernInfoChip(Icons.Default.Info, "${property.rooms} BHK")
                        ModernInfoChip(Icons.Default.Info, "${property.bathrooms} Bath")
                        ModernInfoChip(Icons.Default.Info, "Floor ${property.floorNo}")
                        ModernInfoChip(Icons.Default.Info, property.furnishing)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Description",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1D52)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = property.description,
                        color = Color(0xFF5D6D7E),
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
fun ModernInfoChip(icon: ImageVector, text: String) {
    Surface(
        color = Color(0xFFF8F9FA),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier.width(80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50),
                textAlign = TextAlign.Center
            )
        }
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
        "createdAt" to System.currentTimeMillis(),
        "timestamp" to System.currentTimeMillis()
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
