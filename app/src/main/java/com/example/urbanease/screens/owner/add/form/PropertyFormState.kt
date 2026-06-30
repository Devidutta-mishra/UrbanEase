package com.example.urbanease.screens.owner.add.form

import com.example.urbanease.model.Property

private const val YES = "Yes"
private const val NO = "No"

data class PropertyFormState(
    val location: String = "",
    val title: String = "",
    val description: String = "",
    val rent: String = "",
    val bhk: String = "",
    val bathrooms: String = "",
    val floorNo: String = "",
    val furnishing: String = "",
    val propertyType: String = "",
    val balcony: String = "",
    val totalFloors: String = "",
    val parking: String = "",
    val preferredTenant: String = "",
    val leaseDuration: String = "",
    val availableFrom: String = "",
    val securityDeposit: String = "",
    val maintenanceCharges: String = "",
    val electricityIncluded: String = "",
    val waterIncluded: String = "",
    val petFriendly: String = "",
    val smokingAllowed: String = "",
    val liftAvailable: String = "",
    val gatedSociety: String = "",
    val powerBackup: String = "",
    val wifiAvailable: String = "",
    val kitchenAvailable: String = ""
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
            PropertyField.PROPERTY_TYPE -> copy(propertyType = value)
            PropertyField.BALCONY -> copy(balcony = value)
            PropertyField.TOTAL_FLOORS -> copy(totalFloors = value)
            PropertyField.PARKING -> copy(parking = value)
            PropertyField.PREFERRED_TENANT -> copy(preferredTenant = value)
            PropertyField.LEASE_DURATION -> copy(leaseDuration = value)
            PropertyField.AVAILABLE_FROM -> copy(availableFrom = value)
            PropertyField.SECURITY_DEPOSIT -> copy(securityDeposit = value)
            PropertyField.MAINTENANCE_CHARGES -> copy(maintenanceCharges = value)
            PropertyField.ELECTRICITY_INCLUDED -> copy(electricityIncluded = value)
            PropertyField.WATER_INCLUDED -> copy(waterIncluded = value)
            PropertyField.PET_FRIENDLY -> copy(petFriendly = value)
            PropertyField.SMOKING_ALLOWED -> copy(smokingAllowed = value)
            PropertyField.LIFT_AVAILABLE -> copy(liftAvailable = value)
            PropertyField.GATED_SOCIETY -> copy(gatedSociety = value)
            PropertyField.POWER_BACKUP -> copy(powerBackup = value)
            PropertyField.WIFI_AVAILABLE -> copy(wifiAvailable = value)
            PropertyField.KITCHEN_AVAILABLE -> copy(kitchenAvailable = value)
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
            PropertyField.PROPERTY_TYPE -> propertyType
            PropertyField.BALCONY -> balcony
            PropertyField.TOTAL_FLOORS -> totalFloors
            PropertyField.PARKING -> parking
            PropertyField.PREFERRED_TENANT -> preferredTenant
            PropertyField.LEASE_DURATION -> leaseDuration
            PropertyField.AVAILABLE_FROM -> availableFrom
            PropertyField.SECURITY_DEPOSIT -> securityDeposit
            PropertyField.MAINTENANCE_CHARGES -> maintenanceCharges
            PropertyField.ELECTRICITY_INCLUDED -> electricityIncluded
            PropertyField.WATER_INCLUDED -> waterIncluded
            PropertyField.PET_FRIENDLY -> petFriendly
            PropertyField.SMOKING_ALLOWED -> smokingAllowed
            PropertyField.LIFT_AVAILABLE -> liftAvailable
            PropertyField.GATED_SOCIETY -> gatedSociety
            PropertyField.POWER_BACKUP -> powerBackup
            PropertyField.WIFI_AVAILABLE -> wifiAvailable
            PropertyField.KITCHEN_AVAILABLE -> kitchenAvailable
        }
    }

    fun isValid(requireLocation: Boolean = true): Boolean {
        return PropertyFormConfig.fields
            .filter { it.isRequired }
            .all { config ->
                if (config.field == PropertyField.LOCATION && !requireLocation) {
                    true
                } else {
                    valueFor(config.field).isNotBlank()
                }
            }
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
            furnishing = furnishing,
            propertyType = propertyType,
            balcony = balcony,
            totalFloors = totalFloors,
            parking = parking,
            preferredTenant = preferredTenant,
            leaseDuration = leaseDuration,
            availableFrom = availableFrom.toLongOrNull() ?: 0L,
            securityDeposit = securityDeposit.toLongOrNull() ?: 0L,
            maintenanceCharges = maintenanceCharges.toLongOrNull() ?: 0L,
            electricityIncluded = electricityIncluded == YES,
            waterIncluded = waterIncluded == YES,
            petFriendly = petFriendly == YES,
            smokingAllowed = smokingAllowed == YES,
            liftAvailable = liftAvailable == YES,
            gatedSociety = gatedSociety == YES,
            powerBackup = powerBackup == YES,
            wifiAvailable = wifiAvailable == YES,
            kitchenAvailable = kitchenAvailable == YES
        )
    }
}

private fun Boolean.toYesNo(): String = if (this) YES else NO

fun Property.toPropertyFormState(): PropertyFormState {
    return PropertyFormState(
        location = location,
        title = title,
        description = description,
        rent = if (rent > 0) rent.toString() else "",
        bhk = if (rooms > 0) "$rooms BHK" else "",
        bathrooms = if (bathrooms > 0) bathrooms.toString() else "",
        floorNo = floorNo,
        furnishing = furnishing,
        propertyType = propertyType,
        balcony = balcony,
        totalFloors = totalFloors,
        parking = parking,
        preferredTenant = preferredTenant,
        leaseDuration = leaseDuration,
        availableFrom = if (availableFrom > 0) availableFrom.toString() else "",
        securityDeposit = if (securityDeposit > 0) securityDeposit.toString() else "",
        maintenanceCharges = if (maintenanceCharges > 0) maintenanceCharges.toString() else "",
        electricityIncluded = electricityIncluded.toYesNo(),
        waterIncluded = waterIncluded.toYesNo(),
        petFriendly = petFriendly.toYesNo(),
        smokingAllowed = smokingAllowed.toYesNo(),
        liftAvailable = liftAvailable.toYesNo(),
        gatedSociety = gatedSociety.toYesNo(),
        powerBackup = powerBackup.toYesNo(),
        wifiAvailable = wifiAvailable.toYesNo(),
        kitchenAvailable = kitchenAvailable.toYesNo()
    )
}
