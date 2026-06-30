package com.example.urbanease.screens.owner.add.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanease.ui.animations.AnimationDurations
import com.example.urbanease.ui.animations.AnimationEasings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit,
    index: Int,
    isRequired: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight / 4 },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                delayMillis = index * 50,
                easing = AnimationEasings.DEFAULT
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                delayMillis = index * 50,
                easing = AnimationEasings.DEFAULT
            )
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            FieldLabel(label = label, isRequired = isRequired)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedValue,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select option", color = Color.LightGray) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F0F0),
                        unfocusedContainerColor = Color(0xFFF7F7F7),
                        disabledContainerColor = Color(0xFFF7F7F7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = {
                                Text(selectionOption, color = Color.Black)
                            },
                            onClick = {
                                onValueSelected(selectionOption)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}
