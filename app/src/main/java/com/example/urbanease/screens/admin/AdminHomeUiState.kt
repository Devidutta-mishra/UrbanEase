package com.example.urbanease.screens.admin

import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property

data class AdminHomeUiState(
    val properties: List<Property> = emptyList(),
    val owners: List<MUser> = emptyList(),
    val bachelors: List<MUser> = emptyList(),
    val isLoading: Boolean = false,
    val isAdmin: Boolean = false,
    val error: String? = null
)
