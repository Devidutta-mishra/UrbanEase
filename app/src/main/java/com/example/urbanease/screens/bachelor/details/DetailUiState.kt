package com.example.urbanease.screens.bachelor.details

import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property
import com.example.urbanease.model.PropertyReview

data class DetailUiState(
    val property: Property? = null,
    val ownerInfo: MUser? = null,
    val existingRequest: BookingRequest? = null,
    val reviews: List<PropertyReview> = emptyList(),
    val isLoading: Boolean = true,
    val isBooking: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val error: String? = null
) {
    val averageRating: Double
        get() = if (reviews.isEmpty()) 0.0 else reviews.sumOf { it.rating } / reviews.size.toDouble()

    val reviewCount: Int get() = reviews.size
}
