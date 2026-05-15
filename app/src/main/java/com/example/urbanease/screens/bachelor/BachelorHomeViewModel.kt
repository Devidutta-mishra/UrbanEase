package com.example.urbanease.screens.bachelor

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.repository.HouseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class BachelorHomeViewModel @Inject constructor(
    private val repository: HouseRepository
) : ViewModel() {
    val ads = mutableStateOf<List<PropertyAd>>(emptyList())
    val isLoading = mutableStateOf(false)
    private var adsListener: ListenerRegistration? = null

    init {
        loadApprovedAds()
    }

    fun loadApprovedAds() {
        Log.d("BachelorHomeVM", "Loading approved ads...")
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
