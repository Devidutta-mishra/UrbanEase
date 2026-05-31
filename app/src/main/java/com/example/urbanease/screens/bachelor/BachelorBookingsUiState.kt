package com.example.urbanease.screens.bachelor

data class BachelorBookingsUiState(
    val bookings: List<BachelorBookingUIModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
