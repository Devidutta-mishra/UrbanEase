package com.example.urbanease.screens.owner.requests

data class RequestsUiState(
    val requests: List<RequestWithDetails> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
