package com.example.urbanease.model

import com.google.firebase.firestore.PropertyName

data class MUser(
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",

    @get:PropertyName("display_name")
    @set:PropertyName("display_name")
    var displayName: String = "",

    @get:PropertyName("avatar_url")
    @set:PropertyName("avatar_url")
    var avatarUrl: String = "",

    @get:PropertyName("role")
    @set:PropertyName("role")
    var role: String = "",

    @get:PropertyName("phone_number")
    @set:PropertyName("phone_number")
    var phoneNumber: String = "",

    var id: String? = null
){
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "user_id" to this.userId,
            "display_name" to this.displayName,
            "avatar_url" to this.avatarUrl,
            "role" to this.role,
            "phone_number" to this.phoneNumber
        )
    }
}
