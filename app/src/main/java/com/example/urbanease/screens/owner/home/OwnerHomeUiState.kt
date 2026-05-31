package com.example.urbanease.screens.owner.home

import com.example.urbanease.model.Property

data class OwnerHomeUiState(
    val properties: List<Property> = emptyList(),
    val isLoading: Boolean = false,
    val totalProperties: Int = 0,
    val error: String? = null
)
