package com.example.urbanease.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Property(
    @get:PropertyName("propertyId")
    @set:PropertyName("propertyId")
    var propertyId: String = "",

    @get:PropertyName("ownerId")
    @set:PropertyName("ownerId")
    var ownerId: String = "",

    @get:PropertyName("location")
    @set:PropertyName("location")
    var location: String = "",

    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("rent")
    @set:PropertyName("rent")
    var rent: Long = 0,

    @get:PropertyName("rooms")
    @set:PropertyName("rooms")
    var rooms: Int = 0,

    @get:PropertyName("bathrooms")
    @set:PropertyName("bathrooms")
    var bathrooms: Int = 0,

    @get:PropertyName("floorNo")
    @set:PropertyName("floorNo")
    var floorNo: String = "",

    @get:PropertyName("furnishing")
    @set:PropertyName("furnishing")
    var furnishing: String = "",

    @get:PropertyName("imageUrls")
    @set:PropertyName("imageUrls")
    var imageUrls: List<String> = emptyList(),

    @get:PropertyName("approvalStatus")
    @set:PropertyName("approvalStatus")
    var approvalStatus: String = APPROVAL_PENDING,

    @get:PropertyName("propertyStatus")
    @set:PropertyName("propertyStatus")
    var propertyStatus: String = PROPERTY_AVAILABLE,

    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val APPROVAL_PENDING = "pending"
        const val APPROVAL_APPROVED = "approved"
        const val APPROVAL_REJECTED = "rejected"
        const val APPROVAL_UNDER_REVIEW = "under_review"

        const val PROPERTY_AVAILABLE = "available"
        const val PROPERTY_BOOKED = "booked"
        const val PROPERTY_OCCUPIED = "occupied"
        const val PROPERTY_INACTIVE = "inactive"
        const val PROPERTY_MAINTENANCE = "maintenance"

        val APPROVAL_STATUSES = setOf(
            APPROVAL_PENDING,
            APPROVAL_APPROVED,
            APPROVAL_REJECTED,
            APPROVAL_UNDER_REVIEW
        )

        val PROPERTY_STATUSES = setOf(
            PROPERTY_AVAILABLE,
            PROPERTY_BOOKED,
            PROPERTY_OCCUPIED,
            PROPERTY_INACTIVE,
            PROPERTY_MAINTENANCE
        )
    }

    @get:Exclude
    @set:Exclude
    @Deprecated("Use propertyId. Kept temporarily for older call sites/data migration.")
    var houseId: String
        get() = propertyId
        set(value) {
            propertyId = value
        }
}
