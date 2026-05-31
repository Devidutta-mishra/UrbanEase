package com.example.urbanease.screens.owner.edit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.urbanease.components.AnimatedButton
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.screens.owner.add.PostAdViewModel
import com.example.urbanease.screens.owner.add.PropertyFormContent
import com.example.urbanease.ui.theme.BrandGreen

@Composable
fun EditPropertyScreen(
    navController: NavController,
    propertyId: String,
    viewModel: PostAdViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val originalProperty = viewModel.editingProperty

    LaunchedEffect(propertyId) {
        viewModel.loadPropertyForEdit(propertyId)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

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
                        tint = BrandGreen
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Edit your\nproperty details",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                    color = Color.Black
                )
                Text(
                    text = "Approved ads will be sent back to admin for verification after changes.",
                    fontSize = 14.sp,
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
                        viewModel.updateEditedProperty(
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Property updated and submitted for verification",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("${UrbanScreens.PropertyDetailScreen.name}/${it.propertyId}") {
                                    popUpTo("${UrbanScreens.PropertyDetailScreen.name}/${propertyId}") {
                                        inclusive = true
                                    }
                                }
                            },
                            onFailure = {
                                Toast.makeText(
                                    context,
                                    "Unable to update property",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading && originalProperty != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Update Property", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        if (uiState.isLoading && originalProperty == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            PropertyFormContent(
                formState = viewModel.formState,
                onFieldChange = viewModel::updateField,
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                isEditMode = true
            )
        }
    }
}
