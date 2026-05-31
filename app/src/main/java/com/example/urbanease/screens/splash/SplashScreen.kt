package com.example.urbanease.screens.splash

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.urbanease.navigation.UrbanScreens
import com.example.urbanease.ui.theme.BrandGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    Log.d("SplashScreen", "Rendering SplashScreen")

    val scale = remember {
        Animatable(0.5f)
    }

    var loading by remember { mutableStateOf(true) }
    var showProgressBar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("UrbanEase_Debug", "SplashScreen: LaunchedEffect started")
        
        // Start animation in a separate coroutine to not block the role fetching
        launch {
            Log.d("UrbanEase_Debug", "SplashScreen: Animation starting")
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            Log.d("UrbanEase_Debug", "SplashScreen: Animation completed")
        }

        delay(1000L) // Give a little time for splash feel

        Log.d("UrbanEase_Debug", "SplashScreen: Calling viewModel.getCurrentUserRole")
        viewModel.getCurrentUserRole { role ->
            Log.d("UrbanEase_Debug", "SplashScreen: Role callback received with role: $role")
            
            val destination = when (role) {
                "bachelor" -> UrbanScreens.BachelorScreen.name
                "owner" -> UrbanScreens.OwnerScreen.name
                "admin" -> UrbanScreens.AdminScreen.name
                else -> UrbanScreens.LoginScreen.name
            }

            Log.d("UrbanEase_Debug", "SplashScreen: Navigating to $destination")
            navController.navigate(destination) {
                popUpTo(UrbanScreens.SplashScreen.name) { inclusive = true }
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        delay(2000L) // Show progress bar if it takes too long
        showProgressBar = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .scale(scale.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "UrbanEase",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = BrandGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (loading && showProgressBar) {
                CircularProgressIndicator(color = BrandGreen)
            }
        }
    }
}
