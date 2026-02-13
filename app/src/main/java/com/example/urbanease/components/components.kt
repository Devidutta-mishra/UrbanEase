package com.example.urbanease.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
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
import com.example.urbanease.utils.Colors
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


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
//                .border(
////                    0.dp,
////                    Color.LightGray,
//                    shape = RoundedCornerShape(8.dp)
//                ), // Add light gray border
//            elevation = CardDefaults.cardElevation(12.dp), // Apply only elevation here
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
        Icons.Default.Close
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
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp), // Moved padding here
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
                    color = Color.Black // Set text color directly here
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
fun Appbar(
    title: String,
    icon: ImageVector? = null,
    showProfile: Boolean = true,
    navController: NavController,
    logout: Boolean = false,
    topPadding: Dp = 18.dp,
    onBackArrowClicked: () -> Unit = {}
) {
    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty()) {
        email.split("@")[0]
            .uppercase(Locale.ROOT)
    } else {
        "User"
    }
    Column(modifier = Modifier) {
        TopAppBar(
            modifier = Modifier
                .height(70.dp)
                .offset(y = (-20).dp),
            title = {
                Text(
                    text = title,
                    color = Colors.TextPrimary,
                    style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 25.sp),
                    modifier = Modifier.padding(top = 1.dp)
                )
            },
            navigationIcon = {
                if (icon != null) {
                    IconButton(onClick = { onBackArrowClicked.invoke() }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            },
            actions = {
                if (showProfile) {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Accountbox",
                            modifier = Modifier.size(35.dp),
                            tint = Color.Black.copy(alpha = 0.75f)
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Profile") },
                                onClick = {
                                    expanded = false
                                    navController.navigate(UrbanScreens.DetailScreen.name)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    expanded = false
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(UrbanScreens.LoginScreen.name)
                                }
                            )
                        }
                    }
                } else if (logout) {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut().run {
                            navController.navigate(UrbanScreens.LoginScreen.name)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "logout"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        //        HorizontalDivider(modifier = Modifier, thickness = 0.5.dp, color = Color.LightGray)
    }
}


@Composable
fun BottomNavigationBar(
    onMyAdsClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    myAdsSelected: Boolean = false,
    addSelected: Boolean = false,
    settingsSelected: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        BottomAppBar(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            tonalElevation = 4.dp
        ) {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Add",
                    tint = if (addSelected) Color.Blue else Color.Black,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }

            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .border(
                            2.dp,
                            if (addSelected) Colors.Primary else Colors.TextSecondary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (addSelected) Color.Blue else Colors.TextPrimary,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Add",
                    tint = if (addSelected) Color.Blue else Colors.TextPrimary,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun BottomButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .height(100.dp)
    ) {
        BottomAppBar(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            tonalElevation = 4.dp
        ) {
            TextButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = Color.White)
                    .border(
                        width = 2.dp,
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Text(
                    text = ">",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
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