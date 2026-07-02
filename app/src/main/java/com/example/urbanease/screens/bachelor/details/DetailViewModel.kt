package com.example.urbanease.screens.bachelor.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.PropertyReview
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.BookingResult
import com.example.urbanease.repository.PropertyRepository
import com.google.firebase.firestore.ListenerRegistration
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
    private var reviewsListener: ListenerRegistration? = null

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

            reviewsListener?.remove()
            reviewsListener = repository.listenToReviewsForProperty(propertyId) { reviews ->
                uiState = uiState.copy(reviews = reviews)
            }

            uiState = uiState.copy(isLoading = false)
        }
    }

    val myReview: PropertyReview?
        get() {
            val uid = authRepository.currentUserId ?: return null
            return uiState.reviews.firstOrNull { it.reviewerId == uid }
        }

    fun submitReview(rating: Int, comment: String, onResult: (Boolean) -> Unit) {
        val property = uiState.property ?: return
        val currentUserId = authRepository.currentUserId ?: return
        if (rating < 1) return

        uiState = uiState.copy(isSubmittingReview = true)
        viewModelScope.launch {
            val success = try {
                val name = repository.getUser(currentUserId)?.displayName?.ifBlank { "Tenant" } ?: "Tenant"
                repository.submitReview(
                    PropertyReview(
                        propertyId = property.propertyId,
                        ownerId = property.ownerId,
                        reviewerId = currentUserId,
                        reviewerName = name,
                        rating = rating,
                        comment = comment.trim()
                    )
                )
                true
            } catch (e: Exception) {
                android.util.Log.e("DetailViewModel", "submitReview failed: ${e.message}", e)
                false
            }
            uiState = uiState.copy(isSubmittingReview = false)
            onResult(success)
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

    override fun onCleared() {
        super.onCleared()
        reviewsListener?.remove()
    }
}
