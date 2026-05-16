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
    var status: String = "pending",

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
)
