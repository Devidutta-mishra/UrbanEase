package com.example.urbanease.screens.owner.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanease.screens.owner.add.components.AnimatedDropdownField
import com.example.urbanease.screens.owner.add.components.AnimatedListingInputField
import com.example.urbanease.screens.owner.add.form.*

@Composable
fun PropertyFormContent(
    formState: PropertyFormState,
    onFieldChange: (PropertyField, String) -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false
) {
    val fields = PropertyFormConfig.fields
        .filter { isEditMode || it.field != PropertyField.LOCATION }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Text(
                text = "BASIC INFORMATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        fields.groupContiguousFields().forEachIndexed { index, group ->
            item {
                if (group.size == 1) {
                    PropertyFormField(
                        config = group.first(),
                        value = formState.valueFor(group.first().field),
                        onFieldChange = onFieldChange,
                        index = index
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        group.forEach { config ->
                            Box(modifier = Modifier.weight(1f)) {
                                PropertyFormField(
                                    config = config,
                                    value = formState.valueFor(config.field),
                                    onFieldChange = onFieldChange,
                                    index = index
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PropertyFormField(
    config: PropertyFormFieldConfig,
    value: String,
    onFieldChange: (PropertyField, String) -> Unit,
    index: Int
) {
    when (config.inputType) {
        PropertyFormInputType.TEXT -> {
            AnimatedListingInputField(
                label = config.label,
                value = value,
                onValueChange = { onFieldChange(config.field, it) },
                placeholder = config.placeholder,
                keyboardType = config.keyboardType,
                isSingleLine = config.isSingleLine,
                index = index
            )
        }

        PropertyFormInputType.DROPDOWN -> {
            AnimatedDropdownField(
                label = config.label,
                selectedValue = value,
                options = config.options.map { it.label },
                onValueSelected = { onFieldChange(config.field, it) },
                index = index
            )
        }
    }
}

private fun List<PropertyFormFieldConfig>.groupContiguousFields(): List<List<PropertyFormFieldConfig>> {
    val groups = mutableListOf<List<PropertyFormFieldConfig>>()
    var index = 0

    while (index < size) {
        val field = this[index]
        if (field.rowGroup == null) {
            groups.add(listOf(field))
            index++
        } else {
            val groupedFields = drop(index).takeWhile { it.rowGroup == field.rowGroup }
            groups.add(groupedFields)
            index += groupedFields.size
        }
    }

    return groups
}
