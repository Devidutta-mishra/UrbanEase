package com.example.urbanease.screens.owner

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.repository.HouseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val repository: HouseRepository
) : ViewModel() {

    val ads = mutableStateOf<List<PropertyAd>>(emptyList())
    val isLoading = mutableStateOf(false)
    
    val totalProperties = mutableStateOf(0)
    val approvedProperties = mutableStateOf(0)
    val pendingProperties = mutableStateOf(0)
    val rejectedProperties = mutableStateOf(0)

    private var adsListener: ListenerRegistration? = null

    init {
        loadData()
    }

    fun loadData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        isLoading.value = true
        
        adsListener?.remove()
        adsListener = repository.listenToAdsForOwner(userId) { result ->
            ads.value = result
            calculateStats(result)
            isLoading.value = false
        }
    }

    private fun calculateStats(properties: List<PropertyAd>) {
        totalProperties.value = properties.size
        approvedProperties.value = properties.count { it.isApproved }
        pendingProperties.value = properties.count { !it.isApproved && it.status != "rejected" }
        rejectedProperties.value = properties.count { it.status == "rejected" }
    }

    override fun onCleared() {
        super.onCleared()
        adsListener?.remove()
    }
}
