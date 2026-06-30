package com.example.urbanease.model

import com.google.firebase.firestore.PropertyName

data class BookingRequest(
    @get:PropertyName("requestId")
    @set:PropertyName("requestId")
    var requestId: String = "",

    @get:PropertyName("bachelorId")
    @set:PropertyName("bachelorId")
    var bachelorId: String = "",

    @get:PropertyName("ownerId")
    @set:PropertyName("ownerId")
    var ownerId: String = "",

    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = STATUS_PENDING,

    @get:PropertyName("propertyId")
    @set:PropertyName("propertyId")
    var propertyId: String = "",

    @get:PropertyName("ownerDetailsVisible")
    @set:PropertyName("ownerDetailsVisible")
    var ownerDetailsVisible: Boolean = true,

    @get:PropertyName("appliedAt")
    @set:PropertyName("appliedAt")
    var appliedAt: Long = System.currentTimeMillis(),
    
    // Placeholder for future payment integration
    @get:PropertyName("paymentCompleted")
    @set:PropertyName("paymentCompleted")
    var paymentCompleted: Boolean = false,

    @get:PropertyName("accessGranted")
    @set:PropertyName("accessGranted")
    var accessGranted: Boolean = true,

    @get:PropertyName("adminNote")
    @set:PropertyName("adminNote")
    var adminNote: String = ""
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_CONFIRMED = "confirmed"
        const val STATUS_REJECTED = "rejected"
        const val STATUS_CANCELLED = "cancelled"
        const val STATUS_COMPLETED = "completed"

        val STATUSES = setOf(
            STATUS_PENDING,
            STATUS_CONFIRMED,
            STATUS_REJECTED,
            STATUS_CANCELLED,
            STATUS_COMPLETED
        )

        // Statuses that occupy a property and block a bachelor from re-booking it.
        val ACTIVE_STATUSES = setOf(
            STATUS_PENDING,
            STATUS_CONFIRMED,
            STATUS_COMPLETED
        )
    }
}
