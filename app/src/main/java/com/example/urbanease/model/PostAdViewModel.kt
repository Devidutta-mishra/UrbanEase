package com.example.urbanease.model

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanease.data.PropertyAd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.util.UUID

@HiltViewModel
class PostAdViewModel @Inject constructor() : ViewModel() {
    var ad = mutableStateOf(PropertyAd())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    // Store selected image URIs
    val selectedImages = mutableStateListOf<Uri>()

    fun updateLocation(location: String) {
        ad.value = ad.value.copy(location = location)
    }

    fun updateRent(rent: Int) {
        ad.value = ad.value.copy(rent = rent.toLong())
    }

    fun updateTitle(title: String) {
        ad.value = ad.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        ad.value = ad.value.copy(description = description)
    }

    fun updateRooms(rooms: Int) {
        ad.value = ad.value.copy(rooms = rooms)
    }

    fun updateBathrooms(bathrooms: Int) {
        ad.value = ad.value.copy(bathrooms = bathrooms)
    }

    fun updateFloorNo(floorNo: String) {
        ad.value = ad.value.copy(floorNo = floorNo)
    }

    fun updateFurnishing(furnishing: String) {
        ad.value = ad.value.copy(furnishing = furnishing)
    }

    fun addImage(uri: Uri) {
        selectedImages.add(uri)
    }

    fun removeImage(uri: Uri) {
        selectedImages.remove(uri)
    }

    private fun uploadImages(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {

        if (selectedImages.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        val imageUrls = mutableListOf<String>()
        var uploadCount = 0
        var hasFailed = false

        selectedImages.forEach { uri ->
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val ref = FirebaseStorage.getInstance().reference.child(fileName)

            Log.d("FirebaseStorage", "Starting upload: $fileName")

            ref.putFile(uri)
                .addOnSuccessListener { _ ->
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        imageUrls.add(downloadUrl.toString())
                        uploadCount++
                        Log.d("FirebaseStorage", "Successfully uploaded: $fileName")
                        if (uploadCount == selectedImages.size && !hasFailed) {
                            onSuccess(imageUrls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    if (!hasFailed) {
                        hasFailed = true
                        Log.e("FirebaseStorage", "Upload failed for $fileName", e)
                        onFailure(e)
                    }
                }
        }
    }

    fun postAdToFirestore(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            errorMessage.value = "User not authenticated"
            return
        }

        val userId = currentUser.uid
        val houseId = UUID.randomUUID().toString()

        isLoading.value = true
        errorMessage.value = null

        uploadImages(
            onSuccess = { urls ->
                val adWithDetails = ad.value.copy(
                    ownerId = userId,
                    houseId = houseId,
                    imageUrls = urls,
                    isApproved = false,
                    status = "pending",
                    createdAt = System.currentTimeMillis()
                )

                FirebaseFirestore.getInstance().collection("properties")
                    .document(houseId)
                    .set(adWithDetails)
                    .addOnSuccessListener {
                        isLoading.value = false
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        isLoading.value = false
                        errorMessage.value = "Firestore Error: ${e.message}"
                        Log.e("Firestore", "Error saving ad", e)
                        onFailure(e)
                    }
            },
            onFailure = { e ->
                isLoading.value = false
                errorMessage.value =
                    "Storage Error: ${e.message}. Check if Firebase Storage is enabled."
                Log.e("FirebaseStorage", "Upload images failed", e)
                onFailure(e)
            }
        )
    }
}
