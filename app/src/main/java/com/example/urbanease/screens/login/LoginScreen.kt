package com.example.urbanease.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.urbanease.components.PasswordInput
import com.example.urbanease.components.emailInput
import com.example.urbanease.navigation.UrbanScreens

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = viewModel()
) {
    val showLoginForm = rememberSaveable { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "UrbanEase",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.Black
                    )
//                    Text(
//                        text = if (showLoginForm.value) "Sign in to continue" else "Enter Valid Email and Password",
//                        fontWeight = FontWeight.SemiBold,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Black.copy(alpha = 0.25f)
//                    )
                }

                Spacer(modifier = Modifier.height(35.dp))

                if (showLoginForm.value) {
                    UserForm(loading = false, isCreateAccount = false) { email, password, _ ->
                        viewModel.signInWithEmailAndPassword(email, password) { role ->
                            if (role == "owner") {
                                navController.navigate(UrbanScreens.OwnerScreen.name)
                            } else {
                                navController.navigate(UrbanScreens.BachelorScreen.name)
                            }
                        }
                    }
                } else {
                    UserForm(loading = false, isCreateAccount = true) { email, password, role ->
                        viewModel.createUserWithEmailAndPassword(email, password, role) {
                            if (role == "owner") {
                                navController.navigate(UrbanScreens.OwnerScreen.name)
                            } else {
                                navController.navigate(UrbanScreens.BachelorScreen.name)
                            }
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text1 = if (showLoginForm.value) "New User?" else "Existing User?"
                val text2 = if (showLoginForm.value) "Sign up" else "Login"
                Text(
                    text1,
                    modifier = Modifier,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text2, modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Red)
            }
        }
    }
}

//@Preview
@Composable
fun UserForm(
    loading: Boolean,
    isCreateAccount: Boolean,
    onDone: (String, String, String) -> Unit = { email, pwd, role -> }
) {
    val email = rememberSaveable {
        mutableStateOf("")
    }

    val password = rememberSaveable { mutableStateOf("") }

    val selectedRole = rememberSaveable { mutableStateOf("") }

    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    val modifier = Modifier
        .height(280.dp)
        .background(Color.Transparent)
        .verticalScroll(rememberScrollState())


    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (isCreateAccount) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Text("Role: ", modifier = Modifier.padding(end = 8.dp), color = Color.Black)

                listOf("bachelor", "owner").forEach { role ->
                    Text(
                        text = role.replaceFirstChar { it.uppercase() },
                        modifier = Modifier
                            .clickable {
                                selectedRole.value = role
                                println("Selected role: $role")
                            }
                            .padding(horizontal = 8.dp),
                        color = if (selectedRole.value == role) Color.Blue else Color.Gray
                    )
                }
            }
        }

        emailInput(
            emailState = email,
            enabled = !loading,
            labelId = "Enter your email",
            onAction = KeyboardActions { passwordFocusRequest.requestFocus() }
        )

        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                keyboardController?.hide()
                if (!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim(), if (isCreateAccount) selectedRole.value.lowercase() else "")
            }
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            SubmitButton(
                textId = if (isCreateAccount) "SIGN UP" else "LOGIN ->",
                loading = loading,
                validInputs = valid
            ) {
                onDone(email.value.trim(), password.value.trim(), if (isCreateAccount) selectedRole.value.lowercase() else "")
            }
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    loading: Boolean,
    validInputs: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(3.dp)
            .height(55.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
        shape = CircleShape,
        elevation = ButtonDefaults.buttonElevation( // Corrected here
            defaultElevation = 1.dp, // Default elevation
            pressedElevation = 12.dp, // Elevation when button is pressed
            disabledElevation = 12.dp // No elevation when button is disabled
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7D3BDC),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF7D3BDC),
            disabledContentColor = Color.White
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(25.dp))
        } else {
            Text(text = textId, modifier = Modifier.padding(2.dp), fontSize = 16.sp)
        }
    }
}