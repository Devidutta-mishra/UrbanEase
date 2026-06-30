package com.example.urbanease.screens.bachelor.details

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.model.Property
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.BookingResult
import com.example.urbanease.ui.theme.BrandGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailScreen(
    navController: NavController,
    propertyId: String,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val ad = uiState.property
    val isLoading = uiState.isLoading
    val isBooking = uiState.isBooking
    val existingRequest = uiState.existingRequest
    val ownerInfo = uiState.ownerInfo

    LaunchedEffect(propertyId) {
        viewModel.loadPropertyDetails(propertyId)
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
        val property = ad
        val isApplied = existingRequest != null
        val canBook = (property.approvalStatus == Property.APPROVAL_APPROVED) &&
            (property.propertyStatus == Property.PROPERTY_AVAILABLE)

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            "UrbanEase",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0D4660)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF0D4660)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F8FB))
                )
            },
            bottomBar = {
                if (!isApplied) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 16.dp,
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                .navigationBarsPadding(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL / MONTHLY",
                                    color = Color(0xFF6B7280),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "₹${formatRent(property.rent)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    color = Color(0xFF111827)
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.bookProperty { result ->
                                        Toast.makeText(
                                            context,
                                            bookingMessage(result),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0C7B91),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .height(54.dp)
                                    .width(172.dp),
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
                                        if (canBook) "Book Now" else "Unavailable",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F8FB))
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                PropertyImageGallery(property = property)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ListingBadge(
                            text = "NEW LISTING",
                            containerColor = Color(0xFFFFF2E5),
                            textColor = Color(0xFF8A4B08)
                        )
                        ListingBadge(
                            text = "VERIFIED",
                            containerColor = Color(0xFFE7F2F5),
                            textColor = Color(0xFF0D6780)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = property.title,
                        fontSize = 31.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827),
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF4B5563),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = property.location,
                            color = Color(0xFF4B5563),
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))
                    RentOverviewCard(property = property)

                    if (isApplied) {
                        Spacer(modifier = Modifier.height(24.dp))
                        OwnerDetailsCard(ownerInfo, property, existingRequest?.status)
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    SectionTitle("Property Details")
                    Spacer(modifier = Modifier.height(14.dp))
                    PropertyInfoGrid(property = property)

                    if (property.hasListingExtras()) {
                        Spacer(modifier = Modifier.height(28.dp))
                        SectionTitle("Amenities & Features")
                        Spacer(modifier = Modifier.height(14.dp))
                        AmenitiesSection(property = property)
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    SectionTitle("The Experience")
                    Spacer(modifier = Modifier.height(12.dp))
                    SectionCard {
                        Text(
                            text = property.description.ifBlank {
                                "A comfortable rental with clear pricing, essential features, and direct access to the owner after you apply."
                            },
                            color = Color(0xFF374151),
                            fontSize = 16.sp,
                            lineHeight = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    SectionTitle("Location")
                    Spacer(modifier = Modifier.height(12.dp))
                    SectionCard {
                        Text(
                            text = property.location,
                            color = Color(0xFF111827),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Apply to unlock the owner contact details and continue the conversation directly for this property.",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFE7F2F5)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                PropertyImage(
                                    imageUrl = property.imageUrls.firstOrNull(),
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF0C7B91),
                                    shadowElevation = 6.dp
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .border(4.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isApplied) 36.dp else 120.dp))
                }
            }
        }
    }
}

@Composable
fun PropertyImageGallery(property: Property) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PropertyImage(
            imageUrl = property.imageUrls.firstOrNull(),
            modifier = Modifier
                .weight(1.5f)
                .aspectRatio(0.92f),
            shape = RoundedCornerShape(28.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PropertyImage(
                imageUrl = property.imageUrls.getOrNull(1) ?: property.imageUrls.firstOrNull(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(15.dp)
            )
            Box {
                PropertyImage(
                    imageUrl = property.imageUrls.getOrNull(2) ?: property.imageUrls.firstOrNull(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(15.dp)
                )
                if (property.imageUrls.size > 3) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.42f)
                    ) {
                        Text(
                            text = "+${property.imageUrls.size - 2}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyImage(
    imageUrl: String?,
    modifier: Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp)
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color(0xFFEDEFF2)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
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
    }
}

@Composable
fun ListingBadge(text: String, containerColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun RentOverviewCard(property: Property) {
    SectionCard {
        Text(
            text = "MONTHLY RENT",
            color = Color(0xFF1F2937),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "₹${formatRent(property.rent)}",
                color = Color(0xFF0D6780),
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "/mo",
                color = Color(0xFF111827),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OverviewStat(value = property.rooms.toString(), label = "BEDS")
            OverviewStat(value = property.bathrooms.toString(), label = "BATHS")
            OverviewStat(value = property.floorNo.ifBlank { "-" }, label = "FLOOR")
        }
    }
}

@Composable
fun OverviewStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color(0xFF111827),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFF6B7280),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111827)
    )
}

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, Color(0xFFEEF1F4))
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun PropertyInfoGrid(property: Property) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PropertyInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Home,
                label = "Bedrooms",
                value = "${property.rooms} BHK"
            )
            PropertyInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Info,
                label = "Bathrooms",
                value = property.bathrooms.toString()
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PropertyInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocationOn,
                label = "Floor",
                value = property.floorNo.ifBlank { "Not specified" }
            )
            PropertyInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Person,
                label = "Furnishing",
                value = property.furnishing.ifBlank { "Not specified" }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PropertyInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Info,
                label = "Availability",
                value = property.propertyStatus.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            )
            PropertyInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocationOn,
                label = "Area",
                value = property.location
            )
        }
    }
}

@Composable
fun PropertyInfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier.wrapContentHeight(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFEEF1F4))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEAF4F6)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF0C7B91),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = label,
                color = Color(0xFF6B7280),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = Color(0xFF111827),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun OwnerDetailsCard(owner: MUser?, property: Property, bookingStatus: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD9E9ED))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Owner Contact Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.weight(1f)
                )
                if (!bookingStatus.isNullOrBlank()) {
                    ListingBadge(
                        text = bookingStatus.uppercase(),
                        containerColor = Color(0xFFE7F2F5),
                        textColor = Color(0xFF0C7B91)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "You have already requested this property. You can now contact the owner directly.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 21.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (owner != null) {
                ContactRow(Icons.Default.Person, owner.displayName)
                ContactRow(Icons.Default.Call, owner.phoneNumber)
                ContactRow(Icons.Default.Email, owner.email.ifBlank { "Not provided" })
                ContactRow(Icons.Default.LocationOn, property.location)
            } else {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrandGreen)
            }
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEAF4F6)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0C7B91),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color(0xFF111827),
            fontSize = 15.sp,
            modifier = Modifier.widthIn(min = 0.dp)
        )
    }
}

private fun formatRent(rent: Long): String {
    return NumberFormat.getNumberInstance(Locale("en", "IN")).format(rent)
}

private fun Property.hasListingExtras(): Boolean {
    return propertyType.isNotBlank() || preferredTenant.isNotBlank() || parking.isNotBlank() ||
        balcony.isNotBlank() || totalFloors.isNotBlank() || leaseDuration.isNotBlank() ||
        securityDeposit > 0 || maintenanceCharges > 0 || availableFrom > 0 ||
        electricityIncluded || waterIncluded || kitchenAvailable || wifiAvailable ||
        liftAvailable || powerBackup || gatedSociety || petFriendly || smokingAllowed
}

@Composable
fun AmenitiesSection(property: Property) {
    val specs = buildList {
        if (property.propertyType.isNotBlank()) add("Type" to property.propertyType)
        if (property.preferredTenant.isNotBlank()) add("Preferred Tenant" to property.preferredTenant)
        if (property.parking.isNotBlank()) add("Parking" to property.parking)
        if (property.balcony.isNotBlank()) add("Balcony" to property.balcony)
        if (property.totalFloors.isNotBlank()) add("Total Floors" to property.totalFloors)
        if (property.securityDeposit > 0) add("Security Deposit" to "₹${formatRent(property.securityDeposit)}")
        if (property.maintenanceCharges > 0) add("Maintenance" to "₹${formatRent(property.maintenanceCharges)}")
        if (property.leaseDuration.isNotBlank()) add("Lease Duration" to property.leaseDuration)
        if (property.availableFrom > 0) add("Available From" to formatAvailableFrom(property.availableFrom))
    }
    val amenities = buildList {
        if (property.electricityIncluded) add("Electricity")
        if (property.waterIncluded) add("Water")
        if (property.kitchenAvailable) add("Kitchen")
        if (property.wifiAvailable) add("WiFi")
        if (property.liftAvailable) add("Lift")
        if (property.powerBackup) add("Power Backup")
        if (property.gatedSociety) add("Gated Society")
        if (property.petFriendly) add("Pet Friendly")
        if (property.smokingAllowed) add("Smoking Allowed")
    }

    SectionCard {
        specs.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, color = Color(0xFF6B7280), fontSize = 14.sp)
                Text(
                    value,
                    color = Color(0xFF111827),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (amenities.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "INCLUDED",
                color = Color(0xFF6B7280),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = amenities.joinToString("   •   "),
                color = Color(0xFF111827),
                fontSize = 14.sp,
                lineHeight = 24.sp
            )
        }
    }
}

private fun formatAvailableFrom(millis: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
}

private fun bookingMessage(result: BookingResult): String = when (result) {
    BookingResult.Success -> "Applied Successfully!"
    BookingResult.AlreadyBooked -> "You have already booked this property"
    BookingResult.CannotBookOwnProperty -> "You cannot book your own property"
    BookingResult.PropertyUnavailable -> "This property is no longer available"
    BookingResult.PropertyNotFound -> "This property no longer exists"
    BookingResult.NotAuthenticated -> "Please sign in to book this property"
    is BookingResult.Failure -> "Failed to apply. Please try again"
}
