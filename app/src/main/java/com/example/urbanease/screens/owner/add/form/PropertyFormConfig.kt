package com.example.urbanease.screens.owner.add.form

import androidx.compose.ui.text.input.KeyboardType

enum class PropertyFormInputType {
    TEXT,
    DROPDOWN
}

data class PropertyFormFieldConfig(
    val field: PropertyField,
    val label: String,
    val placeholder: String = "",
    val inputType: PropertyFormInputType = PropertyFormInputType.TEXT,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val isSingleLine: Boolean = true,
    val options: List<DropdownOption> = emptyList(),
    val rowGroup: String? = null
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
        DropdownOption("4", "4"),
        DropdownOption("5+", "5+")
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

    val furnishingOptions = listOf(
        DropdownOption("Fully Furnished", "Fully Furnished"),
        DropdownOption("Semi Furnished", "Semi Furnished"),
        DropdownOption("Unfurnished", "Unfurnished")
    )

    val fields = listOf(
        PropertyFormFieldConfig(
            field = PropertyField.LOCATION,
            label = "Location",
            inputType = PropertyFormInputType.DROPDOWN,
            options = locationOptions
        ),
        PropertyFormFieldConfig(
            field = PropertyField.TITLE,
            label = "Property Title",
            placeholder = "e.g. Cozy 2BHK in Patia"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.RENT,
            label = "Monthly Rent (₹)",
            placeholder = "e.g. 15000",
            keyboardType = KeyboardType.Number
        ),
        PropertyFormFieldConfig(
            field = PropertyField.BHK,
            label = "BHK",
            inputType = PropertyFormInputType.DROPDOWN,
            options = bhkOptions,
            rowGroup = "rooms_bath"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.BATHROOMS,
            label = "Bathrooms",
            inputType = PropertyFormInputType.DROPDOWN,
            options = bathroomOptions,
            rowGroup = "rooms_bath"
        ),
        PropertyFormFieldConfig(
            field = PropertyField.FLOOR_NO,
            label = "Floor Number",
            inputType = PropertyFormInputType.DROPDOWN,
            options = floorOptions
        ),
        PropertyFormFieldConfig(
            field = PropertyField.FURNISHING,
            label = "Furnishing",
            inputType = PropertyFormInputType.DROPDOWN,
            options = furnishingOptions
        ),
        PropertyFormFieldConfig(
            field = PropertyField.DESCRIPTION,
            label = "Description",
            placeholder = "Describe your property...",
            isSingleLine = false
        )
    )
}
