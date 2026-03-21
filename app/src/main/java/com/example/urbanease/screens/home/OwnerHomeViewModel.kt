package com.example.urbanease.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.data.Booking
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.repository.HouseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val repository: HouseRepository
) : ViewModel() {

    val ads = mutableStateOf<List<PropertyAd>>(emptyList())
    val bookings = mutableStateOf<List<Booking>>(emptyList())
    val isLoading = mutableStateOf(false)
    private var adsListener: ListenerRegistration? = null
    private var bookingsListener: ListenerRegistration? = null

    init {
        loadData()
    }

    fun loadData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        isLoading.value = true
        
        // Listen to Ads
        adsListener?.remove()
        adsListener = repository.listenToAdsForOwner(userId) { result ->
            ads.value = result
            isLoading.value = false
        }

        // Listen to Bookings for this owner
        bookingsListener?.remove()
        bookingsListener = FirebaseFirestore.getInstance().collection("bookings")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, _ ->
                bookings.value = snapshot?.toObjects(Booking::class.java) ?: emptyList()
            }
    }

    fun approveBooking(booking: Booking) {
        FirebaseFirestore.getInstance().collection("bookings")
            .document(booking.bookingId)
            .update("status", "approved")
    }

    fun rejectBooking(booking: Booking) {
        FirebaseFirestore.getInstance().collection("bookings")
            .document(booking.bookingId)
            .update("status", "rejected")
    }

    override fun onCleared() {
        super.onCleared()
        adsListener?.remove()
        bookingsListener?.remove()
    }
}
