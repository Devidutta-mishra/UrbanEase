package com.example.urbanease.repository

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
        usersRef.document(userId).get()
            .addOnSuccessListener { document ->
                onResult(document.toObject(MUser::class.java))
            }
            .addOnFailureListener {
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
}
