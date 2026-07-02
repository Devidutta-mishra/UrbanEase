package com.example.urbanease.screens.admin.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.MUser
import com.example.urbanease.model.Property
import com.example.urbanease.repository.PropertyRepository
import com.example.urbanease.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnquiryRow(
    val propertyTitle: String,
    val propertyLocation: String,
    val appliedAt: Long
)

data class AdminUserDetailUiState(
    val user: MUser? = null,
    val properties: List<Property> = emptyList(),
    val enquiries: List<EnquiryRow> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AdminUserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    var uiState by mutableStateOf(AdminUserDetailUiState())
        private set

    fun load(userId: String) {
        uiState = uiState.copy(isLoading = true)
        userRepository.getUser(userId) { user ->
            uiState = uiState.copy(user = user)
            if (user == null) {
                uiState = uiState.copy(isLoading = false)
                return@getUser
            }
            viewModelScope.launch {
                when (user.role) {
                    "owner" -> {
                        val props = propertyRepository.getPropertiesForOwnerOnce(userId)
                        uiState = uiState.copy(properties = props, isLoading = false)
                    }
                    "bachelor" -> {
                        val requests = propertyRepository.getRequestsForBachelor(userId)
                        val propMap = propertyRepository.getPropertiesByIds(
                            requests.map { it.propertyId }.distinct()
                        )
                        val rows = requests
                            .sortedByDescending { it.appliedAt }
                            .map { req ->
                                val p = propMap[req.propertyId]
                                EnquiryRow(
                                    propertyTitle = p?.title ?: "Unknown property",
                                    propertyLocation = p?.location ?: "",
                                    appliedAt = req.appliedAt
                                )
                            }
                        uiState = uiState.copy(enquiries = rows, isLoading = false)
                    }
                    else -> uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }

    fun setSuspended(suspended: Boolean) {
        val user = uiState.user ?: return
        userRepository.setUserSuspended(user.userId, suspended) { success ->
            if (success) {
                uiState = uiState.copy(user = user.copy(suspended = suspended))
            }
        }
    }
}
