package com.example.urbanease.screens.bachelor

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.Property
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import com.example.urbanease.repository.UserRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PropertyFilters(
    val bhk: Int? = null,
    val bathrooms: Int? = null,
    val maxRent: Float? = null,
    val furnishing: String? = null
)

@HiltViewModel
class BachelorHomeViewModel @Inject constructor(
    private val repository: PropertyRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(BachelorHomeUiState())
        private set

    private var propertiesListener: ListenerRegistration? = null
    private var favoritesListener: ListenerRegistration? = null

    val favoriteProperties: List<Property>
        get() = uiState.properties.filter { it.propertyId in uiState.favoriteIds }

    val filteredProperties: List<Property>
        get() {
            var result = uiState.properties

            // Search filter
            if (uiState.searchQuery.isNotEmpty()) {
                result = result.filter { 
                    it.location.contains(uiState.searchQuery, ignoreCase = true) 
                }
            }

            val f = uiState.filters
            if (f.bhk != null) {
                result = result.filter { it.rooms == f.bhk }
            }
            if (f.bathrooms != null) {
                result = result.filter { it.bathrooms == f.bathrooms }
            }
            if (f.maxRent != null) {
                result = result.filter { it.rent <= f.maxRent }
            }
            if (f.furnishing != null) {
                result = result.filter { it.furnishing == f.furnishing }
            }

            return result
        }

    init {
        loadAllProperties()
        loadUserProfile()
        loadFavorites()
    }

    private fun loadUserProfile() {
        val currentUserId = authRepository.currentUserId
        if (currentUserId != null) {
            userRepository.getUser(currentUserId) { profile ->
                uiState = uiState.copy(userProfile = profile)
            }
        }
    }

    private fun loadFavorites() {
        val currentUserId = authRepository.currentUserId ?: return
        favoritesListener?.remove()
        favoritesListener = repository.listenToFavoriteIds(currentUserId) { ids ->
            uiState = uiState.copy(favoriteIds = ids)
        }
    }

    fun toggleFavorite(propertyId: String) {
        val currentUserId = authRepository.currentUserId ?: return
        val shouldFavorite = propertyId !in uiState.favoriteIds
        // Optimistic update; the listener will reconcile with Firestore.
        uiState = uiState.copy(
            favoriteIds = if (shouldFavorite) uiState.favoriteIds + propertyId
            else uiState.favoriteIds - propertyId
        )
        viewModelScope.launch {
            try {
                repository.setFavorite(currentUserId, propertyId, shouldFavorite)
            } catch (e: Exception) {
                // Revert on failure
                uiState = uiState.copy(
                    favoriteIds = if (shouldFavorite) uiState.favoriteIds - propertyId
                    else uiState.favoriteIds + propertyId
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun updateFilters(newFilters: PropertyFilters) {
        uiState = uiState.copy(filters = newFilters)
    }

    fun clearFilters() {
        uiState = uiState.copy(filters = PropertyFilters())
    }

    fun loadAllProperties() {
        Log.d("BachelorHomeVM", "Loading all properties...")
        uiState = uiState.copy(isLoading = true)
        propertiesListener?.remove()

        propertiesListener = repository.listenToApprovedProperties { fetchedProperties ->
            Log.d("BachelorHomeVM", "Displaying ${fetchedProperties.size} properties")
            uiState = uiState.copy(
                properties = fetchedProperties,
                isLoading = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        propertiesListener?.remove()
        favoritesListener?.remove()
    }
}
