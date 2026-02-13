package com.example.urbanease.model

data class House(
    val houseId: String = "",
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val rent: String = "",
    val imageUrl: String = "" // optional for now
)
