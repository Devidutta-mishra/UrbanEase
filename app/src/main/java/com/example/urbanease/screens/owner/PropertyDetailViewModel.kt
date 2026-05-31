package com.example.urbanease.screens.owner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: PropertyRepository
) : ViewModel() {

    var uiState by mutableStateOf(PropertyDetailUiState())
        private set

    fun loadProperty(propertyId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val property = repository.getProperty(propertyId)
                uiState = uiState.copy(
                    property = property,
                    error = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}
