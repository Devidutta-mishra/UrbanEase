package com.example.urbanease.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.data.PropertyAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class PostAdViewModel @Inject constructor() : ViewModel() {
    var ad = mutableStateOf(PropertyAd())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun updateLocation(location: String) {
        Log.d("PostAdViewModel", "updateLocation called: $location")
        ad.value = ad.value.copy(location = location)
        Log.d("PostAdViewModel", "ad updated: ${ad.value}")
    }

    fun updateRent(rent: Int) {
        ad.value = ad.value.copy(rent = rent)
        Log.d("PostAdViewModel", "ad updated: ${ad.value}")
    }

    fun updateTitle(title: String) {
        ad.value = ad.value.copy(title = title)
        Log.d("PostAdViewModel", "ad updated: ${ad.value}")
    }

    fun updateDescription(description: String) {
        ad.value = ad.value.copy(description = description)
        Log.d("PostAdViewModel", "ad updated: ${ad.value}")
    }

    fun updateRooms(rooms: Int) {
        ad.value = ad.value.copy(rooms = rooms)
        Log.d("PostAdViewModel", "ad updated: ${ad.value}")
    }

    fun updateBathrooms(bathrooms: Int) {
        ad.value = ad.value.copy(bathrooms = bathrooms)
        Log.d("PostAdViewModel", "ad updated: ${ad.value}")
    }

    fun postAdToFirestore(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val houseId = java.util.UUID.randomUUID().toString()
        val adWithId = ad.value.copy(ownerId = userId, houseId = houseId)

        Log.d("PostAdViewModel", "Posting Ad: $adWithId")


        isLoading.value = true
        FirebaseFirestore.getInstance().collection("ads")
            .document(houseId)
            .set(adWithId)
            .addOnSuccessListener {
                isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                errorMessage.value = e.message
                onFailure(e)
            }
    }

}