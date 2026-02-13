package com.example.urbanease.screens.add

import android.R
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.urbanease.components.Appbar
import com.example.urbanease.components.BottomButton
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.navigation.UrbanScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LocationScreen(
    navController: NavController,
    viewModel: PostAdViewModel = hiltViewModel()
) {
    val selectedLocation = rememberSaveable { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            topBar = {
                Appbar(
                    title = "Location",
                    navController = navController,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    showProfile = false
                ) {
                    navController.popBackStack()
                }
            },
            bottomBar = {
                BottomButton(
                    onClick = {
                        selectedLocation.value?.let { location ->
                            Log.d("LocationScreen", "Selected location: $location")
                            viewModel.updateLocation(location)
                            Log.d("LocationScreen", "updateLocation called with: $location")
                            navController.navigate(UrbanScreens.RentScreen.name) // Change to your actual next screen
                        }
                    }
                )
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
//            HorizontalDivider(modifier = Modifier.padding(top = 50.dp))
            Surface(
                modifier = Modifier.padding(top = 60.dp, start = 5.dp, end = 5.dp),
                color = Color.Transparent
            ) {
                LocationBox(
                    selectedLocation = selectedLocation,
                    onLocationSelected = { location ->
                        selectedLocation.value = location
                    }
                )
            }
        }
    }
}

@Composable
fun LocationBox(
    selectedLocation: MutableState<String?>,
    onLocationSelected: (String) -> Unit
) {
    val locations = listOf("Patia", "Khandagiri", "Ganga Nagar", "Nayapalli", "Jaydev Vihar")

    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
    ) {
        locations.forEach { location ->
            val isSelected = selectedLocation.value == location
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isSelected) 3.dp else 12.dp)
                    .padding(vertical = 8.dp)
                    .shadow(
                        elevation = if (isSelected) 7.dp else 6.dp,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .clickable {
                        selectedLocation.value = location
                        onLocationSelected(location)
                    }
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Color.Black.copy(alpha = 0.27f) else Color.Transparent,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .background(Color.White)
            ) {
                Text(
                    text = location,
                    modifier = Modifier.padding(16.dp),
                    color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.7f),
                    fontSize = 20.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}