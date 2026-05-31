package com.example.urbanease.screens.owner.add

import android.annotation.SuppressLint
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.urbanease.ui.theme.BrandGreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RentScreen(
    navController: NavController,
    viewModel: PostAdViewModel = hiltViewModel()
) {
    val formState = viewModel.formState
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
                        if (viewModel.validateForm()) {
                            viewModel.mapFormToAd()
                            navController.navigate(UrbanScreens.PhotoScreen.name)
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT)
                                .show()
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
        PropertyFormContent(
            formState = formState,
            onFieldChange = viewModel::updateField,
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
        )
    }
}
