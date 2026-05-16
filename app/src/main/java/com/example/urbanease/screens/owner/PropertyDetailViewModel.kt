package com.example.urbanease.screens.owner

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.House
import com.example.urbanease.repository.HouseRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: HouseRepository
) : ViewModel() {

    val property = mutableStateOf<House?>(null)
    val isLoading = mutableStateOf(false)
    val isUpdating = mutableStateOf(false)

    fun loadProperty(propertyId: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val doc = FirebaseFirestore.getInstance().collection("properties")
                    .document(propertyId).get().await()
                property.value = doc.toObject(House::class.java)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateProperty(house: House) {
        isUpdating.value = true
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("properties")
                    .document(house.houseId)
                    .set(house).await()
                property.value = house
            } catch (e: Exception) {
                // Handle error
            } finally {
                isUpdating.value = false
            }
        }
    }
}
