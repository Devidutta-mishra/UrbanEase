package com.example.urbanease.screens.add

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.urbanease.model.PostAdViewModel

@Composable
fun PhotoScreen(navController: NavHostController, viewModel: PostAdViewModel) {
    Surface {
        Text(text = "Photo Screen")
    }
}