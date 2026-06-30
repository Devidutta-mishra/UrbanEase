package com.example.urbanease.screens.admin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.Property
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

    fun approveProperty(propertyId: String) = moderate(propertyId) {
        propertyRepository.approveProperty(propertyId)
        patchModeration(propertyId, Property.APPROVAL_APPROVED, "", emptyList())
    }

    fun rejectProperty(propertyId: String, reason: String) = moderate(propertyId) {
        propertyRepository.rejectProperty(propertyId, reason)
        patchModeration(propertyId, Property.APPROVAL_REJECTED, reason, emptyList())
    }

    fun requestPropertyChanges(propertyId: String, comment: String, fields: List<String>) =
        moderate(propertyId) {
            propertyRepository.requestPropertyChanges(propertyId, comment, fields)
            patchModeration(propertyId, Property.APPROVAL_CHANGES_REQUESTED, comment, fields)
        }

    fun archiveProperty(propertyId: String) = moderate(propertyId) {
        propertyRepository.archiveProperty(propertyId)
        patchModeration(propertyId, Property.APPROVAL_ARCHIVED, "", emptyList())
    }

    fun hideProperty(propertyId: String) = moderate(propertyId) {
        propertyRepository.hideProperty(propertyId)
    }

    fun unhideProperty(propertyId: String) = moderate(propertyId) {
        propertyRepository.unhideProperty(propertyId)
    }

    private fun moderate(propertyId: String, action: suspend () -> Unit) {
        if (!uiState.isAdmin) return
        viewModelScope.launch {
            try {
                action()
            } catch (e: Exception) {
                Log.e("AdminHomeVM", "Moderation failed for $propertyId: ${e.message}", e)
            }
        }
    }

    private fun patchModeration(
        propertyId: String,
        approvalStatus: String,
        adminComment: String,
        requestedChangeFields: List<String>
    ) {
        uiState = uiState.copy(
            properties = uiState.properties.map {
                if (it.propertyId == propertyId) {
                    it.copy(
                        approvalStatus = approvalStatus,
                        adminComment = adminComment,
                        requestedChangeFields = requestedChangeFields
                    )
                } else {
                    it
                }
            }
        )
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
