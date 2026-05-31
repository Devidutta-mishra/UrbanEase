package com.example.urbanease.screens.owner.add

import android.net.Uri
import com.example.urbanease.model.Property

data class PostAdUiState(
    val ad: Property = Property(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedImages: List<Uri> = emptyList()
)
