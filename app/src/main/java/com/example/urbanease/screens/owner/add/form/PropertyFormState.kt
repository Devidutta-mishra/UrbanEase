package com.example.urbanease.screens.owner.add.form

import com.example.urbanease.model.Property

data class PropertyFormState(
    val location: String = "",
    val title: String = "",
    val description: String = "",
    val rent: String = "",
    val bhk: String = "",
    val bathrooms: String = "",
    val floorNo: String = "",
    val furnishing: String = ""
) {
    fun updated(field: PropertyField, value: String): PropertyFormState {
        return when (field) {
            PropertyField.LOCATION -> copy(location = value)
            PropertyField.TITLE -> copy(title = value)
            PropertyField.DESCRIPTION -> copy(description = value)
            PropertyField.RENT -> copy(rent = value)
            PropertyField.BHK -> copy(bhk = value)
            PropertyField.BATHROOMS -> copy(bathrooms = value)
            PropertyField.FLOOR_NO -> copy(floorNo = value)
            PropertyField.FURNISHING -> copy(furnishing = value)
        }
    }

    fun valueFor(field: PropertyField): String {
        return when (field) {
            PropertyField.LOCATION -> location
            PropertyField.TITLE -> title
            PropertyField.DESCRIPTION -> description
            PropertyField.RENT -> rent
            PropertyField.BHK -> bhk
            PropertyField.BATHROOMS -> bathrooms
            PropertyField.FLOOR_NO -> floorNo
            PropertyField.FURNISHING -> furnishing
        }
    }

    fun isValid(requireLocation: Boolean = true): Boolean {
        return (!requireLocation || location.isNotBlank()) &&
                title.isNotBlank() &&
                description.isNotBlank() &&
                rent.isNotBlank() &&
                bhk.isNotBlank() &&
                bathrooms.isNotBlank() &&
                floorNo.isNotBlank() &&
                furnishing.isNotBlank()
    }

    fun toProperty(base: Property): Property {
        return base.copy(
            location = location,
            title = title,
            description = description,
            rent = rent.toLongOrNull() ?: 0L,
            rooms = bhk.filter { it.isDigit() }.toIntOrNull() ?: 1,
            bathrooms = bathrooms.filter { it.isDigit() }.toIntOrNull() ?: 1,
            floorNo = floorNo,
            furnishing = furnishing
        )
    }
}

fun Property.toPropertyFormState(): PropertyFormState {
    return PropertyFormState(
        location = location,
        title = title,
        description = description,
        rent = if (rent > 0) rent.toString() else "",
        bhk = if (rooms > 0) "${rooms} BHK" else "",
        bathrooms = if (bathrooms > 0) bathrooms.toString() else "",
        floorNo = floorNo,
        furnishing = furnishing
    )
}
