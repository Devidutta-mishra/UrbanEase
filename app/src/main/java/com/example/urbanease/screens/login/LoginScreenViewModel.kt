package com.example.urbanease.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.MUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class  LoginScreenViewModel : ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    internal val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)

    val loading: LiveData<Boolean> = _loading

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        role: String,
        home: () -> Unit
    ) {
        viewModelScope.launch {
            if (_loading.value == false) {
                _loading.value = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        _loading.value = false
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
                                    Log.d("Signup", "User created with role: $role")
                                    home()
                                }
                                .addOnFailureListener { e ->
                                    Log.d("Signup", "Error saving user: ${e.message}")
                                }

                        } else {
                            Log.d(
                                "createUserWithEmailAndPassword",
                                "Error: ${task.exception?.message}"
                            )
                        }
                    }
            }
        }
    }


    fun signInWithEmailAndPassword(email: String, password: String, home: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(userId)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.exists()) {
                                            val role = document.getString("role") ?: "unknown"
                                            Log.d("Login", "Role: $role")
                                            home(role)
                                        } else {
                                            Log.d("Login", "No such user document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("Login", "Error getting user role: ${exception.message}")
                                    }
                            }
                        } else {
                            Log.d("Login", "signIn failed: ${task.exception?.message}")
                        }
                    }
            } catch (ex: Exception) {
                Log.d("Login", "Exception: ${ex.message}")
            }
        }
    }

    private fun createUser(displayName: String?,role: String) {
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

    }
}