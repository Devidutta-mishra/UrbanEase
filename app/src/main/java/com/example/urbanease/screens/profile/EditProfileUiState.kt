package com.example.urbanease.screens.profile

import android.net.Uri

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val displayName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val role: String = "",
    val avatarUrl: String = "",
    val selectedImageUri: Uri? = null,
    val error: String? = null,
    val saved: Boolean = false
)
