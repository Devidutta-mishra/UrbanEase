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

    var emailError = mutableStateOf<String?>(null)
        private set

    var passwordError = mutableStateOf<String?>(null)
        private set

    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set


    fun validateEmail(email: String) {
        val cleanEmail = email.trim()

        emailError.value = when {
            cleanEmail.isBlank() -> "Email cannot be empty"
            cleanEmail.any { it.isWhitespace() } -> "Email should not contain spaces"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun validatePassword(password: String) {
        passwordError.value = when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }


    fun onEmailChange(newEmail: String) {
        email.value = newEmail
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
        validatePassword(newPassword)
    }
    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        role: String,
        home: () -> Unit
    ) {

        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            error.value = "Email and Password cannot be empty"
            return
        }
        if (cleanEmail.any { it.isWhitespace() }) {
            error.value = "Email should not contain spaces"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            error.value = "Invalid email format"
            return
        }

        if (loading.value) return

        loading.value = true
        error.value = null

        auth.createUserWithEmailAndPassword(cleanEmail, cleanPassword)
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

        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            error.value = "Email and Password cannot be empty"
            return
        }

        if (cleanEmail.any { it.isWhitespace() }) {
            error.value = "Email should not contain spaces"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            error.value = "Invalid email format"
            return
        }

        if (loading.value) return

        loading.value = true
        error.value = null

        auth.signInWithEmailAndPassword(cleanEmail, cleanPassword)
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
