package com.example.urbanease.screens.owner

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.data.PropertyAd
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RequestDetailViewModel @Inject constructor() : ViewModel() {

    val requestDetail = mutableStateOf<RequestWithDetails?>(null)
    val isLoading = mutableStateOf(false)

    fun loadRequestDetail(requestId: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val requestDoc = FirebaseFirestore.getInstance().collection("requests")
                    .document(requestId).get().await()
                val request = requestDoc.toObject(BookingRequest::class.java) ?: return@launch
                
                val userDoc = FirebaseFirestore.getInstance().collection("users")
                    .document(request.userId).get().await()
                val user = userDoc.toObject(MUser::class.java)
                
                val propertyDoc = FirebaseFirestore.getInstance().collection("properties")
                    .document(request.propertyId).get().await()
                val property = propertyDoc.toObject(PropertyAd::class.java)
                
                requestDetail.value = RequestWithDetails(request, user, property)
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
                loadRequestDetail(requestId) // Refresh
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
