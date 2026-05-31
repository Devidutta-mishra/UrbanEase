package com.example.urbanease.screens.owner.settings

import com.example.urbanease.model.MUser

data class SettingsUiState(
    val isLoading: Boolean = false,
    val user: MUser? = null,
    val error: String? = null
)
