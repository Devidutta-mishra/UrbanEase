package com.example.urbanease.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.urbanease.model.House
import com.example.urbanease.repository.HouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val houseRepository: HouseRepository
) : ViewModel() {

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> = _uploadStatus

    fun uploadHouse(house: House) {
        houseRepository.uploadHouse(house) { success ->
            _uploadStatus.value = success
        }
    }

    val houses = MutableLiveData<List<House>>()

    fun loadMyHouses(ownerId: String) {
        houseRepository.getHousesForOwner(ownerId) {
            houses.value = it
        }
    }
}