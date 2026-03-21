package com.example.urbanease.screens.add

import androidx.hilt.navigation.compose.hiltViewModel
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.urbanease.components.Appbar
import com.example.urbanease.components.BottomButton
import com.example.urbanease.components.InfoForm
import com.example.urbanease.model.PostAdViewModel
import com.example.urbanease.navigation.UrbanScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RentScreen(
    navController: NavController,
    viewModel: PostAdViewModel = hiltViewModel()
) {
    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val rentState = remember { mutableStateOf("") }
    val roomsState = remember { mutableStateOf("") }
    val bathroomsState = remember { mutableStateOf("") }
    val floorNoState = remember { mutableStateOf("") }
    val furnishingState = remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            topBar = {
                Appbar(
                    title = "ADD DETAILS",
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
                        if (
                            titleState.value.isBlank() ||
                            rentState.value.isBlank() ||
                            roomsState.value.isBlank() ||
                            bathroomsState.value.isBlank() ||
                            floorNoState.value.isBlank() ||
                            descriptionState.value.isBlank() ||
                            furnishingState.value.isBlank()
                        ) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateTitle(titleState.value)
                            viewModel.updateDescription(descriptionState.value)
                            viewModel.updateRent(rentState.value.toIntOrNull() ?: 0)
                            viewModel.updateRooms(roomsState.value.toIntOrNull() ?: 0)
                            viewModel.updateBathrooms(bathroomsState.value.toIntOrNull() ?: 0)
                            viewModel.updateFloorNo(floorNoState.value)
                            viewModel.updateFurnishing(furnishingState.value)
                            
                            navController.navigate(UrbanScreens.PhotoScreen.name)
                        }
                    }
                )
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Surface(
                modifier = Modifier.padding(top = 60.dp, start = 5.dp, end = 5.dp),
                color = Color.Transparent
            ) {
                Column {
                    Text(
                        text = "Location: ${viewModel.ad.value.location}",
                        fontSize = 17.sp,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 18.dp, bottom = 8.dp)
                    )
                    InfoForm("Title", titleState)
                    InfoForm("Rent", rentState)
                    InfoForm("BHK", roomsState)
                    InfoForm("Bathrooms", bathroomsState)
                    InfoForm("Floor No", floorNoState)
                    InfoForm("Description", descriptionState)
                    InfoForm("Furnishing", furnishingState)
                }
            }
        }
    }
}
