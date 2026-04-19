package com.example.urbanease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.urbanease.navigation.UrbanNavigation
import com.example.urbanease.ui.theme.UrbanEaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val navController = rememberNavController()
    Surface(modifier = Modifier.fillMaxSize()) {
        UrbanNavigation(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}
