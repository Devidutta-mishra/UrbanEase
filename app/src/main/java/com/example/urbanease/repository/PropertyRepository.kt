package com.example.urbanease.repository

import com.example.urbanease.model.House
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getProperty(houseId: String): House? {
        return try {
            firestore.collection("properties").document(houseId).get().await().toObject(House::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getOwner(ownerId: String): MUser? {
        return try {
            firestore.collection("users").document(ownerId).get().await().toObject(MUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun listenToBookingRequest(bachelorId: String, propertyId: String): Flow<BookingRequest?> = callbackFlow {
        val registration = firestore.collection("requests")
            .whereEqualTo("bachelorId", bachelorId)
            .whereEqualTo("propertyId", propertyId)
            .addSnapshotListener { snapshot, _ ->
                val request = snapshot?.toObjects(BookingRequest::class.java)
                    ?.sortedByDescending { it.appliedAt }
                    ?.firstOrNull()
                trySend(request)
            }
        awaitClose { registration.remove() }
    }

    suspend fun bookProperty(request: BookingRequest): Boolean {
        return try {
            // If requestId is provided, use it; otherwise let Firestore auto-generate
            if (request.requestId.isNotEmpty()) {
                firestore.collection("requests").document(request.requestId).set(request).await()
            } else {
                // Auto-generate ID if not provided
                firestore.collection("requests").add(request).await()
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("PropertyRepository", "Error booking property: ${e.message}", e)
            false
        }
    }
}
