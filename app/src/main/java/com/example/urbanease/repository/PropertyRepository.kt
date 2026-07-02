package com.example.urbanease.repository

import android.util.Log
import com.example.urbanease.model.BookingRequest
import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property
import com.example.urbanease.model.PropertyReview
import com.example.urbanease.model.ReviewEntry
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
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
    private val favoritesRef = firestore.collection("favorites")
    private val reviewsRef = firestore.collection("propertyReviews")

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
        val now = System.currentTimeMillis()
        val newProperty = property.copy(
            approvalStatus = Property.APPROVAL_PENDING,
            propertyStatus = Property.PROPERTY_AVAILABLE,
            submittedAt = now,
            adminComment = "",
            requestedChangeFields = emptyList(),
            previousApprovalStatus = "",
            reviewHistory = listOf(
                ReviewEntry(
                    status = Property.APPROVAL_PENDING,
                    comment = "Submitted for verification",
                    actorRole = "owner",
                    createdAt = now
                )
            )
        )
        propertiesRef.document(newProperty.propertyId).set(newProperty, SetOptions.merge()).await()
    }

    suspend fun updateOwnerProperty(property: Property): Property {
        val existingSnapshot = propertiesRef.document(property.propertyId).get().await()
        val existingProperty = existingSnapshot.toProperty()
            ?: throw IllegalArgumentException("Property not found")

        val hasListingChanges = property.listingFingerprint() != existingProperty.listingFingerprint()

        val approvalStatus = when {
            existingProperty.approvalStatus == Property.APPROVAL_APPROVED && hasListingChanges ->
                Property.APPROVAL_PENDING
            existingProperty.approvalStatus == Property.APPROVAL_CHANGES_REQUESTED ->
                Property.APPROVAL_PENDING
            existingProperty.approvalStatus == Property.APPROVAL_REJECTED ->
                Property.APPROVAL_PENDING
            else -> existingProperty.approvalStatus
        }

        val resubmitted = approvalStatus == Property.APPROVAL_PENDING &&
            existingProperty.approvalStatus != Property.APPROVAL_PENDING

        val reviewHistory = if (resubmitted) {
            existingProperty.reviewHistory + ReviewEntry(
                status = approvalStatus,
                comment = "Resubmitted for verification",
                actorRole = "owner"
            )
        } else {
            existingProperty.reviewHistory
        }

        val merged = property.copy(
            propertyId = existingProperty.propertyId,
            ownerId = existingProperty.ownerId,
            createdAt = existingProperty.createdAt,
            propertyStatus = existingProperty.propertyStatus,
            approvalStatus = approvalStatus,
            previousApprovalStatus = existingProperty.previousApprovalStatus,
            adminComment = if (resubmitted) "" else existingProperty.adminComment,
            requestedChangeFields = if (resubmitted) emptyList() else existingProperty.requestedChangeFields,
            submittedAt = if (resubmitted) System.currentTimeMillis() else existingProperty.submittedAt,
            reviewHistory = reviewHistory
        )

        propertiesRef.document(property.propertyId).set(merged, SetOptions.merge()).await()
        return merged
    }

    private fun Property.listingFingerprint(): Property = copy(
        propertyId = "",
        ownerId = "",
        approvalStatus = "",
        propertyStatus = "",
        previousApprovalStatus = "",
        adminComment = "",
        requestedChangeFields = emptyList(),
        reviewHistory = emptyList(),
        createdAt = 0L,
        submittedAt = 0L
    )

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

    suspend fun updatePropertyStatusAsOwner(
        propertyId: String,
        ownerId: String,
        propertyStatus: String
    ): Boolean {
        require(propertyStatus in Property.OWNER_SETTABLE_STATUSES) {
            "Owners may not set propertyStatus: $propertyStatus"
        }
        return try {
            val property = propertiesRef.document(propertyId).get().await().toProperty()
                ?: return false
            if (property.ownerId != ownerId) return false
            propertiesRef.document(propertyId)
                .set(mapOf("propertyStatus" to propertyStatus), SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error updating property status: ${e.message}", e)
            false
        }
    }

    suspend fun submitPropertyForReview(propertyId: String, ownerId: String): Boolean {
        return try {
            val property = propertiesRef.document(propertyId).get().await().toProperty() ?: return false
            if (property.ownerId != ownerId) return false
            if (property.approvalStatus !in Property.OWNER_EDITABLE_STATUSES) return false
            propertiesRef.document(propertyId).set(
                mapOf(
                    "approvalStatus" to Property.APPROVAL_PENDING,
                    "adminComment" to "",
                    "requestedChangeFields" to emptyList<String>(),
                    "submittedAt" to System.currentTimeMillis(),
                    "reviewHistory" to FieldValue.arrayUnion(
                        reviewEntryMap(Property.APPROVAL_PENDING, "Submitted for verification", emptyList(), "owner")
                    )
                ),
                SetOptions.merge()
            ).await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error submitting property: ${e.message}", e)
            false
        }
    }

    suspend fun approveProperty(propertyId: String) =
        moderateProperty(propertyId, Property.APPROVAL_APPROVED, "", emptyList())

    suspend fun rejectProperty(propertyId: String, reason: String) =
        moderateProperty(propertyId, Property.APPROVAL_REJECTED, reason, emptyList())

    suspend fun requestPropertyChanges(propertyId: String, comment: String, requestedFields: List<String>) =
        moderateProperty(propertyId, Property.APPROVAL_CHANGES_REQUESTED, comment, requestedFields)

    suspend fun archiveProperty(propertyId: String) =
        moderateProperty(propertyId, Property.APPROVAL_ARCHIVED, "", emptyList())

    suspend fun hideProperty(propertyId: String): Boolean {
        return try {
            val property = propertiesRef.document(propertyId).get().await().toProperty() ?: return false
            if (property.approvalStatus == Property.APPROVAL_HIDDEN) return true
            propertiesRef.document(propertyId).set(
                mapOf(
                    "previousApprovalStatus" to property.approvalStatus,
                    "approvalStatus" to Property.APPROVAL_HIDDEN,
                    "reviewHistory" to FieldValue.arrayUnion(
                        reviewEntryMap(Property.APPROVAL_HIDDEN, "Listing hidden", emptyList(), "admin")
                    )
                ),
                SetOptions.merge()
            ).await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error hiding property: ${e.message}", e)
            false
        }
    }

    suspend fun unhideProperty(propertyId: String): Boolean {
        return try {
            val property = propertiesRef.document(propertyId).get().await().toProperty() ?: return false
            val restored = property.previousApprovalStatus.ifBlank { Property.APPROVAL_APPROVED }
            propertiesRef.document(propertyId).set(
                mapOf(
                    "approvalStatus" to restored,
                    "previousApprovalStatus" to "",
                    "reviewHistory" to FieldValue.arrayUnion(
                        reviewEntryMap(restored, "Listing restored", emptyList(), "admin")
                    )
                ),
                SetOptions.merge()
            ).await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error unhiding property: ${e.message}", e)
            false
        }
    }

    private suspend fun moderateProperty(
        propertyId: String,
        approvalStatus: String,
        comment: String,
        requestedFields: List<String>
    ) {
        require(approvalStatus in Property.APPROVAL_STATUSES) {
            "Invalid approvalStatus: $approvalStatus"
        }
        propertiesRef.document(propertyId).set(
            mapOf(
                "approvalStatus" to approvalStatus,
                "adminComment" to comment,
                "requestedChangeFields" to requestedFields,
                "reviewHistory" to FieldValue.arrayUnion(
                    reviewEntryMap(approvalStatus, comment, requestedFields, "admin")
                )
            ),
            SetOptions.merge()
        ).await()
    }

    private fun reviewEntryMap(
        status: String,
        comment: String,
        requestedFields: List<String>,
        actorRole: String
    ): Map<String, Any> = mapOf(
        "status" to status,
        "comment" to comment,
        "requestedFields" to requestedFields,
        "actorRole" to actorRole,
        "createdAt" to System.currentTimeMillis()
    )

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

    suspend fun bookProperty(propertyId: String, bachelorId: String): BookingResult {
        if (propertyId.isBlank() || bachelorId.isBlank()) return BookingResult.PropertyNotFound

        val propertyRef = propertiesRef.document(propertyId)
        val bookingRef = requestsRef.document(bookingId(propertyId, bachelorId))

        return try {
            firestore.runTransaction { transaction ->
                val propertySnapshot = transaction.get(propertyRef)
                val property = propertySnapshot.toProperty()
                    ?: return@runTransaction BookingResult.PropertyNotFound

                if (property.ownerId == bachelorId) {
                    return@runTransaction BookingResult.CannotBookOwnProperty
                }
                if (
                    property.approvalStatus != Property.APPROVAL_APPROVED ||
                    property.propertyStatus !in Property.BOOKABLE_STATUSES
                ) {
                    return@runTransaction BookingResult.PropertyUnavailable
                }

                val existingBooking = transaction.get(bookingRef)
                if (
                    existingBooking.exists() &&
                    existingBooking.getString("status") in BookingRequest.ACTIVE_STATUSES
                ) {
                    return@runTransaction BookingResult.AlreadyBooked
                }

                val request = BookingRequest(
                    requestId = bookingRef.id,
                    bachelorId = bachelorId,
                    ownerId = property.ownerId,
                    propertyId = propertyId,
                    status = BookingRequest.STATUS_PENDING,
                    ownerDetailsVisible = true,
                    appliedAt = System.currentTimeMillis()
                )
                transaction.set(bookingRef, request)
                BookingResult.Success
            }.await()
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error booking property: ${e.message}", e)
            BookingResult.Failure(e.message)
        }
    }

    suspend fun updateBookingStatus(requestId: String, status: String) {
        require(status in BookingRequest.STATUSES) {
            "Invalid booking status: $status"
        }
        requestsRef.document(requestId)
            .set(mapOf("status" to status), SetOptions.merge())
            .await()
    }

    private fun bookingId(propertyId: String, bachelorId: String): String =
        "${propertyId}_$bachelorId"

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

    suspend fun getRequestsForBachelor(bachelorId: String): List<BookingRequest> {
        return try {
            requestsRef.whereEqualTo("bachelorId", bachelorId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toBookingRequest() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPropertiesForOwnerOnce(ownerId: String): List<Property> {
        return try {
            propertiesRef.whereEqualTo("ownerId", ownerId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toProperty() }
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

    // ---------------------------------------------------------------------
    // Favorites
    // ---------------------------------------------------------------------

    private fun favoriteId(userId: String, propertyId: String): String = "${userId}_$propertyId"

    fun listenToFavoriteIds(userId: String, onResult: (Set<String>) -> Unit): ListenerRegistration {
        return favoritesRef.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptySet())
                    return@addSnapshotListener
                }
                val ids = snapshot?.documents
                    ?.mapNotNull { it.getString("propertyId") }
                    ?.toSet()
                    ?: emptySet()
                onResult(ids)
            }
    }

    suspend fun setFavorite(userId: String, propertyId: String, isFavorite: Boolean) {
        if (userId.isBlank() || propertyId.isBlank()) return
        val doc = favoritesRef.document(favoriteId(userId, propertyId))
        if (isFavorite) {
            doc.set(
                mapOf(
                    "userId" to userId,
                    "propertyId" to propertyId,
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
        } else {
            doc.delete().await()
        }
    }

    // ---------------------------------------------------------------------
    // Tenant reviews
    // ---------------------------------------------------------------------

    private fun DocumentSnapshot.toReview(): PropertyReview? {
        return toObject(PropertyReview::class.java)?.apply {
            if (reviewId.isBlank()) reviewId = id
        }
    }

    fun listenToReviewsForProperty(
        propertyId: String,
        onResult: (List<PropertyReview>) -> Unit
    ): ListenerRegistration {
        return reviewsRef.whereEqualTo("propertyId", propertyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PropertyRepository", "Reviews listener failed: ${error.message}", error)
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents
                    ?.mapNotNull { it.toReview() }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                onResult(reviews)
            }
    }

    suspend fun submitReview(review: PropertyReview) {
        // One review per reviewer per property; a re-submit overwrites the old one.
        val docId = "${review.propertyId}_${review.reviewerId}"
        val toSave = review.copy(reviewId = docId, createdAt = System.currentTimeMillis())
        reviewsRef.document(docId).set(toSave).await()
    }

    // ---------------------------------------------------------------------
    // Listing deletion
    // ---------------------------------------------------------------------

    suspend fun deleteProperty(propertyId: String, ownerId: String): Boolean {
        return try {
            val property = propertiesRef.document(propertyId).get().await().toProperty() ?: return false
            if (property.ownerId != ownerId) return false

            // Best-effort cleanup of related enquiries so they don't dangle.
            val relatedRequests = requestsRef.whereEqualTo("propertyId", propertyId).get().await()
            for (doc in relatedRequests.documents) {
                doc.reference.delete()
            }
            propertiesRef.document(propertyId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error deleting property: ${e.message}", e)
            false
        }
    }
}
