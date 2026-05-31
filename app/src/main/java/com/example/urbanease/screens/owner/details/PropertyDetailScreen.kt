package com.example.urbanease.screens.owner.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.model.Property
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.BackgroundLight
import com.example.urbanease.ui.theme.BrandGreen
import com.example.urbanease.ui.theme.PrimaryTeal
import com.example.urbanease.ui.theme.StatusApproved
import com.example.urbanease.ui.theme.StatusApprovedText
import com.example.urbanease.ui.theme.StatusPending
import com.example.urbanease.ui.theme.StatusPendingText
import com.example.urbanease.ui.theme.StatusRejected
import com.example.urbanease.ui.theme.StatusRejectedText
import com.example.urbanease.ui.theme.TextGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    navController: NavController,
    propertyId: String,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Property Details")
                },
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BackgroundLight
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            uiState.property != null -> {
                PropertyDetailsContent(
                    padding = padding,
                    property = uiState.property,
                    onEdit = {
                        navController.navigate("${UrbanScreens.EditPropertyScreen.name}/${uiState.property.propertyId}")
                    }
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Property not found.")
                }
            }
        }
    }
}

@Composable
private fun PropertyDetailsContent(
    padding: PaddingValues,
    property: Property,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PropertyHero(property = property)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = property.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = property.location,
                    color = TextGrey,
                    fontSize = 14.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ApprovalStatusBadge(property.approvalStatus)
                PropertyStatusBadge(property.propertyStatus)
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Monthly Rent",
                    color = TextGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${property.rent}",
                    color = PrimaryTeal,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailChip(icon = Icons.Default.Home, label = "${property.rooms} BHK")
                DetailChip(icon = Icons.Default.Bathtub, label = "${property.bathrooms} Baths")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailChip(icon = Icons.Default.SquareFoot, label = "Floor ${property.floorNo}")
                DetailChip(icon = Icons.Default.Bed, label = property.furnishing)
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = property.description,
                    color = Color.DarkGray,
                    lineHeight = 22.sp
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Approval",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = when (property.approvalStatus) {
                        Property.APPROVAL_APPROVED ->
                            "This listing is live. Any change to the ad will send it back to admin for verification."
                        Property.APPROVAL_REJECTED ->
                            "This listing is not verified right now. Update it and submit again for admin review."
                        Property.APPROVAL_UNDER_REVIEW ->
                            "This listing is currently being reviewed by the admin."
                        else ->
                            "This listing is waiting for admin verification."
                    },
                    color = TextGrey,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGreen,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Edit Ad", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PropertyHero(property: Property) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        androidx.compose.foundation.Image(
            painter = if (property.imageUrls.isNotEmpty()) {
                rememberAsyncImagePainter(property.imageUrls.first())
            } else {
                painterResource(id = R.drawable.houseimage)
            },
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.18f))
        )

        ApprovalStatusBadge(
            approvalStatus = property.approvalStatus,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
        )
    }
}

@Composable
private fun ApprovalStatusBadge(
    approvalStatus: String,
    modifier: Modifier = Modifier
) {
    val badge = when (approvalStatus) {
        Property.APPROVAL_APPROVED -> {
            StatusBadgeState("Verified", Icons.Default.CheckCircle, StatusApproved, StatusApprovedText)
        }
        Property.APPROVAL_REJECTED -> {
            StatusBadgeState("Rejected", Icons.Default.HourglassTop, StatusRejected, StatusRejectedText)
        }
        Property.APPROVAL_UNDER_REVIEW -> {
            StatusBadgeState("Under Review", Icons.Default.HourglassTop, StatusPending, StatusPendingText)
        }
        else -> {
            StatusBadgeState("Not Verified", Icons.Default.HourglassTop, StatusPending, StatusPendingText)
        }
    }

    StatusBadge(
        label = badge.label,
        icon = badge.icon,
        backgroundColor = badge.backgroundColor,
        contentColor = badge.contentColor,
        modifier = modifier
    )
}

@Composable
private fun PropertyStatusBadge(status: String) {
    StatusBadge(
        label = status.toDisplayLabel(),
        icon = Icons.Default.Home,
        backgroundColor = Color(0xFFF1F5F9),
        contentColor = Color.Black
    )
}

@Composable
private fun StatusBadge(
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DetailChip(icon: ImageVector, label: String) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
            Text(text = label, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        }
    }
}

private fun String.toDisplayLabel(): String {
    return split("_").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}

private data class StatusBadgeState(
    val label: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val contentColor: Color
)
