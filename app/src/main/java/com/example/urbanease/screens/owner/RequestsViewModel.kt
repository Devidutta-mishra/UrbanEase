package com.example.urbanease.screens.owner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestWithDetails(
    val request: BookingRequest,
    val user: MUser?,
    val property: Property?
)

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RequestsUiState())
        private set

    private var requestsListener: ListenerRegistration? = null

    init {
        loadRequests()
    }

    fun loadRequests() {
        val ownerId = authRepository.currentUserId ?: return
        uiState = uiState.copy(isLoading = true)

        requestsListener?.remove()
        requestsListener = propertyRepository.listenToRequestsForOwner(ownerId) { ownerRequests ->
            viewModelScope.launch {
                try {
                    val bookingRequests = ownerRequests
                        .filterNot { it.status == "viewed" }
                        .sortedByDescending { it.appliedAt }

                    val userIds = bookingRequests.map { it.bachelorId }
                        .filter { it.isNotBlank() }
                        .distinct()
                    val propertyIds = bookingRequests.map { it.propertyId }
                        .filter { it.isNotBlank() }
                        .distinct()

                    val users = propertyRepository.getUsersByIds(userIds)
                    val properties = propertyRepository.getPropertiesByIds(propertyIds)

                    uiState = uiState.copy(
                        requests = bookingRequests.map { request ->
                            RequestWithDetails(
                                request = request,
                                user = users[request.bachelorId],
                                property = properties[request.propertyId]
                            )
                        },
                        errorMessage = null
                    )
                } catch (e: Exception) {
                    uiState = uiState.copy(errorMessage = e.message ?: "Unable to load bookings")
                } finally {
                    uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        requestsListener?.remove()
    }
}
