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

        adsListener = FirebaseFirestore.getInstance()
            .collection("properties")
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("BachelorHomeVM", "Firestore Error: ${error.message}", error)
                    isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("BachelorHomeVM", "Snapshot received. Document count: ${snapshot.size()}")
                    val fetchedAds = snapshot.documents.mapNotNull { document ->
                        try {
                            val ad = document.toObject(PropertyAd::class.java)
                            Log.d("BachelorHomeVM", "Parsed ad: ${ad?.title} from doc: ${document.id}")
                            ad?.copy(houseId = document.id)
                        } catch (e: Exception) {
                            Log.e("BachelorHomeVM", "Error parsing document ${document.id}: ${e.message}")
                            null
                        }
                    }
                    ads.value = fetchedAds
                    Log.d("BachelorHomeVM", "Displaying ${fetchedAds.size} ads")
                } else {
                    Log.d("BachelorHomeVM", "Snapshot is null")
                    ads.value = emptyList()
                }

                isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        adsListener?.remove()
    }
}
