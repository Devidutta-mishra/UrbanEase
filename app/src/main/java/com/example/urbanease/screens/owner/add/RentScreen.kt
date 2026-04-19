package com.example.urbanease.screens.owner.add

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun RentScreen(
    navController: NavController,
    viewModel: PostAdViewModel = hiltViewModel()
) {
    val titleState = remember { mutableStateOf(viewModel.ad.value.title) }
    val descriptionState = remember { mutableStateOf(viewModel.ad.value.description) }
    val rentState =
        remember { mutableStateOf(if (viewModel.ad.value.rent > 0) viewModel.ad.value.rent.toString() else "") }
    val roomsState =
        remember { mutableStateOf(if (viewModel.ad.value.rooms > 0) viewModel.ad.value.rooms.toString() else "") }
    val bathroomsState =
        remember { mutableStateOf(if (viewModel.ad.value.bathrooms > 0) viewModel.ad.value.bathrooms.toString() else "") }
    val floorNoState = remember { mutableStateOf(viewModel.ad.value.floorNo) }
    val furnishingState = remember { mutableStateOf(viewModel.ad.value.furnishing) }

    val context = LocalContext.current

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
                    text = "Tell us more\nabout your place",
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
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                AnimatedButton(
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen, // Industry Standard Green
                        contentColor = Color.White
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
        item {
            Text(
                text = "BASIC INFORMATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            AnimatedListingInputField(
                label = "Property Title",
                state = titleState,
                placeholder = "e.g. Cozy 2BHK in Patia",
                index = 0
            )
        }

        item {
            AnimatedListingInputField(
                label = "Monthly Rent (₹)",
                state = rentState,
                placeholder = "e.g. 15000",
                keyboardType = KeyboardType.Number,
                index = 1
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedListingInputField(
                        label = "BHK",
                        state = roomsState,
                        placeholder = "2",
                        keyboardType = KeyboardType.Number,
                        index = 2
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedListingInputField(
                        label = "Bathrooms",
                        state = bathroomsState,
                        placeholder = "2",
                        keyboardType = KeyboardType.Number,
                        index = 3
                    )
                }
            }
        }

        item {
            AnimatedListingInputField(
                label = "Floor Number",
                state = floorNoState,
                placeholder = "e.g. 3rd Floor",
                index = 4
            )
        }

        item {
            AnimatedListingInputField(
                label = "Furnishing",
                state = furnishingState,
                placeholder = "e.g. Semi-Furnished",
                index = 5
            )
        }

        item {
            AnimatedListingInputField(
                label = "Description",
                state = descriptionState,
                placeholder = "Describe your property...",
                isSingleLine = false,
                index = 6
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingInputField(
    label: String,
    state: MutableState<String>,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isSingleLine: Boolean = true
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = state.value,
            onValueChange = { state.value = it },
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = isSingleLine,
            maxLines = if (isSingleLine) 1 else 5,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F0F0),
                unfocusedContainerColor = Color(0xFFF7F7F7),
                disabledContainerColor = Color(0xFFF7F7F7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            )
        )
    }
}

/**
 * Animated version of ListingInputField with fade + slide entry animation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedListingInputField(
    label: String,
    state: MutableState<String>,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isSingleLine: Boolean = true,
    index: Int = 0
) {
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
        ListingInputField(
            label = label,
            state = state,
            placeholder = placeholder,
            keyboardType = keyboardType,
            isSingleLine = isSingleLine
        )
    }
}

