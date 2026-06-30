package com.example.urbanease.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property
import com.example.urbanease.model.ReviewEntry
import com.example.urbanease.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDetailScreen(
    navController: NavController,
    propertyId: String,
    viewModel: AdminHomeViewModel = hiltViewModel()
) {
    val property = viewModel.uiState.properties.find { it.propertyId == propertyId }
    val owner = property?.let { current ->
        viewModel.uiState.owners.find { it.userId == current.ownerId }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                title = {
                    Text(
                        "Property Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (property != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Image Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = if (property.imageUrls.isNotEmpty()) rememberAsyncImagePainter(
                            property.imageUrls[0]
                        )
                        else painterResource(id = R.drawable.houseimage),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = if (property.imageUrls.size > 1) rememberAsyncImagePainter(
                                property.imageUrls[1]
                            )
                            else painterResource(id = R.drawable.houseimage),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Image(
                            painter = if (property.imageUrls.size > 2) rememberAsyncImagePainter(
                                property.imageUrls[2]
                            )
                            else painterResource(id = R.drawable.houseimage),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    property.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(property.location, color = TextGrey, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "MONTHLY RENT",
                            color = TextGrey,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "₹${property.rent}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feature Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeatureChip(icon = Icons.Default.Bed, label = "${property.rooms} Beds")
                    FeatureChip(icon = Icons.Default.Bathtub, label = "${property.bathrooms} Baths")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeatureChip(icon = Icons.Default.Chair, label = property.furnishing)
                    FeatureChip(
                        icon = Icons.Default.SquareFoot,
                        label = property.propertyStatus.toDisplayLabel()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Property Description",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        property.description,
                        modifier = Modifier.padding(20.dp),
                        color = Color.DarkGray,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                PropertySpecificationsCard(property = property)

                Spacer(modifier = Modifier.height(24.dp))

                VerificationStatusCard(property = property)

                Spacer(modifier = Modifier.height(16.dp))

                OwnerInfoCard(owner = owner, ownerId = property.ownerId)

                Spacer(modifier = Modifier.height(16.dp))

                AdminModerationPanel(
                    property = property,
                    onApprove = { viewModel.approveProperty(property.propertyId) },
                    onReject = { reason -> viewModel.rejectProperty(property.propertyId, reason) },
                    onRequestChanges = { comment, fields ->
                        viewModel.requestPropertyChanges(property.propertyId, comment, fields)
                    },
                    onHide = { viewModel.hideProperty(property.propertyId) },
                    onUnhide = { viewModel.unhideProperty(property.propertyId) },
                    onArchive = { viewModel.archiveProperty(property.propertyId) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ReviewHistoryCard(history = property.reviewHistory)

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

private enum class ModerationDialogType { REJECT, REQUEST_CHANGES }

private val REQUESTABLE_FIELDS = listOf(
    "Title", "Rent", "Bedrooms", "Bathrooms", "Furnishing",
    "Floor", "Description", "Photos", "Location"
)

@Composable
private fun VerificationStatusCard(property: Property) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Verification",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status", color = TextGrey, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                ApprovalStatusPill(property.approvalStatus)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Submitted", color = TextGrey, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(formatModerationDate(property.submittedAt), fontSize = 13.sp, color = Color.Black)
            }
            if (property.adminComment.isNotBlank()) {
                Text("Latest note", color = TextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(property.adminComment, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
            }
            if (property.requestedChangeFields.isNotEmpty()) {
                Text(
                    "Requested fields: ${property.requestedChangeFields.joinToString()}",
                    color = StatusPendingText,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ApprovalStatusPill(status: String) {
    val (background, content) = when (status) {
        Property.APPROVAL_APPROVED -> StatusApproved to StatusApprovedText
        Property.APPROVAL_REJECTED -> StatusRejected to StatusRejectedText
        Property.APPROVAL_PENDING,
        Property.APPROVAL_UNDER_REVIEW,
        Property.APPROVAL_CHANGES_REQUESTED -> StatusPending to StatusPendingText
        else -> Color(0xFFF1F5F9) to Color.Black
    }
    Surface(color = background, shape = RoundedCornerShape(999.dp)) {
        Text(
            status.toDisplayLabel(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = content,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun OwnerInfoCard(owner: MUser?, ownerId: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Owner",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            if (owner != null) {
                OwnerInfoLine(Icons.Default.Person, owner.displayName.ifBlank { "Unknown" })
                OwnerInfoLine(Icons.Default.Phone, owner.phoneNumber.ifBlank { "Not provided" })
                OwnerInfoLine(Icons.Default.Email, owner.email.ifBlank { "Not provided" })
            } else {
                Text(
                    "Owner profile unavailable (ID: $ownerId)",
                    color = TextGrey,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun OwnerInfoLine(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, fontSize = 14.sp, color = Color.Black)
    }
}

@Composable
private fun AdminModerationPanel(
    property: Property,
    onApprove: () -> Unit,
    onReject: (String) -> Unit,
    onRequestChanges: (String, List<String>) -> Unit,
    onHide: () -> Unit,
    onUnhide: () -> Unit,
    onArchive: () -> Unit
) {
    var dialog by remember { mutableStateOf<ModerationDialogType?>(null) }
    val isHidden = property.approvalStatus == Property.APPROVAL_HIDDEN
    val isArchived = property.approvalStatus == Property.APPROVAL_ARCHIVED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Moderation",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Approve to publish, request changes with notes, or take the listing off the public feed.",
                color = TextGrey,
                fontSize = 13.sp
            )

            Button(
                onClick = onApprove,
                modifier = Modifier.fillMaxWidth(),
                enabled = property.approvalStatus != Property.APPROVAL_APPROVED,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Approve", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ModerationActionButton(
                    label = "Request Changes",
                    container = StatusPending,
                    content = StatusPendingText,
                    onClick = { dialog = ModerationDialogType.REQUEST_CHANGES }
                )
                ModerationActionButton(
                    label = "Reject",
                    container = StatusRejected,
                    content = StatusRejectedText,
                    onClick = { dialog = ModerationDialogType.REJECT }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ModerationActionButton(
                    label = if (isHidden) "Unhide" else "Hide",
                    container = Color(0xFFF1F5F9),
                    content = Color.Black,
                    onClick = { if (isHidden) onUnhide() else onHide() }
                )
                ModerationActionButton(
                    label = "Archive",
                    container = Color(0xFFF1F5F9),
                    content = Color.Black,
                    enabled = !isArchived,
                    onClick = onArchive
                )
            }
        }
    }

    when (dialog) {
        ModerationDialogType.REJECT -> ModerationCommentDialog(
            title = "Reject listing",
            label = "Reason for rejection",
            showFieldSelection = false,
            onDismiss = { dialog = null },
            onConfirm = { comment, _ ->
                onReject(comment)
                dialog = null
            }
        )
        ModerationDialogType.REQUEST_CHANGES -> ModerationCommentDialog(
            title = "Request changes",
            label = "What should the owner fix?",
            showFieldSelection = true,
            onDismiss = { dialog = null },
            onConfirm = { comment, fields ->
                onRequestChanges(comment, fields)
                dialog = null
            }
        )
        null -> Unit
    }
}

@Composable
private fun RowScope.ModerationActionButton(
    label: String,
    container: Color,
    content: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ModerationCommentDialog(
    title: String,
    label: String,
    showFieldSelection: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>) -> Unit
) {
    var comment by remember { mutableStateOf("") }
    val selectedFields = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(comment.trim(), selectedFields.toList()) },
                enabled = comment.isNotBlank()
            ) {
                Text("Confirm", color = PrimaryTeal, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGrey)
            }
        },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                if (showFieldSelection) {
                    Text(
                        "Fields needing changes (optional)",
                        color = TextGrey,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    REQUESTABLE_FIELDS.forEach { field ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedFields.contains(field),
                                onCheckedChange = { checked ->
                                    if (checked) selectedFields.add(field) else selectedFields.remove(field)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal)
                            )
                            Text(field, fontSize = 14.sp, color = Color.Black)
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun ReviewHistoryCard(history: List<ReviewEntry>) {
    if (history.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Review History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            history.sortedByDescending { it.createdAt }.forEach { entry ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            entry.status.toDisplayLabel(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(formatModerationDate(entry.createdAt), fontSize = 12.sp, color = TextGrey)
                    }
                    if (entry.comment.isNotBlank()) {
                        Text(entry.comment, fontSize = 13.sp, color = Color.DarkGray)
                    }
                    if (entry.requestedFields.isNotEmpty()) {
                        Text(
                            "Fields: ${entry.requestedFields.joinToString()}",
                            fontSize = 12.sp,
                            color = TextGrey
                        )
                    }
                    Text("by ${entry.actorRole.ifBlank { "system" }}", fontSize = 11.sp, color = TextGrey)
                }
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            }
        }
    }
}

private fun formatModerationDate(timestamp: Long): String {
    if (timestamp <= 0L) return "—"
    return SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

@Composable
private fun PropertySpecificationsCard(property: Property) {
    val specs = buildList {
        if (property.propertyType.isNotBlank()) add("Property Type" to property.propertyType)
        if (property.preferredTenant.isNotBlank()) add("Preferred Tenant" to property.preferredTenant)
        if (property.parking.isNotBlank()) add("Parking" to property.parking)
        if (property.balcony.isNotBlank()) add("Balcony" to property.balcony)
        if (property.floorNo.isNotBlank()) add("Floor" to property.floorNo)
        if (property.totalFloors.isNotBlank()) add("Total Floors" to property.totalFloors)
        if (property.securityDeposit > 0) add("Security Deposit" to "₹${property.securityDeposit}")
        if (property.maintenanceCharges > 0) add("Maintenance" to "₹${property.maintenanceCharges}")
        if (property.leaseDuration.isNotBlank()) add("Lease Duration" to property.leaseDuration)
        if (property.availableFrom > 0) add("Available From" to formatListingDate(property.availableFrom))
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Specifications & Amenities",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            if (specs.isEmpty() && amenities.isEmpty()) {
                Text("No additional details provided.", color = TextGrey, fontSize = 13.sp)
            } else {
                specs.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = TextGrey, fontSize = 14.sp)
                        Text(
                            value,
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (amenities.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("INCLUDED", color = TextGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        amenities.joinToString("   •   "),
                        color = Color.Black,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

private fun formatListingDate(millis: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
}

private fun String.toDisplayLabel(): String {
    return split("_").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}

@Composable
fun FeatureChip(icon: ImageVector, label: String) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
