package com.example.urbanease.repository

import android.util.Log
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val propertiesRef = firestore.collection("properties")
    private val requestsRef = firestore.collection("requests")
    private val usersRef = firestore.collection("users")

    private fun DocumentSnapshot.toProperty(): Property? {
        return toObject(Property::class.java)?.copy(propertyId = id)?.apply {
            approvalStatus = resolveApprovalStatus(this@toProperty)
            propertyStatus = resolvePropertyStatus(this@toProperty)
        }
    }

    private fun resolveApprovalStatus(document: DocumentSnapshot): String {
        val explicitStatus = document.getString("approvalStatus")
            ?.takeIf { it in Property.APPROVAL_STATUSES }
        if (explicitStatus != null) return explicitStatus

        val legacyIsApproved = document.getBoolean("isApproved")
        if (legacyIsApproved == true) return Property.APPROVAL_APPROVED
        if (legacyIsApproved == false) return Property.APPROVAL_PENDING

        val legacyStatus = document.getString("status")
        if (legacyStatus in Property.APPROVAL_STATUSES) return legacyStatus!!

        // Preserve visibility for old documents that never had moderation fields.
        return Property.APPROVAL_APPROVED
    }

    private fun resolvePropertyStatus(document: DocumentSnapshot): String {
        val explicitStatus = document.getString("propertyStatus")
            ?.takeIf { it in Property.PROPERTY_STATUSES }
        if (explicitStatus != null) return explicitStatus

        val legacyStatus = document.getString("status")
        return when {
            legacyStatus in Property.PROPERTY_STATUSES -> legacyStatus!!
            legacyStatus in Property.APPROVAL_STATUSES -> Property.PROPERTY_AVAILABLE
            else -> Property.PROPERTY_AVAILABLE
        }
    }

    private fun DocumentSnapshot.toBookingRequest(): BookingRequest? {
        return toObject(BookingRequest::class.java)?.apply {
            if (requestId.isBlank()) requestId = id
        }
    }

    fun listenToPropertiesForOwner(
        ownerId: String,
        onResult: (List<Property>) -> Unit
    ): ListenerRegistration {
        return propertiesRef.whereEqualTo("ownerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val properties = snapshot?.documents?.mapNotNull { it.toProperty() } ?: emptyList()
                onResult(properties)
            }
    }

    fun listenToAllProperties(onResult: (List<Property>) -> Unit): ListenerRegistration {
        return propertiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            val properties = snapshot?.documents?.mapNotNull { it.toProperty() } ?: emptyList()
            onResult(properties)
        }
    }

    fun listenToApprovedProperties(onResult: (List<Property>) -> Unit): ListenerRegistration {
        return propertiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            val properties = snapshot?.documents
                ?.mapNotNull { it.toProperty() }
                ?.filter {
                    it.approvalStatus == Property.APPROVAL_APPROVED &&
                        it.propertyStatus == Property.PROPERTY_AVAILABLE
                }
                ?: emptyList()
            onResult(properties)
        }
    }

    fun getPropertiesForOwner(ownerId: String, onResult: (List<Property>) -> Unit) {
        propertiesRef.whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.documents.mapNotNull { it.toProperty() })
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getAllProperties(onResult: (List<Property>) -> Unit) {
        propertiesRef.get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.documents.mapNotNull { it.toProperty() })
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    suspend fun getProperty(propertyId: String): Property? {
        return try {
            propertiesRef.document(propertyId).get().await().toProperty()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPropertiesByIds(propertyIds: Collection<String>): Map<String, Property?> {
        return propertyIds.associateWith { getProperty(it) }
    }

    suspend fun createProperty(property: Property) {
        val newProperty = property.copy(
            approvalStatus = Property.APPROVAL_PENDING,
            propertyStatus = Property.PROPERTY_AVAILABLE
        )
        propertiesRef.document(newProperty.propertyId).set(newProperty, SetOptions.merge()).await()
    }

    suspend fun updateOwnerProperty(property: Property) {
        val updates = mapOf(
            "ownerId" to property.ownerId,
            "location" to property.location,
            "title" to property.title,
            "description" to property.description,
            "rent" to property.rent,
            "rooms" to property.rooms,
            "bathrooms" to property.bathrooms,
            "floorNo" to property.floorNo,
            "furnishing" to property.furnishing,
            "imageUrls" to property.imageUrls,
            "createdAt" to property.createdAt
        )
        propertiesRef.document(property.propertyId).set(updates, SetOptions.merge()).await()
    }

    suspend fun updateApprovalStatus(propertyId: String, approvalStatus: String) {
        require(approvalStatus in Property.APPROVAL_STATUSES) {
            "Invalid approvalStatus: $approvalStatus"
        }
        propertiesRef.document(propertyId)
            .set(mapOf("approvalStatus" to approvalStatus), SetOptions.merge())
            .await()
    }

    suspend fun updatePropertyStatus(propertyId: String, propertyStatus: String) {
        require(propertyStatus in Property.PROPERTY_STATUSES) {
            "Invalid propertyStatus: $propertyStatus"
        }
        propertiesRef.document(propertyId)
            .set(mapOf("propertyStatus" to propertyStatus), SetOptions.merge())
            .await()
    }

    suspend fun getOwner(ownerId: String): MUser? {
        return getUser(ownerId)
    }

    suspend fun getUser(userId: String): MUser? {
        return try {
            usersRef.document(userId).get().await().toObject(MUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersByIds(userIds: Collection<String>): Map<String, MUser?> {
        return userIds.associateWith { getUser(it) }
    }

    fun listenToBookingRequest(bachelorId: String, propertyId: String): Flow<BookingRequest?> = callbackFlow {
        val registration = requestsRef
            .whereEqualTo("bachelorId", bachelorId)
            .whereEqualTo("propertyId", propertyId)
            .addSnapshotListener { snapshot, _ ->
                val request = snapshot?.documents
                    ?.mapNotNull { it.toBookingRequest() }
                    ?.filterNot { it.status == "viewed" }
                    ?.sortedByDescending { it.appliedAt }
                    ?.firstOrNull()
                trySend(request)
            }
        awaitClose { registration.remove() }
    }

    suspend fun bookProperty(request: BookingRequest): Boolean {
        return try {
            if (request.requestId.isNotEmpty()) {
                requestsRef.document(request.requestId).set(request).await()
            } else {
                val requestRef = requestsRef.document()
                requestsRef.document(requestRef.id)
                    .set(request.apply { requestId = requestRef.id })
                    .await()
            }
            if (request.propertyId.isNotBlank()) {
                updatePropertyStatus(request.propertyId, Property.PROPERTY_BOOKED)
            }
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error booking property: ${e.message}", e)
            false
        }
    }

    fun listenToRequestsForOwner(
        ownerId: String,
        onResult: (List<BookingRequest>) -> Unit
    ): ListenerRegistration {
        return requestsRef.whereEqualTo("ownerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                onResult(snapshot?.documents?.mapNotNull { it.toBookingRequest() } ?: emptyList())
            }
    }

    fun listenToRequestsForBachelor(
        bachelorId: String,
        onResult: (List<BookingRequest>) -> Unit
    ): ListenerRegistration {
        return requestsRef.whereEqualTo("bachelorId", bachelorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                onResult(snapshot?.documents?.mapNotNull { it.toBookingRequest() } ?: emptyList())
            }
    }

    suspend fun getRequestsForOwner(ownerId: String): List<BookingRequest> {
        return try {
            requestsRef.whereEqualTo("ownerId", ownerId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toBookingRequest() }
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun getRequest(requestId: String): BookingRequest? {
        return try {
            requestsRef.document(requestId).get().await().toBookingRequest()
        } catch (e: Exception) {
            null
        }
    }
}
