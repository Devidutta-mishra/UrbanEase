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

    @get:PropertyName("propertyType")
    @set:PropertyName("propertyType")
    var propertyType: String = "",

    @get:PropertyName("balcony")
    @set:PropertyName("balcony")
    var balcony: String = "",

    @get:PropertyName("totalFloors")
    @set:PropertyName("totalFloors")
    var totalFloors: String = "",

    @get:PropertyName("parking")
    @set:PropertyName("parking")
    var parking: String = "",

    @get:PropertyName("preferredTenant")
    @set:PropertyName("preferredTenant")
    var preferredTenant: String = "",

    @get:PropertyName("leaseDuration")
    @set:PropertyName("leaseDuration")
    var leaseDuration: String = "",

    @get:PropertyName("availableFrom")
    @set:PropertyName("availableFrom")
    var availableFrom: Long = 0L,

    @get:PropertyName("securityDeposit")
    @set:PropertyName("securityDeposit")
    var securityDeposit: Long = 0L,

    @get:PropertyName("maintenanceCharges")
    @set:PropertyName("maintenanceCharges")
    var maintenanceCharges: Long = 0L,

    @get:PropertyName("electricityIncluded")
    @set:PropertyName("electricityIncluded")
    var electricityIncluded: Boolean = false,

    @get:PropertyName("waterIncluded")
    @set:PropertyName("waterIncluded")
    var waterIncluded: Boolean = false,

    @get:PropertyName("petFriendly")
    @set:PropertyName("petFriendly")
    var petFriendly: Boolean = false,

    @get:PropertyName("smokingAllowed")
    @set:PropertyName("smokingAllowed")
    var smokingAllowed: Boolean = false,

    @get:PropertyName("liftAvailable")
    @set:PropertyName("liftAvailable")
    var liftAvailable: Boolean = false,

    @get:PropertyName("gatedSociety")
    @set:PropertyName("gatedSociety")
    var gatedSociety: Boolean = false,

    @get:PropertyName("powerBackup")
    @set:PropertyName("powerBackup")
    var powerBackup: Boolean = false,

    @get:PropertyName("wifiAvailable")
    @set:PropertyName("wifiAvailable")
    var wifiAvailable: Boolean = false,

    @get:PropertyName("kitchenAvailable")
    @set:PropertyName("kitchenAvailable")
    var kitchenAvailable: Boolean = false,

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
    var createdAt: Long = System.currentTimeMillis(),

    @get:PropertyName("submittedAt")
    @set:PropertyName("submittedAt")
    var submittedAt: Long = 0L,

    @get:PropertyName("adminComment")
    @set:PropertyName("adminComment")
    var adminComment: String = "",

    @get:PropertyName("requestedChangeFields")
    @set:PropertyName("requestedChangeFields")
    var requestedChangeFields: List<String> = emptyList(),

    @get:PropertyName("previousApprovalStatus")
    @set:PropertyName("previousApprovalStatus")
    var previousApprovalStatus: String = "",

    @get:PropertyName("reviewHistory")
    @set:PropertyName("reviewHistory")
    var reviewHistory: List<ReviewEntry> = emptyList()
) {
    companion object {
        const val APPROVAL_DRAFT = "draft"
        const val APPROVAL_PENDING = "pending"
        const val APPROVAL_UNDER_REVIEW = "under_review"
        const val APPROVAL_CHANGES_REQUESTED = "changes_requested"
        const val APPROVAL_APPROVED = "approved"
        const val APPROVAL_REJECTED = "rejected"
        const val APPROVAL_HIDDEN = "hidden"
        const val APPROVAL_ARCHIVED = "archived"

        const val PROPERTY_AVAILABLE = "available"
        const val PROPERTY_RENTED = "rented"
        const val PROPERTY_SOLD = "sold"
        const val PROPERTY_BOOKED = "booked"
        const val PROPERTY_OCCUPIED = "occupied"
        const val PROPERTY_INACTIVE = "inactive"
        const val PROPERTY_MAINTENANCE = "maintenance"

        val APPROVAL_STATUSES = setOf(
            APPROVAL_DRAFT,
            APPROVAL_PENDING,
            APPROVAL_UNDER_REVIEW,
            APPROVAL_CHANGES_REQUESTED,
            APPROVAL_APPROVED,
            APPROVAL_REJECTED,
            APPROVAL_HIDDEN,
            APPROVAL_ARCHIVED
        )

        // States in which the owner is allowed to edit and (re)submit the listing.
        val OWNER_EDITABLE_STATUSES = setOf(
            APPROVAL_DRAFT,
            APPROVAL_CHANGES_REQUESTED,
            APPROVAL_REJECTED
        )

        // States in which the listing is locked because the admin is reviewing it.
        val UNDER_REVIEW_STATUSES = setOf(
            APPROVAL_PENDING,
            APPROVAL_UNDER_REVIEW
        )

        val PROPERTY_STATUSES = setOf(
            PROPERTY_AVAILABLE,
            PROPERTY_RENTED,
            PROPERTY_SOLD,
            PROPERTY_BOOKED,
            PROPERTY_OCCUPIED,
            PROPERTY_INACTIVE,
            PROPERTY_MAINTENANCE
        )

        // Statuses a property owner is allowed to set manually.
        val OWNER_SETTABLE_STATUSES = setOf(
            PROPERTY_AVAILABLE,
            PROPERTY_RENTED,
            PROPERTY_SOLD
        )

        // Statuses where the listing is open for new booking requests.
        val BOOKABLE_STATUSES = setOf(
            PROPERTY_AVAILABLE
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
