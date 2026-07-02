package com.example.urbanease.screens.owner.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                    isUpdatingStatus = uiState.isUpdatingStatus,
                    isDeleting = uiState.isDeleting,
                    onStatusChange = viewModel::updatePropertyStatus,
                    onSubmitForReview = viewModel::submitForReview,
                    onEdit = {
                        navController.navigate("${UrbanScreens.EditPropertyScreen.name}/${uiState.property.propertyId}")
                    },
                    onDelete = { viewModel.deleteProperty { navController.popBackStack() } }
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
    isUpdatingStatus: Boolean,
    isDeleting: Boolean,
    onStatusChange: (String) -> Unit,
    onSubmitForReview: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
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

        VerificationBanner(property = property)

        AdminFeedbackCard(property = property)

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

        if (property.approvalStatus == Property.APPROVAL_APPROVED) {
            AvailabilityManagerCard(
                currentStatus = property.propertyStatus,
                isUpdating = isUpdatingStatus,
                onStatusChange = onStatusChange
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val canSubmit = property.approvalStatus in Property.OWNER_EDITABLE_STATUSES
        val canEdit = canSubmit || property.approvalStatus == Property.APPROVAL_APPROVED

        if (canSubmit) {
            Button(
                onClick = onSubmitForReview,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isUpdatingStatus,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal,
                    contentColor = Color.White
                )
            ) {
                if (isUpdatingStatus) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        if (property.approvalStatus == Property.APPROVAL_DRAFT) {
                            "Submit for Verification"
                        } else {
                            "Resubmit for Verification"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = onEdit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = canEdit,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGreen,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            )
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Edit Ad", fontWeight = FontWeight.Bold)
        }

        if (!canEdit) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Editing is locked while the listing is under review.",
                color = TextGrey,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isDeleting && !isUpdatingStatus,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StatusRejectedText),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejectedText)
        ) {
            if (isDeleting) {
                CircularProgressIndicator(
                    color = StatusRejectedText,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Delete Listing", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete listing?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This permanently removes the listing and its enquiries. This cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Delete", color = StatusRejectedText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextGrey)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun AvailabilityManagerCard(
    currentStatus: String,
    isUpdating: Boolean,
    onStatusChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Availability",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "You control who can book this listing. Mark it Rented or Sold to stop new booking requests.",
                color = TextGrey,
                lineHeight = 20.sp,
                fontSize = 14.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Property.OWNER_SETTABLE_STATUSES.forEach { status ->
                    AvailabilityOption(
                        modifier = Modifier.weight(1f),
                        label = status.toDisplayLabel(),
                        selected = status == currentStatus,
                        enabled = !isUpdating && status != currentStatus,
                        onClick = { onStatusChange(status) }
                    )
                }
            }

            if (isUpdating) {
                CircularProgressIndicator(
                    color = BrandGreen,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AvailabilityOption(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val container = if (selected) BrandGreen else Color(0xFFF1F5F9)
    val content = if (selected) Color.White else Color.Black

    Surface(
        modifier = modifier,
        color = container,
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = content,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
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

@Composable
private fun VerificationBanner(property: Property) {
    val state = when (property.approvalStatus) {
        Property.APPROVAL_DRAFT -> StatusBadgeState(
            "Draft", Icons.Default.Edit, StatusPending, StatusPendingText
        )
        Property.APPROVAL_PENDING, Property.APPROVAL_UNDER_REVIEW -> StatusBadgeState(
            "Submitted for verification", Icons.Default.HourglassTop, StatusPending, StatusPendingText
        )
        Property.APPROVAL_CHANGES_REQUESTED -> StatusBadgeState(
            "Action required", Icons.Default.Info, StatusPending, StatusPendingText
        )
        Property.APPROVAL_APPROVED -> StatusBadgeState(
            "Approved", Icons.Default.CheckCircle, StatusApproved, StatusApprovedText
        )
        Property.APPROVAL_REJECTED -> StatusBadgeState(
            "Rejected", Icons.Default.Warning, StatusRejected, StatusRejectedText
        )
        Property.APPROVAL_HIDDEN -> StatusBadgeState(
            "Hidden", Icons.Default.VisibilityOff, Color(0xFFF1F5F9), Color.Black
        )
        Property.APPROVAL_ARCHIVED -> StatusBadgeState(
            "Archived", Icons.Default.Archive, Color(0xFFF1F5F9), Color.Black
        )
        else -> StatusBadgeState(
            property.approvalStatus, Icons.Default.Info, Color(0xFFF1F5F9), Color.Black
        )
    }
    val message = when (property.approvalStatus) {
        Property.APPROVAL_DRAFT -> "Submit this listing when you're ready for admin review."
        Property.APPROVAL_PENDING, Property.APPROVAL_UNDER_REVIEW ->
            "Waiting for admin review. Editing is locked until the review is complete."
        Property.APPROVAL_CHANGES_REQUESTED ->
            "The admin requested changes. Update the listing and resubmit."
        Property.APPROVAL_APPROVED -> "This listing is verified and visible to tenants."
        Property.APPROVAL_REJECTED -> "This listing was rejected. Review the reason below and resubmit."
        Property.APPROVAL_HIDDEN -> "An admin hid this listing. It is not visible to tenants."
        Property.APPROVAL_ARCHIVED -> "This listing is archived."
        else -> ""
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = state.backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                state.icon,
                contentDescription = null,
                tint = state.contentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(
                    state.label,
                    color = state.contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    message,
                    color = state.contentColor.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun AdminFeedbackCard(property: Property) {
    if (property.adminComment.isBlank() && property.requestedChangeFields.isEmpty()) return
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Admin Feedback",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (property.adminComment.isNotBlank()) {
                Text(property.adminComment, color = Color.DarkGray, lineHeight = 20.sp)
            }
            if (property.requestedChangeFields.isNotEmpty()) {
                Text(
                    "Fields to update",
                    color = TextGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                property.requestedChangeFields.forEach { field ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusRejectedText,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(field, color = Color.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
