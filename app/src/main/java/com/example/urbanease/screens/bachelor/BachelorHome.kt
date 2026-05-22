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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
import com.example.urbanease.model.Property
import com.example.urbanease.model.MUser
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.BrandGreen
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
            1 -> ApplicationsScreen(navController, paddingValues)
            2 -> BachelorProfileScreen(navController, paddingValues, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BachelorHomeScreen(
    navController: NavHostController,
    viewModel: BachelorHomeViewModel,
    paddingValues: PaddingValues
) {
    val uiState = viewModel.uiState
    val properties = viewModel.filteredProperties
    val isLoading = uiState.isLoading
    val searchQuery = uiState.searchQuery
    val currentFilters = uiState.filters
    
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAllProperties()
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilters = currentFilters,
            onDismiss = { showFilterSheet = false },
            onApply = { 
                viewModel.updateFilters(it)
                showFilterSheet = false
            },
            onClear = {
                viewModel.clearFilters()
                showFilterSheet = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(paddingValues)
    ) {
        // Search Header with Filter Button
        SearchHeader(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            onFilterClick = { showFilterSheet = true },
            hasActiveFilters = currentFilters != PropertyFilters()
        )

        // Main Content
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BrandGreen
                )
            } else if (properties.isEmpty()) {
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
                            if (searchQuery.isNotEmpty()) "Coming soon !" else "No properties found",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (searchQuery.isNotEmpty()) "We are currently not serving in this location." else "Try adjusting your filters or search criteria.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(properties) { ad ->
                        BachelorPropertyCard(ad) {
                            navController.navigate("${UrbanScreens.DetailScreen.name}/${ad.propertyId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHeader(
    query: String, 
    onQueryChange: (String) -> Unit, 
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "UrbanEase",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1D52)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF5F6F7),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 15.sp,
                            color = Color.Black
                        ),
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text(
                                    text = "Search by location...",
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box {
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onFilterClick() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (hasActiveFilters) Color(0xFF1A1D52) else Color(0xFFF5F6F7),
                    border = BorderStroke(1.dp, if (hasActiveFilters) Color(0xFF1A1D52) else Color(0xFFEEEEEE))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = if (hasActiveFilters) Color.White else Color.Black
                        )
                    }
                }
                if (hasActiveFilters) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilters: PropertyFilters,
    onDismiss: () -> Unit,
    onApply: (PropertyFilters) -> Unit,
    onClear: () -> Unit
) {
    var tempBhk by remember { mutableStateOf(currentFilters.bhk) }
    var tempBathrooms by remember { mutableStateOf(currentFilters.bathrooms) }
    val tempMaxRentByState = remember { mutableStateOf(currentFilters.maxRent ?: 50000f) }
    var tempMaxRent by tempMaxRentByState
    var tempFurnishing by remember { mutableStateOf(currentFilters.furnishing) }
    
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filters",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                TextButton(onClick = onClear) {
                    Text("Clear All", color = Color.Gray)
                }
            }

            // Scrollable Content Area
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // BHK Filter
                Text("BHK", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 3, 4).forEach { bhk ->
                        FilterChip(
                            text = "$bhk BHK",
                            isSelected = tempBhk == bhk,
                            onClick = { tempBhk = if (tempBhk == bhk) null else bhk }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bathrooms Filter
                Text("Bathrooms", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 2, 3).forEach { bath ->
                        FilterChip(
                            text = "$bath+",
                            isSelected = tempBathrooms == bath,
                            onClick = { tempBathrooms = if (tempBathrooms == bath) null else bath }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rent Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Max Rent", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("₹${tempMaxRent.toInt()}", color = BrandGreen, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = tempMaxRent,
                    onValueChange = { tempMaxRent = it },
                    valueRange = 5000f..100000f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandGreen,
                        activeTrackColor = BrandGreen,
                        inactiveTrackColor = Color(0xFFEEEEEE)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Furnishing
                Text("Furnishing", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Furnished", "Semi-Furnished", "Unfurnished").forEach { status ->
                        FilterChip(
                            text = status,
                            isSelected = tempFurnishing == status,
                            onClick = { tempFurnishing = if (tempFurnishing == status) null else status }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Fixed Footer with Apply Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = Color.White
            ) {
                Button(
                    onClick = { 
                        onApply(PropertyFilters(tempBhk, tempBathrooms, tempMaxRent, tempFurnishing)) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1D52),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Apply Filters & Search", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 16.sp
                        )
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
fun BachelorPropertyCard(ad: Property, onClick: () -> Unit) {
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
                        text = ad.title.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        },
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
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ad.location,
                        fontSize = 14.sp,
                        color = Color.Black
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
fun ApplicationsScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: BachelorBookingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val allBookings = uiState.bookings
    val isLoading = uiState.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .padding(paddingValues)
    ) {
        Text(
            text = "My Applications",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1D52),
            modifier = Modifier.padding(24.dp)
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (allBookings.isEmpty()) {
            EmptyState(
                icon = Icons.AutoMirrored.Filled.List,
                title = "No Applications Yet",
                description = "Your property applications will appear here. Owner contact details are provided instantly."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allBookings) { booking ->
                    BookingItem(booking) {
                        navController.navigate("${UrbanScreens.DetailScreen.name}/${booking.request.propertyId}")
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: BachelorBookingUIModel, onCardClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
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
                    color = BrandGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "APPLIED",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Owner Details (Shown directly)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Owner Contact Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (booking.owner != null) {
                        ContactInfoRow(Icons.Default.Person, booking.owner.displayName)
                        ContactInfoRow(Icons.Default.Call, booking.owner.phoneNumber)
                        ContactInfoRow(Icons.Default.Email, booking.owner.email.ifBlank { "Not provided" })
                    } else {
                        Text("Loading owner details...", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "₹${booking.property?.rent ?: 0}/mo",
                fontWeight = FontWeight.Bold,
                color = BrandGreen,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ContactInfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = BrandGreen, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 13.sp, color = Color.DarkGray)
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
fun BachelorProfileScreen(navController: NavHostController, paddingValues: PaddingValues, viewModel: BachelorHomeViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState
    val userProfile = uiState.userProfile
    var showLogoutDialog by remember { mutableStateOf(false) }

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
            ProfileMenuItem(Icons.Default.Email, "Email Address", userProfile?.email ?: "N/A")
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
                        viewModel.logout()
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
            Triple("Applications", Icons.AutoMirrored.Filled.List, 1),
            Triple("Profile", Icons.Default.Person, 2)
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
