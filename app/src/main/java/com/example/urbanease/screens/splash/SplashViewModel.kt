package com.example.urbanease.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getCurrentUserRole(onResult: (String?) -> Unit) {
        val currentUser = authRepository.currentUser
        Log.d("UrbanEase_Debug", "SplashViewModel: getCurrentUserRole called. Current user email: ${currentUser?.email}")
        
        if (currentUser?.email.isNullOrEmpty()) {
            Log.d("UrbanEase_Debug", "SplashViewModel: No current user or email is empty. Returning null role.")
            onResult(null)
            return
        }

        Log.d("UrbanEase_Debug", "SplashViewModel: Fetching user document from repository for UID: ${currentUser?.uid}")
        userRepository.getUser(currentUser!!.uid) { user ->
            Log.d("UrbanEase_Debug", "SplashViewModel: Received user from repository: $user. Role: ${user?.role}")
            onResult(user?.role)
        }
    }
}
