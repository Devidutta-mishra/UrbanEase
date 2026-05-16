package com.example.urbanease.repository

import com.example.urbanease.model.House
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val adsRef = firestore.collection("properties")

    fun listenToAdsForOwner(ownerId: String, onResult: (List<House>) -> Unit): ListenerRegistration {
        return adsRef.whereEqualTo("ownerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val ads = snapshot?.toObjects(House::class.java) ?: emptyList()
                onResult(ads)
            }
    }

    fun listenToAllAds(onResult: (List<House>) -> Unit): ListenerRegistration {
        return adsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            val ads = snapshot?.documents?.mapNotNull { it.toObject(House::class.java)?.copy(houseId = it.id) } ?: emptyList()
            onResult(ads)
        }
    }

    fun listenToApprovedAds(onResult: (List<House>) -> Unit): ListenerRegistration {
        return adsRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val ads = snapshot?.documents?.mapNotNull { it.toObject(House::class.java)?.copy(houseId = it.id) } ?: emptyList()
                onResult(ads)
            }
    }

    // Keep the one-time fetch methods just in case
    fun getAdsForOwner(ownerId: String, onResult: (List<House>) -> Unit) {
        adsRef.whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                val ads = snapshot.toObjects(House::class.java)
                onResult(ads)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getAllAds(onResult: (List<House>) -> Unit) {
        adsRef.get()
            .addOnSuccessListener { snapshot ->
                val ads = snapshot.toObjects(House::class.java)
                onResult(ads)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
