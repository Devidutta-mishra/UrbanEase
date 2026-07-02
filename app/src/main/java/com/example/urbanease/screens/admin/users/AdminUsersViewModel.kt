package com.example.urbanease.screens.admin.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.UserRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AdminUsersUiState(
    val users: List<MUser> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val roleFilter: String = "All"
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(AdminUsersUiState(isLoading = true))
        private set

    private var usersListener: ListenerRegistration? = null

    init {
        usersListener = userRepository.listenToAllUsers { users ->
            uiState = uiState.copy(
                users = users.filter { it.role == "owner" || it.role == "bachelor" },
                isLoading = false
            )
        }
    }

    val filteredUsers: List<MUser>
        get() {
            var result = uiState.users
            when (uiState.roleFilter) {
                "Owners" -> result = result.filter { it.role == "owner" }
                "Bachelors" -> result = result.filter { it.role == "bachelor" }
            }
            if (uiState.searchQuery.isNotBlank()) {
                val q = uiState.searchQuery.trim()
                result = result.filter {
                    it.displayName.contains(q, ignoreCase = true) ||
                        it.email.contains(q, ignoreCase = true)
                }
            }
            return result.sortedBy { it.displayName.lowercase() }
        }

    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun onRoleFilterChanged(filter: String) {
        uiState = uiState.copy(roleFilter = filter)
    }

    fun setSuspended(userId: String, suspended: Boolean) {
        userRepository.setUserSuspended(userId, suspended) { /* listener updates the list */ }
    }

    override fun onCleared() {
        super.onCleared()
        usersListener?.remove()
    }
}
