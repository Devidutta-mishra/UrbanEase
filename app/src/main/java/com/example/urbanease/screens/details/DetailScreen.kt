package com.example.urbanease.screens.details

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun DetailScreen(navController: NavController, adId: String?) {
    val context = LocalContext.current

    var ad by remember { mutableStateOf<PropertyAd?>(null) }
    var isLoadingAd by remember { mutableStateOf(true) }

    LaunchedEffect(adId) {
        if (adId != null) {
            FirebaseFirestore.getInstance()
                .collection("ads")
                .document(adId)
                .get()
                .addOnSuccessListener { document ->
                    ad = document.toObject(PropertyAd::class.java)
                    isLoadingAd = false
                }
                .addOnFailureListener {
                    isLoadingAd = false
                }
        } else {
            isLoadingAd = false
        }
    }

    if (isLoadingAd) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (ad != null) {
        var isBooking by remember { mutableStateOf(false) }

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
                                text = "₹${ad!!.rent}/month",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF006874)
                            )
                        }
                        Button(
                            onClick = {
                                isBooking = true
                                bookProperty(ad!!) { success ->
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
                    if (ad!!.imageUrls.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(ad!!.imageUrls.first()),
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
                    Text(text = ad!!.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = ad!!.location,
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
                        InfoChip(Icons.Default.Info, "${ad!!.rooms} BHK")
                        InfoChip(Icons.Default.Info, "${ad!!.bathrooms} Bath")
                        InfoChip(Icons.Default.Info, "Floor ${ad!!.floorNo}")
                        InfoChip(Icons.Default.Info, ad!!.furnishing)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = ad!!.description, color = Color.DarkGray, lineHeight = 20.sp)

                    Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
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
    val user = FirebaseAuth.getInstance().currentUser ?: return
    val bookingId = UUID.randomUUID().toString()

    val booking = Booking(
        bookingId = bookingId,
        houseId = ad.houseId,
        ownerId = ad.ownerId,
        userId = user.uid, // Corrected to userId to match Booking data class and Firestore rules
        bachelorEmail = user.email ?: "",
        houseTitle = ad.title,
        rent = ad.rent,
        status = "pending"
    )

    FirebaseFirestore.getInstance().collection("bookings")
        .document(bookingId)
        .set(booking)
        .addOnSuccessListener { onResult(true) }
        .addOnFailureListener { onResult(false) }
}
