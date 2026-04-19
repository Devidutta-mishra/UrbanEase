package com.example.urbanease.screens.splash


import android.R.attr.text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.urbanease.navigation.UrbanScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val scale = remember {
        Animatable(0f)
    }

    var loading by remember { mutableStateOf(true) }
    var showProgressBar by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
        delay(2000L)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser?.email.isNullOrEmpty()) {
            navController.navigate(UrbanScreens.LoginScreen.name)
            loading = false
        } else {
            val uid = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    when (role) {
                        "bachelor" -> navController.navigate(UrbanScreens.BachelorScreen.name)
                        "owner" -> navController.navigate(UrbanScreens.OwnerScreen.name)
                        else -> navController.navigate(UrbanScreens.LoginScreen.name)
                    }
                    loading = false
                }
                .addOnFailureListener {
                    navController.navigate(UrbanScreens.LoginScreen.name)
                    loading = false
                }
        }
    }

    LaunchedEffect(Unit) {
        delay(1000L)
        showProgressBar = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
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
                color = Color.Red.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (loading && showProgressBar) {
                CircularProgressIndicator(color = Color.Red.copy(alpha = 0.5f))
            }
        }
    }
}