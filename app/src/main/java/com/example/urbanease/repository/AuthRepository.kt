package com.example.urbanease.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun login(
        email: String,
        password: String,
        onResult: (Result<String>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: currentUserId
                    if (userId != null) {
                        Log.d(TAG, "Login successful for userId: $userId")
                        onResult(Result.success(userId))
                    } else {
                        Log.w(TAG, "Login completed without an authenticated user")
                        onResult(Result.failure(IllegalStateException("Unable to determine current user")))
                    }
                } else {
                    val exception = task.exception ?: Exception("Login failed")
                    Log.w(TAG, "Login failed", exception)
                    onResult(Result.failure(exception))
                }
            }
    }

    fun signup(
        email: String,
        password: String,
        onResult: (Result<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: currentUserId
                    if (userId != null) {
                        Log.d(TAG, "Signup successful for userId: $userId")
                        onResult(Result.success(userId))
                    } else {
                        Log.w(TAG, "Signup completed without an authenticated user")
                        onResult(Result.failure(IllegalStateException("Unable to determine current user")))
                    }
                } else {
                    val exception = task.exception ?: Exception("Signup failed")
                    Log.w(TAG, "Signup failed", exception)
                    onResult(Result.failure(exception))
                }
            }
    }

    fun logout() {
        auth.signOut()
        Log.d(TAG, "User signed out")
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
