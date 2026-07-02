package com.example.urbanease.screens.admin.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.urbanease.model.Property
import com.example.urbanease.ui.theme.BackgroundLight
import com.example.urbanease.ui.theme.PrimaryTeal
import com.example.urbanease.ui.theme.TextGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailScreen(
    navController: NavController,
    userId: String,
    viewModel: AdminUserDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val user = uiState.user

    LaunchedEffect(userId) { viewModel.load(userId) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                title = { Text("User Details", fontWeight = FontWeight.Bold, color = PrimaryTeal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = PrimaryTeal) }

            user == null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("User not found.", color = TextGrey) }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(PrimaryTeal.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        user.displayName.take(1).uppercase().ifBlank { "U" },
                                        color = PrimaryTeal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        user.displayName.ifBlank { "Unknown" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        user.role.replaceFirstChar { it.uppercase() },
                                        color = TextGrey,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoLine(Icons.Default.Phone, user.phoneNumber.ifBlank { "Not provided" })
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoLine(Icons.Default.Email, user.email.ifBlank { "Not provided" })
                        }
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.setSuspended(!user.suspended) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.suspended) PrimaryTeal else Color(0xFFC62828),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            if (user.suspended) "Unsuspend Account" else "Suspend Account",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Text(
                        if (user.role == "owner") "Listings (${uiState.properties.size})"
                        else "Enquiries (${uiState.enquiries.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                if (user.role == "owner") {
                    if (uiState.properties.isEmpty()) {
                        item { Text("No listings.", color = TextGrey, fontSize = 14.sp) }
                    } else {
                        items(uiState.properties) { property -> OwnerListingRow(property) }
                    }
                } else {
                    if (uiState.enquiries.isEmpty()) {
                        item { Text("No enquiries.", color = TextGrey, fontSize = 14.sp) }
                    } else {
                        items(uiState.enquiries) { row ->
                            EnquiryRowItem(row)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLine(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, fontSize = 14.sp, color = Color.Black)
    }
}

@Composable
private fun OwnerListingRow(property: Property) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Home, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(property.title.ifBlank { "Untitled" }, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Text(property.location, color = TextGrey, fontSize = 12.sp)
            }
            Text(
                property.approvalStatus.replace("_", " ").replaceFirstChar { it.uppercase() },
                color = TextGrey,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EnquiryRowItem(row: EnquiryRow) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Home, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(row.propertyTitle, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Text(row.propertyLocation, color = TextGrey, fontSize = 12.sp)
            }
            Text(
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(row.appliedAt)),
                color = TextGrey,
                fontSize = 11.sp
            )
        }
    }
}
