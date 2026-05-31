package com.example.urbanease.screens.owner.add

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanease.model.Property
import com.example.urbanease.repository.AuthRepository
import com.example.urbanease.repository.PropertyRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PostAdViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    var uiState by mutableStateOf(PostAdUiState())
        private set

    var formState by mutableStateOf(PropertyFormState())
        private set

    var editingProperty by mutableStateOf<Property?>(null)
        private set

    fun updateField(field: PropertyField, value: String) {
        formState = formState.updated(field, value)
    }

    fun validateForm(requireLocation: Boolean = true): Boolean {
        return formState.isValid(requireLocation)
    }

    fun mapFormToAd() {
        uiState = uiState.copy(
            ad = formState.toProperty(uiState.ad)
        )
    }

    fun loadPropertyIntoForm(property: Property) {
        editingProperty = property
        formState = property.toPropertyFormState()
    }

    fun createUpdatedProperty(original: Property): Property {
        return formState.toProperty(original)
    }

    fun loadPropertyForEdit(propertyId: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val property = propertyRepository.getProperty(propertyId)
                if (property == null) {
                    uiState = uiState.copy(errorMessage = "Property not found")
                } else {
                    loadPropertyIntoForm(property)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun updateEditedProperty(
        onSuccess: (Property) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val original = editingProperty ?: return
        if (!validateForm(requireLocation = true)) {
            uiState = uiState.copy(errorMessage = "Please fill in all fields")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val savedProperty = propertyRepository.updateOwnerProperty(createUpdatedProperty(original))
                editingProperty = savedProperty
                formState = savedProperty.toPropertyFormState()
                uiState = uiState.copy(isLoading = false)
                onSuccess(savedProperty)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
                onFailure(e)
            }
        }
    }

    fun addImage(uri: Uri) {
        val currentImages = uiState.selectedImages.toMutableList()
        currentImages.add(uri)
        uiState = uiState.copy(selectedImages = currentImages)
    }

    fun removeImage(uri: Uri) {
        val currentImages = uiState.selectedImages.toMutableList()
        currentImages.remove(uri)
        uiState = uiState.copy(selectedImages = currentImages)
    }

    private fun uploadImages(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {

        if (uiState.selectedImages.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        val imageUrls = mutableListOf<String>()
        var uploadCount = 0
        var hasFailed = false

        uiState.selectedImages.forEach { uri ->
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val ref = FirebaseStorage.getInstance().reference.child(fileName)

            Log.d("FirebaseStorage", "Starting upload: $fileName")

            ref.putFile(uri)
                .addOnSuccessListener { _ ->
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        imageUrls.add(downloadUrl.toString())
                        uploadCount++
                        Log.d("FirebaseStorage", "Successfully uploaded: $fileName")
                        if (uploadCount == uiState.selectedImages.size && !hasFailed) {
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
        val currentUserId = authRepository.currentUserId
        if (currentUserId == null) {
            uiState = uiState.copy(errorMessage = "User not authenticated")
            return
        }

        val userId = currentUserId
        val propertyId = UUID.randomUUID().toString()

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        uploadImages(
            onSuccess = { urls ->
                val adWithDetails = uiState.ad.copy(
                    ownerId = userId,
                    propertyId = propertyId,
                    imageUrls = urls,
                    approvalStatus = Property.APPROVAL_PENDING,
                    propertyStatus = Property.PROPERTY_AVAILABLE,
                    createdAt = System.currentTimeMillis()
                )

                viewModelScope.launch {
                    try {
                        propertyRepository.createProperty(adWithDetails)
                        uiState = uiState.copy(isLoading = false)
                        onSuccess()
                    } catch (e: Exception) {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = "Firestore Error: ${e.message}"
                        )
                        Log.e("Firestore", "Error saving ad", e)
                        onFailure(e)
                    }
                }
            },
            onFailure = { e ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Storage Error: ${e.message}. Check if Firebase Storage is enabled."
                )
                Log.e("FirebaseStorage", "Upload images failed", e)
                onFailure(e)
            }
        )
    }
}
