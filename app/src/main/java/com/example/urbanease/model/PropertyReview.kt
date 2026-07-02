package com.example.urbanease.model

import com.google.firebase.firestore.PropertyName

data class PropertyReview(
    @get:PropertyName("reviewId")
    @set:PropertyName("reviewId")
    var reviewId: String = "",

    @get:PropertyName("propertyId")
    @set:PropertyName("propertyId")
    var propertyId: String = "",

    @get:PropertyName("ownerId")
    @set:PropertyName("ownerId")
    var ownerId: String = "",

    @get:PropertyName("reviewerId")
    @set:PropertyName("reviewerId")
    var reviewerId: String = "",

    @get:PropertyName("reviewerName")
    @set:PropertyName("reviewerName")
    var reviewerName: String = "",

    @get:PropertyName("rating")
    @set:PropertyName("rating")
    var rating: Int = 0,

    @get:PropertyName("comment")
    @set:PropertyName("comment")
    var comment: String = "",

    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
