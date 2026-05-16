package com.example.urbanease.screens.bachelor

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.House
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.HouseRepository
import com.example.urbanease.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class PropertyFilters(
    val bhk: Int? = null,
    val bathrooms: Int? = null,
    val maxRent: Float? = null,
    val furnishing: String? = null
)

@HiltViewModel
class BachelorHomeViewModel @Inject constructor(
    private val repository: HouseRepository,
    private val userRepository: UserRepository
    ) : ViewModel() {
    val ads = mutableStateOf<List<House>>(emptyList())
    val userProfile = mutableStateOf<MUser?>(null)
    val searchQuery = mutableStateOf("")
    val filters = mutableStateOf(PropertyFilters())
    val isLoading = mutableStateOf(false)
    private var adsListener: ListenerRegistration? = null

    val filteredAds: List<House>
        get() {
            var result = ads.value

            // Search filter
            if (searchQuery.value.isNotEmpty()) {
                result = result.filter { 
                    it.location.contains(searchQuery.value, ignoreCase = true) 
                }
            }

            // Industry level filters
            val f = filters.value
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
        loadAllAds()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userRepository.getUser(currentUser.uid) { profile ->
                userProfile.value = profile
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun updateFilters(newFilters: PropertyFilters) {
        filters.value = newFilters
    }

    fun clearFilters() {
        filters.value = PropertyFilters()
    }

    fun loadAllAds() {
        Log.d("BachelorHomeVM", "Loading all ads...")
        isLoading.value = true
        adsListener?.remove()

        adsListener = repository.listenToApprovedAds { fetchedAds ->
            Log.d("BachelorHomeVM", "Displaying ${fetchedAds.size} ads")
            ads.value = fetchedAds
            isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        adsListener?.remove()
    }
}
