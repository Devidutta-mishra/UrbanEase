package com.example.urbanease.repository

import android.util.Log
import com.example.urbanease.model.MUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersRef = firestore.collection("users")

    fun getUser(userId: String, onResult: (MUser?) -> Unit) {
        Log.d("UrbanEase_Debug", "UserRepository: getUser starting for UID: $userId")
        try {
            usersRef.document(userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            val user = document.toObject(MUser::class.java)
                            Log.d("UrbanEase_Debug", "UserRepository: getUser success. User found: ${user?.displayName}, Role: ${user?.role}")
                            onResult(user)
                        } else {
                            Log.d("UrbanEase_Debug", "UserRepository: getUser success but document does not exist or is null")
                            onResult(null)
                        }
                    } else {
                        val exception = task.exception
                        Log.e("UrbanEase_Debug", "UserRepository: getUser task failed", exception)
                        onResult(null)
                    }
                }
        } catch (e: Exception) {
            Log.e("UrbanEase_Debug", "UserRepository: Exception in getUser", e)
            onResult(null)
        }
    }

    fun listenToAllUsers(onResult: (List<MUser>) -> Unit): ListenerRegistration {
        return usersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            val users = snapshot?.toObjects(MUser::class.java) ?: emptyList()
            onResult(users)
        }
    }

    fun saveUser(user: MUser, onResult: (Boolean) -> Unit) {
        usersRef.document(user.userId).set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateUserEmail(userId: String, email: String) {
        usersRef.document(userId).update("email", email)
    }

    fun updateDisplayName(userId: String, displayName: String) {
        usersRef.document(userId).update("displayName", displayName)
    }
}
