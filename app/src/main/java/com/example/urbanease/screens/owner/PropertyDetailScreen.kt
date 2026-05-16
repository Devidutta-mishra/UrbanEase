package com.example.urbanease.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.urbanease.ui.theme.BrandGreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    navController: NavController,
    propertyId: String,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val property = viewModel.property.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Property") },
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
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (property != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                PropertyCard(ad = property, onClick = {})
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Update Listing", style = MaterialTheme.typography.titleLarge, color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                var title by remember { mutableStateOf(property.title) }
                var rent by remember { mutableStateOf(property.rent.toString()) }
                var description by remember { mutableStateOf(property.description) }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = BrandGreen,
                        unfocusedLabelColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = rent,
                    onValueChange = { rent = it },
                    label = { Text("Rent") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = BrandGreen,
                        unfocusedLabelColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = BrandGreen,
                        unfocusedLabelColor = Color.Gray
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        viewModel.updateProperty(property.copy(
                            title = title,
                            rent = rent.toLongOrNull() ?: 0L,
                            description = description
                        ))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isUpdating.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    if (viewModel.isUpdating.value) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Property not found.")
            }
        }
    }
}
