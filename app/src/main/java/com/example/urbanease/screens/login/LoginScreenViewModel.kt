package com.example.urbanease.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    var uiState by mutableStateOf(LoginUiState())
        private set


    fun validateEmail(email: String) {
        val cleanEmail = email.trim()

        val emailError = when {
            cleanEmail.isBlank() -> "Email cannot be empty"
            cleanEmail.any { it.isWhitespace() } -> "Email should not contain spaces"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() -> {
                if (cleanEmail.contains(".com")) "Invalid email format" else null
            }
            else -> null
        }
        uiState = uiState.copy(emailError = emailError)
    }

    fun validatePassword(password: String) {
        val passwordError = when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        uiState = uiState.copy(passwordError = passwordError)
    }


    fun onEmailChange(newEmail: String) {
        uiState = uiState.copy(email = newEmail)
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        uiState = uiState.copy(password = newPassword)
        validatePassword(newPassword)
    }
    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        role: String,
        displayName: String,
        phoneNumber: String,
        home: () -> Unit
    ) {

        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            uiState = uiState.copy(error = "Email and Password cannot be empty")
            return
        }
        if (cleanEmail.any { it.isWhitespace() }) {
            uiState = uiState.copy(error = "Email should not contain spaces")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            uiState = uiState.copy(error = "Invalid email format")
            return
        }

        if (uiState.isLoading) return

        startLoading()

        authRepository.signup(cleanEmail, cleanPassword) { result ->
            result
                .onSuccess { userId ->
                    val user = MUser(
                        userId = userId,
                        displayName = displayName,
                        avatarUrl = "",
                        role = role,
                        phoneNumber = phoneNumber,
                        email = cleanEmail,
                        id = null
                    )

                    userRepository.saveUser(user) { saved ->
                        if (saved) {
                            Log.d("Signup", "User created with role: $role")
                            finishLoading()
                            home()
                        } else {
                            finishLoading()
                            uiState = uiState.copy(error = "Failed to save user data")
                            Log.d("Signup", "Error saving user")
                        }
                    }
                }
                .onFailure { exception ->
                    finishLoading()
                    uiState = uiState.copy(error = exception.message ?: "Signup failed")
                    Log.d("createUserWithEmailAndPassword", "Error: ${exception.message}")
                }
        }
    }


    fun signInWithEmailAndPassword(email: String, password: String, home: (String) -> Unit) {

        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            uiState = uiState.copy(error = "Email and Password cannot be empty")
            return
        }

        if (cleanEmail.any { it.isWhitespace() }) {
            uiState = uiState.copy(error = "Email should not contain spaces")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            uiState = uiState.copy(error = "Invalid email format")
            return
        }

        if (uiState.isLoading) return

        startLoading()

        authRepository.login(cleanEmail, cleanPassword) { result ->
            result
                .onSuccess { userId ->
                    userRepository.getUser(userId) { user ->
                        finishLoading()
                        if (user != null) {
                            if (user.suspended) {
                                authRepository.logout()
                                uiState = uiState.copy(
                                    error = "Your account has been suspended. Please contact support."
                                )
                                Log.d("Login", "Suspended account blocked: $userId")
                                return@getUser
                            }
                            userRepository.updateUserEmail(userId, cleanEmail)
                            val role = user.role.ifBlank { "unknown" }
                            Log.d("Login", "Role: $role")
                            home(role)
                        } else {
                            uiState = uiState.copy(error = "User role not found")
                            Log.d("Login", "No such user document")
                        }
                    }
                }
                .onFailure { exception ->
                    finishLoading()
                    uiState = uiState.copy(error = exception.message ?: "Login failed")
                    Log.d("Login", "signIn failed: ${exception.message}")
                }
        }
    }

    private fun startLoading() {
        uiState = uiState.copy(isLoading = true, error = null)
    }

    private fun finishLoading() {
        uiState = uiState.copy(isLoading = false)
    }
}
