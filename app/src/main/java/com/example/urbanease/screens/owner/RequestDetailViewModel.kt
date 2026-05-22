package com.example.urbanease.screens.owner

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestDetailViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    val requestDetail = mutableStateOf<RequestWithDetails?>(null)
    val isLoading = mutableStateOf(false)

    fun loadRequestDetail(requestId: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val request = propertyRepository.getRequest(requestId) ?: return@launch
                val user = propertyRepository.getUser(request.bachelorId)
                val property = propertyRepository.getProperty(request.propertyId)

                requestDetail.value = RequestWithDetails(request, user, property)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading.value = false
            }
        }
    }
}
