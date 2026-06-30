package com.example.urbanease.screens.owner.add.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanease.ui.animations.AnimationDurations
import com.example.urbanease.ui.animations.AnimationEasings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedDateField(
    label: String,
    value: String,
    onValueSelected: (String) -> Unit,
    index: Int,
    isRequired: Boolean = false
) {
    var showPicker by remember { mutableStateOf(false) }
    val selectedMillis = value.toLongOrNull()

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

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = true },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF7F7F7)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedMillis?.let { formatDate(it) } ?: "Select date",
                        color = if (selectedMillis != null) Color.Black else Color.LightGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }

    if (showPicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onValueSelected(it.toString()) }
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(millis: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
}
