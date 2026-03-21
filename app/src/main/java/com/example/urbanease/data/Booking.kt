package com.example.urbanease.data

data class Booking(
    val bookingId: String = "",
    val houseId: String = "",
    val ownerId: String = "",
    val userId: String = "", // Matches your Firestore rules (renter)
    val bachelorEmail: String = "",
    val houseTitle: String = "",
    val rent: Long = 0,
    val status: String = "pending", // pending, approved, rejected
    val timestamp: Long = System.currentTimeMillis()
)
