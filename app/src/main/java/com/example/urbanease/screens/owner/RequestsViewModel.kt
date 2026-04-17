package com.example.urbanease.screens.owner

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.data.PropertyAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class RequestWithDetails(
    val request: BookingRequest,
    val user: MUser?,
    val property: PropertyAd?
)

@HiltViewModel
class RequestsViewModel @Inject constructor() : ViewModel() {

    val requests = mutableStateOf<List<RequestWithDetails>>(emptyList())
    val isLoading = mutableStateOf(false)

    init {
        loadRequests()
    }

    fun loadRequests() {
        val ownerId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        isLoading.value = true
        
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance().collection("requests")
                    .whereEqualTo("ownerId", ownerId)
                    .get().await()
                
                val requestList = snapshot.toObjects(BookingRequest::class.java)
                
                val detailedRequests = requestList.map { request ->
                    val userDoc = FirebaseFirestore.getInstance().collection("users")
                        .document(request.userId).get().await()
                    val user = userDoc.toObject(MUser::class.java)
                    
                    val propertyDoc = FirebaseFirestore.getInstance().collection("properties")
                        .document(request.propertyId).get().await()
                    val property = propertyDoc.toObject(PropertyAd::class.java)
                    
                    RequestWithDetails(request, user, property)
                }
                
                requests.value = detailedRequests
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("requests")
                    .document(requestId)
                    .update("status", status).await()
                loadRequests() // Refresh
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
