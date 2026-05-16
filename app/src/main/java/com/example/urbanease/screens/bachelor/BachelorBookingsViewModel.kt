package com.example.urbanease.screens.bachelor

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.House
import com.example.urbanease.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class BachelorBookingUIModel(
    val request: BookingRequest,
    val property: House?,
    val owner: MUser? = null
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
            .whereEqualTo("bachelorId", bachelorId)
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
                        .sortedByDescending { it.appliedAt }
                        .distinctBy { it.propertyId }
                    
                    val uiModels = mutableListOf<BachelorBookingUIModel>()
                    var processedCount = 0
                    
                    if (requestList.isEmpty()) {
                        bookings.value = emptyList()
                        isLoading.value = false
                        return@addSnapshotListener
                    }

                    requestList.forEach { request ->
                        val db = FirebaseFirestore.getInstance()
                        
                        // Fetch property and owner info
                        db.collection("properties").document(request.propertyId).get()
                            .addOnSuccessListener { propertyDoc ->
                                val property = propertyDoc.toObject(House::class.java)
                                
                                db.collection("users").document(request.ownerId).get()
                                    .addOnSuccessListener { ownerDoc ->
                                        val owner = ownerDoc.toObject(MUser::class.java)
                                        uiModels.add(BachelorBookingUIModel(request, property, owner))
                                        processedCount++
                                        checkIfDone(processedCount, requestList.size, uiModels)
                                    }
                                    .addOnFailureListener {
                                        uiModels.add(BachelorBookingUIModel(request, property, null))
                                        processedCount++
                                        checkIfDone(processedCount, requestList.size, uiModels)
                                    }
                            }
                            .addOnFailureListener {
                                uiModels.add(BachelorBookingUIModel(request, null, null))
                                processedCount++
                                checkIfDone(processedCount, requestList.size, uiModels)
                            }
                    }
                } else {
                    isLoading.value = false
                }
            }
    }

    private fun checkIfDone(processedCount: Int, total: Int, uiModels: MutableList<BachelorBookingUIModel>) {
        if (processedCount == total) {
            bookings.value = uiModels.sortedByDescending { it.request.appliedAt }
            isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        bookingsListener?.remove()
    }
}
