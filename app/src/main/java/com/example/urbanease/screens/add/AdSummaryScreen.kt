package com.example.urbanease.screens.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.navigation.UrbanScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdSummaryScreen(navController: NavHostController, viewModel: PostAdViewModel) {
    val ad = viewModel.ad.value
    val isLoading by viewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Review Your Ad") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (viewModel.selectedImages.isNotEmpty()) {
                    Text(text = "Photos", style = MaterialTheme.typography.titleMedium)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.selectedImages) { uri ->
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                DetailItem(label = "Title", value = ad.title)
                DetailItem(label = "Location", value = ad.location)
                DetailItem(label = "Rent", value = "₹${ad.rent}")
                DetailItem(label = "Rooms", value = "${ad.rooms} BHK")
                DetailItem(label = "Bathrooms", value = "${ad.bathrooms}")
                DetailItem(label = "Description", value = ad.description)

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.postAdToFirestore(
                            onSuccess = {
                                navController.navigate(UrbanScreens.OwnerScreen.name) {
                                    popUpTo("post_ad_graph") { inclusive = true }
                                }
                            },
                            onFailure = {
                                // Error handled by viewModel.errorMessage
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Post Ad")
                    }
                }

                viewModel.errorMessage.value?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp)
    }
}
