package com.example.urbanease.screens.owner.add

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.urbanease.components.AnimatedButton
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.animations.AnimationDurations
import com.example.urbanease.ui.animations.AnimationEasings
import com.example.urbanease.ui.theme.BrandGreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LocationScreen(
    navController: NavController,
    viewModel: PostAdViewModel = hiltViewModel()
) {
    val selectedLocation = rememberSaveable { mutableStateOf<String?>(null) }

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
                    text = "Where is your\nproperty located?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                    color = Color.Black
                )
                Text(
                    text = "Select the neighborhood to help tenants find you.",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                AnimatedButton(
                    onClick = {
                        selectedLocation.value?.let { location ->
                            viewModel.updateLocation(location)
                            navController.navigate(UrbanScreens.RentScreen.name)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedLocation.value != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen, // Industry Standard Green
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            val locations = listOf(
                "Patia",
                "Khandagiri",
                "Ganga Nagar",
                "Siripur",
                "Jaydev Vihar",
                "DLF Cyber City",
                "Chandrasekharpur"
            )

            items(locations.size) { index ->
                val location = locations[index]
                val isSelected = selectedLocation.value == location

                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight / 4 },
                        animationSpec = tween(
                            durationMillis = AnimationDurations.NORMAL,
                            delayMillis = index * 50,
                            easing = AnimationEasings.DEFAULT
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = AnimationDurations.NORMAL,
                            delayMillis = index * 50,
                            easing = AnimationEasings.DEFAULT
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color.Black else Color(0xFFF7F7F7))
                            .clickable { selectedLocation.value = location }
                            .padding(20.dp)
                    ) {
                        Text(
                            text = location,
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}