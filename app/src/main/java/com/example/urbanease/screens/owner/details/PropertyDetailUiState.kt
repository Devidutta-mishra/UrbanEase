package com.example.urbanease.screens.owner.details

import com.example.urbanease.model.Property

data class PropertyDetailUiState(
    val property: Property? = null,
    val isLoading: Boolean = false,
    val isUpdatingStatus: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null
)
