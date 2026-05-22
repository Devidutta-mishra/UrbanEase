package com.example.urbanease.screens.bachelor

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.Property
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BachelorBookingUIModel(
    val request: BookingRequest,
    val property: Property?,
    val owner: MUser? = null
)

@HiltViewModel
class BachelorBookingsViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    var uiState by mutableStateOf(BachelorBookingsUiState())
        private set

    private var bookingsListener: ListenerRegistration? = null

    init {
        val currentUserId = authRepository.currentUserId
        if (currentUserId != null) {
            fetchBookingsForBachelor(currentUserId)
        }
    }

    private fun fetchBookingsForBachelor(bachelorId: String) {
        uiState = uiState.copy(isLoading = true)
        bookingsListener?.remove()
        
        bookingsListener = propertyRepository.listenToRequestsForBachelor(bachelorId) { rawRequestList ->
            viewModelScope.launch {
                try {
                    val requestList = rawRequestList
                        .filterNot { it.status == "viewed" }
                        .sortedByDescending { it.appliedAt }
                        .distinctBy { it.propertyId }

                    uiState = uiState.copy(
                        bookings = requestList.map { request ->
                            BachelorBookingUIModel(
                                request = request,
                                property = propertyRepository.getProperty(request.propertyId),
                                owner = propertyRepository.getOwner(request.ownerId)
                            )
                        }.sortedByDescending { it.request.appliedAt }
                    )
                } catch (e: Exception) {
                    Log.e("BachelorBookingsVM", "Error building bookings: ${e.message}", e)
                    uiState = uiState.copy(bookings = emptyList(), error = e.message)
                } finally {
                    uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bookingsListener?.remove()
    }
}
