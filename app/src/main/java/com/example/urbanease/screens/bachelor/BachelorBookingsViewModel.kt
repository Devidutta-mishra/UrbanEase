package com.example.urbanease.screens.bachelor

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.data.PropertyAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class BachelorBookingUIModel(
    val request: BookingRequest,
    val property: PropertyAd?
)

@HiltViewModel
class BachelorBookingsViewModel @Inject constructor() : ViewModel() {
    val bookings = mutableStateOf<List<BachelorBookingUIModel>>(emptyList())
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
        
        bookingsListener = FirebaseFirestore.getInstance().collection("requests")
            .whereEqualTo("userId", bachelorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BachelorBookingsVM", "Error fetching bookings: ${error.message}")
                    isLoading.value = false
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val rawRequestList = snapshot.toObjects(BookingRequest::class.java)
                    
                    // Filter to show each house once - take the latest request for each propertyId
                    val requestList = rawRequestList
                        .sortedByDescending { it.createdAt }
                        .distinctBy { it.propertyId }
                    
                    // Since we need property details, and addSnapshotListener doesn't easily support joins,
                    // we'll fetch property details for each request. 
                    // For better performance in a real app, you might denormalize data.
                    
                    val uiModels = mutableListOf<BachelorBookingUIModel>()
                    var processedCount = 0
                    
                    if (requestList.isEmpty()) {
                        bookings.value = emptyList()
                        isLoading.value = false
                        return@addSnapshotListener
                    }

                    requestList.forEach { request ->
                        FirebaseFirestore.getInstance().collection("properties")
                            .document(request.propertyId)
                            .get()
                            .addOnSuccessListener { propertyDoc ->
                                val property = propertyDoc.toObject(PropertyAd::class.java)
                                uiModels.add(BachelorBookingUIModel(request, property))
                                processedCount++
                                if (processedCount == requestList.size) {
                                    bookings.value = uiModels.sortedByDescending { it.request.createdAt }
                                    isLoading.value = false
                                }
                            }
                            .addOnFailureListener {
                                uiModels.add(BachelorBookingUIModel(request, null))
                                processedCount++
                                if (processedCount == requestList.size) {
                                    bookings.value = uiModels.sortedByDescending { it.request.createdAt }
                                    isLoading.value = false
                                }
                            }
                    }
                } else {
                    isLoading.value = false
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        bookingsListener?.remove()
    }
}
