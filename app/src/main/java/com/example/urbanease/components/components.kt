package com.example.urbanease.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.urbanease.navigation.UrbanScreens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onAction: KeyboardActions? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val finalKeyboardActions = onAction ?: KeyboardActions(onDone = {
        keyboardController?.hide()
    })

    val visualTransformation =
        if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation()

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text(text = labelId) },
                singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF1F2023)),
                enabled = enabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = imeAction
                ),
                visualTransformation = visualTransformation,
                trailingIcon = { PasswordVisibility(passwordVisibility = passwordVisibility) },
                modifier = Modifier.fillMaxWidth(),
                keyboardActions = finalKeyboardActions,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.LightGray,
                    disabledIndicatorColor = Color.LightGray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = {
        passwordVisibility.value = !visible
    }) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "toggle visibility")
    }
}


@Composable
fun emailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    InputField(
        modifier = Modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        isSingleLine = true,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onAction = onAction
    )
}

@Composable
fun InputField(
    modifier: Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            TextField(
                value = valueState.value,
                onValueChange = { valueState.value = it },
                label = { Text(text = labelId) },
                singleLine = isSingleLine,
                textStyle = TextStyle(
                    fontSize = 19.sp,
                    color = Color.Black
                ),
                enabled = enabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardActions = onAction,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    disabledIndicatorColor = Color.LightGray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.Gray,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.Gray
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerAppbar(
    navController: NavController
) {
    Column(modifier = Modifier.background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "UrbanEase",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF1A1C1E)
                    )
                )
                Text(
                    text = "Manage your properties",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Notification */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF42474E)
                    )
                }
                IconButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(UrbanScreens.LoginScreen.name) {
                        popUpTo(0)
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFF42474E)
                    )
                }
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Composable
fun OwnerBottomNavigationBar(
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    currentScreen: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
    ) {
        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter),
            thickness = 0.5.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onHomeClick() }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentScreen == "Home") Color(0xFF006494) else Color(0xFF42474E)
                )
                Text(
                    text = "Home",
                    fontSize = 12.sp,
                    color = if (currentScreen == "Home") Color(0xFF006494) else Color(0xFF42474E)
                )
            }

            Box(
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38B6FF))
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Property",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSettingsClick() }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (currentScreen == "Settings") Color(0xFF006494) else Color(0xFF42474E)
                )
                Text(
                    text = "Settings",
                    fontSize = 12.sp,
                    color = if (currentScreen == "Settings") Color(0xFF006494) else Color(0xFF42474E)
                )
            }
        }
        Text(
            text = "Add Property",
            fontSize = 12.sp,
            color = Color(0xFF42474E),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun InfoForm(
    title: String,
    titleState: MutableState<String>,
    readOnly: Boolean = false
) {
    InputField(
        modifier = Modifier,
        labelId = title,
        enabled = !readOnly,
        isSingleLine = true,
        valueState = titleState
    )
}

@Composable
fun BottomButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .height(60.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = ">",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appbar(
    title: String,
    icon: ImageVector? = null,
    showProfile: Boolean = true,
    navController: NavController,
    logout: Boolean = false,
    topPadding: Dp = 18.dp,
    onBackArrowClicked: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (icon != null) {
                IconButton(onClick = { onBackArrowClicked() }) {
                    Icon(imageVector = icon, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (showProfile) {
                IconButton(onClick = { /* Profile */ }) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(
    onMyAdsClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    myAdsSelected: Boolean = false
) {
    // Kept for backward compatibility if needed, but OwnerBottomNavigationBar is preferred for new design
}
