package com.example.urbanease.data

import com.google.firebase.firestore.PropertyName

data class PropertyAd(
    @get:PropertyName("houseId")
    @set:PropertyName("houseId")
    var houseId: String = "",

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

    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = "available",

    @get:PropertyName("isApproved")
    @set:PropertyName("isApproved")
    var isApproved: Boolean = false,

    @get:PropertyName("adminNote")
    @set:PropertyName("adminNote")
    var adminNote: String = "",

    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
