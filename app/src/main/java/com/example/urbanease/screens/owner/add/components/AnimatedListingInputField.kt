package com.example.urbanease.screens.owner.add.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanease.ui.animations.AnimationDurations
import com.example.urbanease.ui.animations.AnimationEasings

@Composable
fun FieldLabel(label: String, isRequired: Boolean) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        if (isRequired) {
            Text(
                text = " *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isSingleLine: Boolean = true,
    isRequired: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        FieldLabel(label = label, isRequired = isRequired)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = isSingleLine,
            maxLines = if (isSingleLine) 1 else 5,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(),
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
    }
}

@Composable
fun AnimatedListingInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isSingleLine: Boolean = true,
    isRequired: Boolean = false,
    index: Int = 0
) {
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
        ListingInputField(
            label = label,
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            keyboardType = keyboardType,
            imeAction = imeAction,
            isSingleLine = isSingleLine,
            isRequired = isRequired
        )
    }
}
