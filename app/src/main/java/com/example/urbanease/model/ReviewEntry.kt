package com.example.urbanease.model

import com.google.firebase.firestore.PropertyName

data class ReviewEntry(
    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = "",

    @get:PropertyName("comment")
    @set:PropertyName("comment")
    var comment: String = "",

    @get:PropertyName("requestedFields")
    @set:PropertyName("requestedFields")
    var requestedFields: List<String> = emptyList(),

    @get:PropertyName("actorRole")
    @set:PropertyName("actorRole")
    var actorRole: String = "",

    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
