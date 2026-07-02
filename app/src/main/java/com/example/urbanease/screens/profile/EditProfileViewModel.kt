package com.example.urbanease.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.UserRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(EditProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        val userId = authRepository.currentUserId
        if (userId == null) {
            uiState = uiState.copy(error = "User not authenticated")
            return
        }
        uiState = uiState.copy(isLoading = true)
        userRepository.getUser(userId) { user ->
            uiState = if (user != null) {
                uiState.copy(
                    isLoading = false,
                    displayName = user.displayName,
                    phoneNumber = user.phoneNumber,
                    email = user.email,
                    role = user.role,
                    avatarUrl = user.avatarUrl,
                    error = null
                )
            } else {
                uiState.copy(isLoading = false, error = "Failed to load profile")
            }
        }
    }

    fun onDisplayNameChange(value: String) {
        uiState = uiState.copy(displayName = value, saved = false, error = null)
    }

    fun onPhoneChange(value: String) {
        uiState = uiState.copy(phoneNumber = value, saved = false, error = null)
    }

    fun onImageSelected(uri: Uri) {
        uiState = uiState.copy(selectedImageUri = uri, saved = false, error = null)
    }

    fun save(onSaved: () -> Unit) {
        val userId = authRepository.currentUserId ?: return
        if (uiState.displayName.isBlank()) {
            uiState = uiState.copy(error = "Name cannot be empty")
            return
        }
        if (uiState.isSaving) return
        uiState = uiState.copy(isSaving = true, error = null)

        val imageUri = uiState.selectedImageUri
        if (imageUri != null) {
            val fileName = "avatars/${userId}_${UUID.randomUUID()}.jpg"
            val ref = FirebaseStorage.getInstance().reference.child(fileName)
            ref.putFile(imageUri)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener { url -> persist(userId, url.toString(), onSaved) }
                        .addOnFailureListener { e -> onUploadFailed(e) }
                }
                .addOnFailureListener { e -> onUploadFailed(e) }
        } else {
            persist(userId, null, onSaved)
        }
    }

    private fun persist(userId: String, avatarUrl: String?, onSaved: () -> Unit) {
        userRepository.updateUserProfile(
            userId = userId,
            displayName = uiState.displayName.trim(),
            phoneNumber = uiState.phoneNumber.trim(),
            avatarUrl = avatarUrl
        ) { success ->
            uiState = uiState.copy(
                isSaving = false,
                saved = success,
                avatarUrl = avatarUrl ?: uiState.avatarUrl,
                selectedImageUri = if (success) null else uiState.selectedImageUri,
                error = if (success) null else "Failed to save changes"
            )
            if (success) onSaved()
        }
    }

    private fun onUploadFailed(e: Exception) {
        Log.e("EditProfileVM", "Avatar upload failed", e)
        uiState = uiState.copy(isSaving = false, error = "Failed to upload photo")
    }
}
