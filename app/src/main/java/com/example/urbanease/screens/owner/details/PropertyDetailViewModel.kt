package com.example.urbanease.screens.owner.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.Property
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(PropertyDetailUiState())
        private set

    fun loadProperty(propertyId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val property = repository.getProperty(propertyId)
                uiState = uiState.copy(
                    property = property,
                    error = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun updatePropertyStatus(propertyStatus: String) {
        val property = uiState.property ?: return
        val ownerId = authRepository.currentUserId
        if (ownerId == null) {
            uiState = uiState.copy(error = "You are not authorised to update this property")
            return
        }

        uiState = uiState.copy(isUpdatingStatus = true, error = null)
        viewModelScope.launch {
            val success = repository.updatePropertyStatusAsOwner(
                propertyId = property.propertyId,
                ownerId = ownerId,
                propertyStatus = propertyStatus
            )
            uiState = if (success) {
                uiState.copy(
                    property = property.copy(propertyStatus = propertyStatus),
                    isUpdatingStatus = false
                )
            } else {
                uiState.copy(
                    isUpdatingStatus = false,
                    error = "Unable to update property status"
                )
            }
        }
    }

    fun deleteProperty(onDeleted: () -> Unit) {
        val property = uiState.property ?: return
        val ownerId = authRepository.currentUserId
        if (ownerId == null) {
            uiState = uiState.copy(error = "You are not authorised to delete this property")
            return
        }

        uiState = uiState.copy(isDeleting = true, error = null)
        viewModelScope.launch {
            val success = repository.deleteProperty(property.propertyId, ownerId)
            if (success) {
                uiState = uiState.copy(isDeleting = false)
                onDeleted()
            } else {
                uiState = uiState.copy(
                    isDeleting = false,
                    error = "Unable to delete this listing"
                )
            }
        }
    }

    fun submitForReview() {
        val property = uiState.property ?: return
        val ownerId = authRepository.currentUserId
        if (ownerId == null) {
            uiState = uiState.copy(error = "You are not authorised to submit this property")
            return
        }

        uiState = uiState.copy(isUpdatingStatus = true, error = null)
        viewModelScope.launch {
            val success = repository.submitPropertyForReview(property.propertyId, ownerId)
            uiState = if (success) {
                uiState.copy(
                    property = property.copy(
                        approvalStatus = Property.APPROVAL_PENDING,
                        adminComment = "",
                        requestedChangeFields = emptyList(),
                        submittedAt = System.currentTimeMillis()
                    ),
                    isUpdatingStatus = false
                )
            } else {
                uiState.copy(
                    isUpdatingStatus = false,
                    error = "Unable to submit for verification"
                )
            }
        }
    }
}
