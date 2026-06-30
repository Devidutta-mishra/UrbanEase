package com.example.urbanease.screens.bachelor.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.BookingResult
import com.example.urbanease.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val authRepository: AuthRepository,
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
            if ((currentUserId != null) && (prop != null)) {
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

    fun bookProperty(onResult: (BookingResult) -> Unit) {
        val currentProp = uiState.property
        if (currentProp == null) {
            onResult(BookingResult.PropertyNotFound)
            return
        }
        val currentUserId = authRepository.currentUserId
        if (currentUserId == null) {
            onResult(BookingResult.NotAuthenticated)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isBooking = true)
            val result = repository.bookProperty(currentProp.propertyId, currentUserId)
            uiState = uiState.copy(isBooking = false)
            onResult(result)
        }
    }
}
