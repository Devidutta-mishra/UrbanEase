package com.example.urbanease.screens.login

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.MUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow

class LoginScreenViewModel : ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    internal val auth: FirebaseAuth = Firebase.auth

    var loading: MutableState<Boolean> = mutableStateOf(false)
        private set

    var error: MutableState<String?> = mutableStateOf(null)
        private set

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        role: String,
        home: () -> Unit
    ) {
        if (loading.value) return

        loading.value = true
        error.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val displayName = task.result?.user?.email?.split('@')?.get(0)
                    val userId = auth.currentUser?.uid

                    val user = MUser(
                        userId = userId.toString(),
                        displayName = displayName.toString(),
                        avatarUrl = "",
                        role = role,
                        id = null
                    ).toMap()

                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId!!)
                        .set(user)
                        .addOnSuccessListener {
                            loading.value = false
                            Log.d("Signup", "User created with role: $role")
                            home()
                        }
                        .addOnFailureListener { e ->
                            loading.value = false
                            error.value = "Failed to save user data: ${e.message}"
                            Log.d("Signup", "Error saving user: ${e.message}")
                        }
                } else {
                    loading.value = false
                    error.value = task.exception?.message ?: "Signup failed"
                    Log.d("createUserWithEmailAndPassword", "Error: ${task.exception?.message}")
                }
            }
    }


    fun signInWithEmailAndPassword(email: String, password: String, home: (String) -> Unit) {
        if (loading.value) return

        loading.value = true
        error.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        FirebaseFirestore.getInstance().collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                loading.value = false
                                if (document != null && document.exists()) {
                                    val role = document.getString("role") ?: "unknown"
                                    Log.d("Login", "Role: $role")
                                    home(role)
                                } else {
                                    error.value = "User role not found"
                                    Log.d("Login", "No such user document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                loading.value = false
                                error.value = "Error getting user role: ${exception.message}"
                                Log.d("Login", "Error getting user role: ${exception.message}")
                            }
                    }
                } else {
                    loading.value = false
                    error.value = task.exception?.message ?: "Login failed"
                    Log.d("Login", "signIn failed: ${task.exception?.message}")
                }
            }
    }
}
