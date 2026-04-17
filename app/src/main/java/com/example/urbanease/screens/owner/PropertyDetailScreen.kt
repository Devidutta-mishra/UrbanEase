package com.example.urbanease.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.urbanease.data.PropertyAd

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
                title = { Text("Property Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (property != null) {
            val isEditable = property.status == "rejected" || !property.isApproved
            
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                PropertyCard(ad = property, onClick = {})
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (isEditable) {
                    Text("Edit Mode", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var title by remember { mutableStateOf(property.title) }
                    var rent by remember { mutableStateOf(property.rent.toString()) }
                    var description by remember { mutableStateOf(property.description) }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rent,
                        onValueChange = { rent = it },
                        label = { Text("Rent") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            viewModel.updateProperty(property.copy(
                                title = title,
                                rent = rent.toLongOrNull() ?: 0L,
                                description = description
                            ))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isUpdating.value
                    ) {
                        if (viewModel.isUpdating.value) CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(24.dp))
                        else Text("Update Property")
                    }
                } else {
                    Text("Details", style = MaterialTheme.typography.titleLarge)
                    Text(property.description)
                    // Add more details as needed
                }
            }
        } else {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Property not found or error loading data.")
            }
        }
    }
}
