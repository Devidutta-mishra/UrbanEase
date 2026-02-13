package com.example.urbanease.repository

import com.example.urbanease.model.House
import com.google.firebase.firestore.FirebaseFirestore

class HouseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val housesRef = firestore.collection("houses")

    fun uploadHouse(house: House, onResult: (Boolean) -> Unit) {
        val docRef = housesRef.document()
        val houseWithId = house.copy(houseId = docRef.id)

        docRef.set(houseWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getHousesForOwner(ownerId: String, onResult: (List<House>) -> Unit) {
        housesRef.whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                val houses = snapshot.toObjects(House::class.java)
                onResult(houses)
            }
    }

    fun getAllHouses(onResult: (List<House>) -> Unit) {
        housesRef.get()
            .addOnSuccessListener { snapshot ->
                val houses = snapshot.toObjects(House::class.java)
                onResult(houses)
            }
    }
}