package com.example.urbanease.screens.owner

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.House
import com.example.urbanease.repository.HouseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val repository: HouseRepository
) : ViewModel() {

    val ads = mutableStateOf<List<House>>(emptyList())
    val isLoading = mutableStateOf(false)
    
    val totalProperties = mutableStateOf(0)

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
            totalProperties.value = result.size
            isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        adsListener?.remove()
    }
}
