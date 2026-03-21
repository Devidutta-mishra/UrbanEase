package com.example.urbanease.repository

import com.example.urbanease.data.PropertyAd
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class HouseRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val adsRef = firestore.collection("ads")

    fun listenToAdsForOwner(ownerId: String, onResult: (List<PropertyAd>) -> Unit): ListenerRegistration {
        return adsRef.whereEqualTo("ownerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val ads = snapshot?.toObjects(PropertyAd::class.java) ?: emptyList()
                onResult(ads)
            }
    }

    fun listenToAllAds(onResult: (List<PropertyAd>) -> Unit): ListenerRegistration {
        return adsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            val ads = snapshot?.toObjects(PropertyAd::class.java) ?: emptyList()
            onResult(ads)
        }
    }

    // Keep the one-time fetch methods just in case
    fun getAdsForOwner(ownerId: String, onResult: (List<PropertyAd>) -> Unit) {
        adsRef.whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                val ads = snapshot.toObjects(PropertyAd::class.java)
                onResult(ads)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getAllAds(onResult: (List<PropertyAd>) -> Unit) {
        adsRef.get()
            .addOnSuccessListener { snapshot ->
                val ads = snapshot.toObjects(PropertyAd::class.java)
                onResult(ads)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
