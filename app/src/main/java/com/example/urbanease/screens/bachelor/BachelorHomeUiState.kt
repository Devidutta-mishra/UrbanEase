package com.example.urbanease.screens.bachelor

import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property

data class BachelorHomeUiState(
    val properties: List<Property> = emptyList(),
    val userProfile: MUser? = null,
    val searchQuery: String = "",
    val filters: PropertyFilters = PropertyFilters(),
    val isLoading: Boolean = false,
    val error: String? = null
)
