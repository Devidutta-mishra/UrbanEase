package com.example.urbanease.screens.admin

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.House
import com.example.urbanease.model.MUser
import com.example.urbanease.repository.HouseRepository
import com.example.urbanease.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val houseRepository: HouseRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val ads = mutableStateOf<List<House>>(emptyList())
    val owners = mutableStateOf<List<MUser>>(emptyList())
    val bachelors = mutableStateOf<List<MUser>>(emptyList())
    val isLoading = mutableStateOf(false)
    val isAdmin = mutableStateOf(false)

    private var adsListener: ListenerRegistration? = null
    private var usersListener: ListenerRegistration? = null

    init {
        checkAdminRoleAndFetch()
    }

    private fun checkAdminRoleAndFetch() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("AdminHomeVM", "No user logged in")
            return
        }

        isLoading.value = true
        userRepository.getUser(currentUser.uid) { user ->
            if (user?.role == "admin") {
                isAdmin.value = true
                Log.d("AdminHomeVM", "Admin verified. Fetching data...")
                fetchAllData()
            } else {
                isAdmin.value = false
                isLoading.value = false
                Log.e("AdminHomeVM", "Unauthorized: User is not an admin. Role: ${user?.role}")
            }
        }
    }

    private fun fetchAllData() {
        // Listen to all ads
        adsListener?.remove()
        adsListener = houseRepository.listenToAllAds { fetchedAds ->
            ads.value = fetchedAds
            isLoading.value = false
        }

        // Listen to all users to filter owners and bachelors
        usersListener?.remove()
        usersListener = userRepository.listenToAllUsers { allUsers ->
            owners.value = allUsers.filter { it.role == "owner" }
            bachelors.value = allUsers.filter { it.role == "bachelor" }
        }
    }

    override fun onCleared() {
        super.onCleared()
        adsListener?.remove()
        usersListener?.remove()
    }
}
