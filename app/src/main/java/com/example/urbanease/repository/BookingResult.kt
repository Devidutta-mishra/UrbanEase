package com.example.urbanease.repository

sealed interface BookingResult {
    data object Success : BookingResult
    data object AlreadyBooked : BookingResult
    data object PropertyUnavailable : BookingResult
    data object PropertyNotFound : BookingResult
    data object CannotBookOwnProperty : BookingResult
    data object NotAuthenticated : BookingResult
    data class Failure(val message: String?) : BookingResult
}
