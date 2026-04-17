package com.example.urbanease.model

import com.google.firebase.firestore.PropertyName

data class BookingRequest(
    @get:PropertyName("requestId")
    @set:PropertyName("requestId")
    var requestId: String = "",

    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("ownerId")
    @set:PropertyName("ownerId")
    var ownerId: String = "",

    @get:PropertyName("propertyId")
    @set:PropertyName("propertyId")
    var propertyId: String = "",

    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = "pending",

    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
