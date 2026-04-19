package com.example.urbanease.screens.owner.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.components.AnimatedButton
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.animations.AnimationDurations
import com.example.urbanease.ui.animations.AnimationEasings
import com.example.urbanease.ui.theme.BrandGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdSummaryScreen(navController: NavHostController, viewModel: PostAdViewModel) {
    val ad = viewModel.ad.value
    val isLoading by viewModel.isLoading

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF7F7F7), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp),
                        tint = BrandGreen // Industry Standard Green
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Review your\nlisting details",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                    color = Color.Black
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                AnimatedButton(
                    onClick = {
                        viewModel.postAdToFirestore(
                            onSuccess = {
                                navController.navigate(UrbanScreens.OwnerScreen.name) {
                                    popUpTo(UrbanScreens.OwnerScreen.name) { inclusive = true }
                                }
                            },
                            onFailure = {
                                // Error handled by viewModel.errorMessage
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen, // Industry Standard Green
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Confirm and Post", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDurations.NORMAL,
                        easing = AnimationEasings.DEFAULT
                    )
                )
            ) {
                Text(
                    text = "PROPERTY PREVIEW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Photos Section
            if (viewModel.selectedImages.isNotEmpty()) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight / 4 },
                        animationSpec = tween(
                            durationMillis = AnimationDurations.NORMAL,
                            easing = AnimationEasings.DEFAULT
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = AnimationDurations.NORMAL,
                            easing = AnimationEasings.DEFAULT
                        )
                    )
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.selectedImages) { uri ->
                            androidx.compose.foundation.Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(width = 240.dp, height = 160.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            } else {
                // Default image if no photos provided
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = AnimationDurations.NORMAL,
                            easing = AnimationEasings.DEFAULT
                        )
                    )
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.houseimage),
                        contentDescription = "Default Property Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 4 },
                    animationSpec = tween(
                        durationMillis = AnimationDurations.NORMAL,
                        delayMillis = 100,
                        easing = AnimationEasings.DEFAULT
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDurations.NORMAL,
                        delayMillis = 100,
                        easing = AnimationEasings.DEFAULT
                    )
                )
            ) {
                SummaryDetailSection(
                    title = "Overview",
                    items = listOf(
                        "Title" to ad.title,
                        "Location" to ad.location,
                        "Rent" to "₹${ad.rent} / month",
                        "Configuration" to "${ad.rooms} BHK",
                        "Bathrooms" to "${ad.bathrooms}",
                        "Floor" to ad.floorNo,
                        "Furnishing" to ad.furnishing
                    )
                )
            }

            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 4 },
                    animationSpec = tween(
                        durationMillis = AnimationDurations.NORMAL,
                        delayMillis = 150,
                        easing = AnimationEasings.DEFAULT
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDurations.NORMAL,
                        delayMillis = 150,
                        easing = AnimationEasings.DEFAULT
                    )
                )
            ) {
                SummaryDetailSection(
                    title = "Description",
                    items = listOf("" to ad.description)
                )
            }

            viewModel.errorMessage.value?.let { error ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = AnimationDurations.FAST,
                            easing = AnimationEasings.DEFAULT
                        )
                    )
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SummaryDetailSection(title: String, items: List<Pair<String, String>>) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (label.isNotEmpty()) {
                    Text(text = label, color = Color.DarkGray, fontSize = 14.sp)
                    Text(
                        text = value,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                } else {
                    Text(text = value, color = Color.DarkGray, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            thickness = 0.5.dp,
            color = Color(0xFFEEEEEE)
        )
    }
}
