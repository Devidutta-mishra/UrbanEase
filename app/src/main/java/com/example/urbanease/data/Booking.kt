package com.example.urbanease.data

import com.google.firebase.firestore.PropertyName

data class Booking(
    @get:PropertyName("bookingId")
    @set:PropertyName("bookingId")
    var bookingId: String = "",

    @get:PropertyName("houseId")
    @set:PropertyName("houseId")
    var houseId: String = "",

    @get:PropertyName("ownerId")
    @set:PropertyName("ownerId")
    var ownerId: String = "",

    @get:PropertyName("bachelorId")
    @set:PropertyName("bachelorId")
    var bachelorId: String = "",

    @get:PropertyName("bachelorEmail")
    @set:PropertyName("bachelorEmail")
    var bachelorEmail: String = "",

    @get:PropertyName("houseTitle")
    @set:PropertyName("houseTitle")
    var houseTitle: String = "",

    @get:PropertyName("houseLocation")
    @set:PropertyName("houseLocation")
    var houseLocation: String = "",

    @get:PropertyName("rent")
    @set:PropertyName("rent")
    var rent: Long = 0,

    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = "pending", // pending, approved, rejected

    @get:PropertyName("timestamp")
    @set:PropertyName("timestamp")
    var timestamp: Long = System.currentTimeMillis()
)
