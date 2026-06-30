package com.example.urbanease.screens.owner.add.form

import androidx.compose.ui.text.input.KeyboardType

enum class PropertyFormInputType {
    TEXT,
    DROPDOWN,
    DATE
}

data class PropertyFormFieldConfig(
    val field: PropertyField,
    val label: String,
    val placeholder: String = "",
    val inputType: PropertyFormInputType = PropertyFormInputType.TEXT,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val isSingleLine: Boolean = true,
    val isRequired: Boolean = false,
    val options: List<DropdownOption> = emptyList(),
    val rowGroup: String? = null,
    val sectionHeader: String? = null
)

object PropertyFormConfig {
    val locationOptions = listOf(
        DropdownOption("Patia", "Patia"),
        DropdownOption("Khandagiri", "Khandagiri"),
        DropdownOption("Ganga Nagar", "Ganga Nagar"),
        DropdownOption("Siripur", "Siripur"),
        DropdownOption("Jaydev Vihar", "Jaydev Vihar"),
        DropdownOption("DLF Cyber City", "DLF Cyber City"),
        DropdownOption("Chandrasekharpur", "Chandrasekharpur")
    )

    val bhkOptions = listOf(
        DropdownOption("1RK", "1 RK"),
        DropdownOption("1BHK", "1 BHK"),
        DropdownOption("2BHK", "2 BHK"),
        DropdownOption("3BHK", "3 BHK"),
        DropdownOption("4BHK", "4 BHK"),
        DropdownOption("5+BHK", "5+ BHK")
    )

    val bathroomOptions = listOf(
        DropdownOption("1", "1"),
        DropdownOption("2", "2"),
        DropdownOption("3", "3"),
        DropdownOption("4+", "4+")
    )

    val balconyOptions = listOf(
        DropdownOption("None", "None"),
        DropdownOption("1", "1"),
        DropdownOption("2", "2"),
        DropdownOption("3+", "3+")
    )

    val floorOptions = listOf(
        DropdownOption("Ground Floor", "Ground Floor"),
        DropdownOption("1st Floor", "1st Floor"),
        DropdownOption("2nd Floor", "2nd Floor"),
        DropdownOption("3rd Floor", "3rd Floor"),
        DropdownOption("4th Floor", "4th Floor"),
        DropdownOption("5th Floor", "5th Floor"),
        DropdownOption("6th Floor", "6th Floor"),
        DropdownOption("7th Floor", "7th Floor"),
        DropdownOption("8th Floor", "8th Floor"),
        DropdownOption("9th Floor", "9th Floor"),
        DropdownOption("10th+ Floor", "10th+ Floor")
    )

    val totalFloorsOptions = listOf(
        DropdownOption("1", "1"),
        DropdownOption("2", "2"),
        DropdownOption("3", "3"),
        DropdownOption("4", "4"),
        DropdownOption("5", "5"),
        DropdownOption("6", "6"),
        DropdownOption("7", "7"),
        DropdownOption("8", "8"),
        DropdownOption("9", "9"),
        DropdownOption("10+", "10+")
    )

    val furnishingOptions = listOf(
        DropdownOption("Fully Furnished", "Fully Furnished"),
        DropdownOption("Semi Furnished", "Semi Furnished"),
        DropdownOption("Unfurnished", "Unfurnished")
    )

    val propertyTypeOptions = listOf(
        DropdownOption("Apartment", "Apartment"),
        DropdownOption("Independent House", "Independent House"),
        DropdownOption("PG", "PG"),
        DropdownOption("Hostel", "Hostel"),
        DropdownOption("Shared Room", "Shared Room"),
        DropdownOption("Studio Apartment", "Studio Apartment")
    )

    val parkingOptions = listOf(
        DropdownOption("No Parking", "No Parking"),
        DropdownOption("Bike Parking", "Bike Parking"),
        DropdownOption("Car Parking", "Car Parking"),
        DropdownOption("Bike + Car Parking", "Bike + Car Parking")
    )

    val preferredTenantOptions = listOf(
        DropdownOption("Bachelor", "Bachelor"),
        DropdownOption("Family", "Family"),
        DropdownOption("Both", "Both")
    )

    val leaseDurationOptions = listOf(
        DropdownOption("1 Month", "1 Month"),
        DropdownOption("3 Months", "3 Months"),
        DropdownOption("6 Months", "6 Months"),
        DropdownOption("11 Months", "11 Months"),
        DropdownOption("1 Year", "1 Year"),
        DropdownOption("2+ Years", "2+ Years")
    )

    val yesNoOptions = listOf(
        DropdownOption("Yes", "Yes"),
        DropdownOption("No", "No")
    )

    val fields = listOf(
        PropertyFormFieldConfig(
            field = PropertyField.LOCATION,
            label = "Address",
            inputType = PropertyFormInputType.DROPDOWN,
            isRequired = true,
            options = locationOptions,
            sectionHeader = "ADDRESS"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.TITLE,
            label = "Property Title",
            placeholder = "e.g. Cozy 2BHK in Patia",
            isRequired = true,
            sectionHeader = "BASIC INFORMATION"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.PROPERTY_TYPE,
            label = "Property Type",
            inputType = PropertyFormInputType.DROPDOWN,
            isRequired = true,
            options = propertyTypeOptions
        ),
        PropertyFormFieldConfig(
            field = PropertyField.RENT,
            label = "Monthly Rent (₹)",
            placeholder = "e.g. 15000",
            keyboardType = KeyboardType.Number,
            isRequired = true,
            rowGroup = "rent_deposit",
            sectionHeader = "PRICING"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.SECURITY_DEPOSIT,
            label = "Security Deposit (₹)",
            placeholder = "e.g. 30000",
            keyboardType = KeyboardType.Number,
            rowGroup = "rent_deposit"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.MAINTENANCE_CHARGES,
            label = "Maintenance Charges (₹)",
            placeholder = "e.g. 1500",
            keyboardType = KeyboardType.Number
        ),
        PropertyFormFieldConfig(
            field = PropertyField.BHK,
            label = "Bedrooms",
            inputType = PropertyFormInputType.DROPDOWN,
            isRequired = true,
            options = bhkOptions,
            rowGroup = "rooms_bath",
            sectionHeader = "ROOMS & LAYOUT"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.BATHROOMS,
            label = "Bathrooms",
            inputType = PropertyFormInputType.DROPDOWN,
            isRequired = true,
            options = bathroomOptions,
            rowGroup = "rooms_bath"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.BALCONY,
            label = "Balcony",
            inputType = PropertyFormInputType.DROPDOWN,
            options = balconyOptions,
            rowGroup = "balcony_parking"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.PARKING,
            label = "Parking",
            inputType = PropertyFormInputType.DROPDOWN,
            options = parkingOptions,
            rowGroup = "balcony_parking"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.FLOOR_NO,
            label = "Floor Number",
            inputType = PropertyFormInputType.DROPDOWN,
            options = floorOptions,
            rowGroup = "floors"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.TOTAL_FLOORS,
            label = "Total Floors",
            inputType = PropertyFormInputType.DROPDOWN,
            options = totalFloorsOptions,
            rowGroup = "floors"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.FURNISHING,
            label = "Furnishing",
            inputType = PropertyFormInputType.DROPDOWN,
            isRequired = true,
            options = furnishingOptions,
            rowGroup = "furnish_tenant",
            sectionHeader = "FURNISHING & TENANT"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.PREFERRED_TENANT,
            label = "Preferred Tenant",
            inputType = PropertyFormInputType.DROPDOWN,
            options = preferredTenantOptions,
            rowGroup = "furnish_tenant"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.AVAILABLE_FROM,
            label = "Available From",
            inputType = PropertyFormInputType.DATE,
            isRequired = true,
            rowGroup = "avail_lease",
            sectionHeader = "AVAILABILITY"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.LEASE_DURATION,
            label = "Lease Duration",
            inputType = PropertyFormInputType.DROPDOWN,
            options = leaseDurationOptions,
            rowGroup = "avail_lease"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.ELECTRICITY_INCLUDED,
            label = "Electricity Included",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_utilities",
            sectionHeader = "AMENITIES"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.WATER_INCLUDED,
            label = "Water Included",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_utilities"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.PET_FRIENDLY,
            label = "Pet Friendly",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_rules"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.SMOKING_ALLOWED,
            label = "Smoking Allowed",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_rules"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.LIFT_AVAILABLE,
            label = "Lift Available",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_building"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.GATED_SOCIETY,
            label = "Gated Society",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_building"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.POWER_BACKUP,
            label = "Power Backup",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_extras"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.WIFI_AVAILABLE,
            label = "WiFi Available",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions,
            rowGroup = "amenity_extras"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.KITCHEN_AVAILABLE,
            label = "Kitchen Available",
            inputType = PropertyFormInputType.DROPDOWN,
            options = yesNoOptions
        ),
        PropertyFormFieldConfig(
            field = PropertyField.DESCRIPTION,
            label = "Description",
            placeholder = "Describe your property...",
            isSingleLine = false,
            isRequired = true,
            sectionHeader = "DESCRIPTION"
        )
    )
}
