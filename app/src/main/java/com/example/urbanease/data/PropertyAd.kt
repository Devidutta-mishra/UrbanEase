package com.example.urbanease.data

data class PropertyAd(
    val houseId: String = "",
    val ownerId: String = "",
    val location: String = "",
    val title: String = "",
    val description: String = "",
    val rent: Int = 0,
    val rooms: Int = 0,
    val bathrooms: Int = 0
)
