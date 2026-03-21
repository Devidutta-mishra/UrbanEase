package com.example.urbanease.screens.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.navigation.UrbanScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoScreen(navController: NavHostController, viewModel: PostAdViewModel) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { viewModel.addImage(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Photos") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Upload photos of your property to attract more tenants.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Photos",
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Tap to add photos")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.selectedImages.isNotEmpty()) {
                Text(
                    text = "Selected Photos (${viewModel.selectedImages.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.selectedImages) { uri ->
                        Box(modifier = Modifier.size(120.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removeImage(uri) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        navController.navigate(UrbanScreens.AdSummaryScreen.name)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.selectedImages.isNotEmpty()
                ) {
                    Text("Next: Review Details")
                }
                
                TextButton(
                    onClick = {
                        navController.navigate(UrbanScreens.AdSummaryScreen.name)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip for now")
                }
            }
        }
    }
}
