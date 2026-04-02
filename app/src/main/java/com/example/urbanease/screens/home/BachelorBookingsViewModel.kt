package com.example.urbanease.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.data.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class BachelorBookingsViewModel @Inject constructor() : ViewModel() {
    val bookings = mutableStateOf<List<Booking>>(emptyList())
    val isLoading = mutableStateOf(false)
    private var bookingsListener: ListenerRegistration? = null

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            fetchBookingsForBachelor(currentUser.uid)
        }
    }

    private fun fetchBookingsForBachelor(bachelorId: String) {
        isLoading.value = true
        bookingsListener?.remove()
        
        bookingsListener = FirebaseFirestore.getInstance().collection("bookings")
            .whereEqualTo("bachelorId", bachelorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BachelorBookingsVM", "Error fetching bookings: ${error.message}")
                    isLoading.value = false
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    bookings.value = snapshot.toObjects(Booking::class.java)
                    Log.d("BachelorBookingsVM", "Fetched ${bookings.value.size} bookings")
                }
                isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        bookingsListener?.remove()
    }
}
