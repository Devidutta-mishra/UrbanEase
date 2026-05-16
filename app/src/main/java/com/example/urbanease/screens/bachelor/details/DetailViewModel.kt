package com.example.urbanease.screens.bachelor.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.House
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _property = MutableStateFlow<House?>(null)
    val property: StateFlow<House?> = _property.asStateFlow()

    private val _ownerInfo = MutableStateFlow<MUser?>(null)
    val ownerInfo: StateFlow<MUser?> = _ownerInfo.asStateFlow()

    private val _existingRequest = MutableStateFlow<BookingRequest?>(null)
    val existingRequest: StateFlow<BookingRequest?> = _existingRequest.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isBooking = MutableStateFlow(false)
    val isBooking: StateFlow<Boolean> = _isBooking.asStateFlow()

    private var bookingListenerJob: Job? = null

    fun loadPropertyDetails(houseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val prop = repository.getProperty(houseId)
            _property.value = prop
            
            prop?.ownerId?.let { ownerId ->
                _ownerInfo.value = repository.getOwner(ownerId)
            }
            
            _isLoading.value = false

            // Listen to booking requests for this user and property
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                bookingListenerJob?.cancel()
                bookingListenerJob = launch {
                    repository.listenToBookingRequest(currentUser.uid, houseId).collect {
                        _existingRequest.value = it
                    }
                }
            }
        }
    }

    fun bookProperty(onResult: (Boolean) -> Unit) {
        val currentProp = _property.value ?: return
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        viewModelScope.launch {
            _isBooking.value = true
            val bookingId = UUID.randomUUID().toString()
            val request = BookingRequest(
                requestId = bookingId,
                bachelorId = currentUser.uid,
                ownerId = currentProp.ownerId,
                propertyId = currentProp.houseId,
                ownerDetailsVisible = true,
                status = "pending",
                appliedAt = System.currentTimeMillis()
            )
            val success = repository.bookProperty(request)
            _isBooking.value = false
            onResult(success)
        }
    }
}
