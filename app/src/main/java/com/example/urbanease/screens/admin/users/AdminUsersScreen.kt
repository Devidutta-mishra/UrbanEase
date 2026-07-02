package com.example.urbanease.screens.admin.users

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.urbanease.model.MUser
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.BackgroundLight
import com.example.urbanease.ui.theme.PrimaryTeal
import com.example.urbanease.ui.theme.TextGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    navController: NavController,
    viewModel: AdminUsersViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val users = viewModel.filteredUsers
    val roles = listOf("All", "Owners", "Bachelors")

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                title = { Text("Manage Users", fontWeight = FontWeight.Bold, color = PrimaryTeal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 15.sp, color = Color.Black),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (uiState.searchQuery.isEmpty()) {
                                Text("Search by name or email", color = Color.Gray, fontSize = 15.sp)
                            }
                            inner()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(roles) { role ->
                    val selected = role == uiState.roleFilter
                    Surface(
                        modifier = Modifier.clickable { viewModel.onRoleFilterChanged(role) },
                        color = if (selected) PrimaryTeal else Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            role,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            color = if (selected) Color.White else Color.Black,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else if (users.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found.", color = TextGrey)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(users) { user ->
                        UserRow(
                            user = user,
                            onToggleSuspend = { viewModel.setSuspended(user.userId, !user.suspended) },
                            onClick = {
                                navController.navigate("${UrbanScreens.AdminUserDetailScreen.name}/${user.userId}")
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun UserRow(
    user: MUser,
    onToggleSuspend: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(PrimaryTeal.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.displayName.take(1).uppercase().ifBlank { "U" },
                    color = PrimaryTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        user.displayName.ifBlank { "Unknown" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    if (user.suspended) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                "SUSPENDED",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = Color(0xFFC62828),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    "${user.role.replaceFirstChar { it.uppercase() }} • ${user.email}",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }
            TextButton(onClick = onToggleSuspend) {
                Text(
                    if (user.suspended) "Unsuspend" else "Suspend",
                    color = if (user.suspended) PrimaryTeal else Color(0xFFC62828),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
