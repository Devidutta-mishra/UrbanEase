package com.example.urbanease.screens.admin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import com.example.urbanease.repository.UserRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    var uiState by mutableStateOf(AdminHomeUiState())
        private set

    private var propertiesListener: ListenerRegistration? = null
    private var usersListener: ListenerRegistration? = null

    init {
        checkAdminRoleAndFetch()
    }

    private fun checkAdminRoleAndFetch() {
        val currentUserId = authRepository.currentUserId
        if (currentUserId == null) {
            Log.e("AdminHomeVM", "No user logged in")
            return
        }

        uiState = uiState.copy(isLoading = true)
        userRepository.getUser(currentUserId) { user ->
            if (user?.role == "admin") {
                uiState = uiState.copy(isAdmin = true)
                Log.d("AdminHomeVM", "Admin verified. Fetching data...")
                fetchAllData()
            } else {
                uiState = uiState.copy(isAdmin = false, isLoading = false)
                Log.e("AdminHomeVM", "Unauthorized: User is not an admin. Role: ${user?.role}")
            }
        }
    }

    private fun fetchAllData() {
        // Listen to all properties
        propertiesListener?.remove()
        propertiesListener = propertyRepository.listenToAllProperties { fetchedProperties ->
            uiState = uiState.copy(properties = fetchedProperties, isLoading = false)
        }

        // Listen to all users to filter owners and bachelors
        usersListener?.remove()
        usersListener = userRepository.listenToAllUsers { allUsers ->
            uiState = uiState.copy(
                owners = allUsers.filter { it.role == "owner" },
                bachelors = allUsers.filter { it.role == "bachelor" }
            )
        }
    }

    fun updateApprovalStatus(propertyId: String, approvalStatus: String) {
        if (!uiState.isAdmin) return
        viewModelScope.launch {
            try {
                propertyRepository.updateApprovalStatus(propertyId, approvalStatus)
                uiState = uiState.copy(
                    properties = uiState.properties.map {
                        if (it.propertyId == propertyId) it.copy(approvalStatus = approvalStatus) else it
                    }
                )
            } catch (e: Exception) {
                Log.e("AdminHomeVM", "Unable to update approval status: ${e.message}", e)
            }
        }
    }

    fun updatePropertyStatus(propertyId: String, propertyStatus: String) {
        if (!uiState.isAdmin) return
        viewModelScope.launch {
            try {
                propertyRepository.updatePropertyStatus(propertyId, propertyStatus)
                uiState = uiState.copy(
                    properties = uiState.properties.map {
                        if (it.propertyId == propertyId) it.copy(propertyStatus = propertyStatus) else it
                    }
                )
            } catch (e: Exception) {
                Log.e("AdminHomeVM", "Unable to update property status: ${e.message}", e)
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    override fun onCleared() {
        super.onCleared()
        propertiesListener?.remove()
        usersListener?.remove()
    }
}
