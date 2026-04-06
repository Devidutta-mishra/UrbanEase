package com.example.urbanease.screens.admin

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.data.PropertyAd
import com.example.urbanease.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor() : ViewModel() {
    val ads = mutableStateOf<List<PropertyAd>>(emptyList())
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
        FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(MUser::class.java)
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
            .addOnFailureListener { e ->
                isLoading.value = false
                Log.e("AdminHomeVM", "Error verifying admin role: ${e.message}")
            }
    }

    private fun fetchAllData() {
        // Listen to all ads
        adsListener?.remove()
        adsListener = FirebaseFirestore.getInstance().collection("ads")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminHomeVM", "Error fetching ads: ${error.message}")
                    isLoading.value = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val fetchedAds = snapshot.documents.mapNotNull { document ->
                        try {
                            val ad = document.toObject(PropertyAd::class.java)
                            ad?.copy(houseId = document.id)
                        } catch (e: Exception) {
                            Log.e("AdminHomeVM", "Error parsing ad: ${document.id}")
                            null
                        }
                    }
                    ads.value = fetchedAds
                }
                isLoading.value = false
            }

        // Listen to all users to filter owners and bachelors
        usersListener?.remove()
        usersListener = FirebaseFirestore.getInstance().collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminHomeVM", "Error fetching users: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val allUsers = snapshot.toObjects(MUser::class.java)
                    owners.value = allUsers.filter { it.role == "owner" }
                    bachelors.value = allUsers.filter { it.role == "bachelor" }
                }
            }
    }

    fun approveAd(houseId: String) {
        if (!isAdmin.value || houseId.isEmpty()) return
        
        val updates = mapOf(
            "isApproved" to true,
            "status" to "approved"
        )
        FirebaseFirestore.getInstance().collection("ads").document(houseId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("AdminHomeVM", "Ad approved successfully: $houseId")
            }
            .addOnFailureListener { e ->
                Log.e("AdminHomeVM", "FAILED to approve ad: ${e.message}")
            }
    }

    fun rejectAd(houseId: String) {
        if (!isAdmin.value || houseId.isEmpty()) return

        val updates = mapOf(
            "isApproved" to false,
            "status" to "rejected"
        )
        FirebaseFirestore.getInstance().collection("ads").document(houseId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("AdminHomeVM", "Ad rejected successfully: $houseId")
            }
            .addOnFailureListener { e ->
                Log.e("AdminHomeVM", "FAILED to reject ad: ${e.message}")
            }
    }

    override fun onCleared() {
        super.onCleared()
        adsListener?.remove()
        usersListener?.remove()
    }
}
