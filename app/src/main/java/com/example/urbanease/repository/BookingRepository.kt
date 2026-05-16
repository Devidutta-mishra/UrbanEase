package com.example.urbanease.repository

import com.example.urbanease.model.BookingRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val requestsRef = firestore.collection("requests")

    fun listenToRequestsForOwner(ownerId: String, onResult: (List<BookingRequest>) -> Unit): ListenerRegistration {
        return requestsRef.whereEqualTo("ownerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                onResult(snapshot?.toObjects(BookingRequest::class.java) ?: emptyList())
            }
    }

    fun listenToRequestsForBachelor(bachelorId: String, onResult: (List<BookingRequest>) -> Unit): ListenerRegistration {
        return requestsRef.whereEqualTo("bachelorId", bachelorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                onResult(snapshot?.toObjects(BookingRequest::class.java) ?: emptyList())
            }
    }

    fun getRequest(requestId: String, onResult: (BookingRequest?) -> Unit) {
        requestsRef.document(requestId).get()
            .addOnSuccessListener { document ->
                onResult(document.toObject(BookingRequest::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }
}
