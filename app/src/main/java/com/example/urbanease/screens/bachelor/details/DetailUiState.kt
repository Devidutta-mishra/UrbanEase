package com.example.urbanease.screens.bachelor.details

import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property

data class DetailUiState(
    val property: Property? = null,
    val ownerInfo: MUser? = null,
    val existingRequest: BookingRequest? = null,
    val isLoading: Boolean = true,
    val isBooking: Boolean = false,
    val error: String? = null
)
