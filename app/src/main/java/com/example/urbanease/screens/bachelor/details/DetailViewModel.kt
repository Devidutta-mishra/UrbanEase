package com.example.urbanease.screens.bachelor.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.Property
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(DetailUiState())
        private set

    private var bookingListenerJob: Job? = null

    fun loadPropertyDetails(propertyId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val prop = repository.getProperty(propertyId)
            uiState = uiState.copy(property = prop)
            
            prop?.ownerId?.let { ownerId ->
                uiState = uiState.copy(ownerInfo = repository.getOwner(ownerId))
            }

            val currentUserId = authRepository.currentUserId
            if (currentUserId != null && prop != null) {
                bookingListenerJob?.cancel()
                bookingListenerJob = launch {
                    repository.listenToBookingRequest(currentUserId, propertyId).collect {
                        uiState = uiState.copy(existingRequest = it)
                    }
                }
            }

            uiState = uiState.copy(isLoading = false)
        }
    }

    fun bookProperty(onResult: (Boolean) -> Unit) {
        val currentProp = uiState.property ?: return
        val currentUserId = authRepository.currentUserId ?: return
        if (
            currentProp.approvalStatus != Property.APPROVAL_APPROVED ||
            currentProp.propertyStatus != Property.PROPERTY_AVAILABLE
        ) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isBooking = true)
            val bookingId = UUID.randomUUID().toString()
            val request = BookingRequest(
                requestId = bookingId,
                bachelorId = currentUserId,
                ownerId = currentProp.ownerId,
                propertyId = currentProp.propertyId,
                ownerDetailsVisible = true,
                status = "pending",
                appliedAt = System.currentTimeMillis()
            )
            val success = repository.bookProperty(request)
            uiState = uiState.copy(isBooking = false)
            onResult(success)
        }
    }
}
