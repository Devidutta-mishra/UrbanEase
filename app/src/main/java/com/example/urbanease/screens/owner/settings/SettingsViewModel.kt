package com.example.urbanease.screens.owner.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(SettingsUiState())
        private set

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val userId = authRepository.currentUserId
        if (userId == null) {
            uiState = uiState.copy(error = "User not authenticated")
            return
        }

        uiState = uiState.copy(isLoading = true)
        userRepository.getUser(userId) { user ->
            if (user != null) {
                uiState = uiState.copy(
                    user = user,
                    isLoading = false,
                    error = null
                )
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Failed to load user profile"
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
