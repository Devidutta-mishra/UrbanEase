package com.example.urbanease.screens.owner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.Property
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(OwnerHomeUiState())
        private set

    private var propertiesListener: ListenerRegistration? = null

    init {
        loadData()
    }

    fun loadData() {
        val userId = authRepository.currentUserId ?: return
        uiState = uiState.copy(isLoading = true)
        propertiesListener?.remove()
        propertiesListener = repository.listenToPropertiesForOwner(userId) { result ->
            uiState = uiState.copy(
                properties = result,
                totalProperties = result.size,
                isLoading = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        propertiesListener?.remove()
    }
}
