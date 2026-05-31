package com.example.urbanease

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.urbanease.navigation.UrbanNavigation
import com.example.urbanease.ui.theme.UrbanEaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("UrbanEase_Debug", "MainActivity: onCreate")
        enableEdgeToEdge()
        setContent {
            UrbanEaseTheme {
                UrbanEase()
            }
        }
    }
}

@Composable
fun UrbanEase() {
    SideEffect {
        Log.d("UrbanEase_Debug", "UrbanEase: SideEffect (Recomposing?)")
    }
    val navController = rememberNavController()
    UrbanNavigation(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}
