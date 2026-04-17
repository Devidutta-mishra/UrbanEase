package com.example.urbanease.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.urbanease.R
import com.example.urbanease.navigation.UrbanScreens

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = viewModel()
) {
    val showLoginForm = rememberSaveable { mutableStateOf(true) }
    val loading = viewModel.loading.value
    val error = viewModel.error.value

    if (showLoginForm.value) {
        LoginContent(
            viewModel = viewModel,
            loading = loading,
            error = error,
            onSignIn = { email, password ->
                viewModel.signInWithEmailAndPassword(email, password) { role ->
                    when (role) {
                        "owner" -> navController.navigate(UrbanScreens.OwnerScreen.name)
                        "admin" -> navController.navigate(UrbanScreens.AdminScreen.name)
                        else -> navController.navigate(UrbanScreens.BachelorScreen.name)
                    }
                }
            },
            onNavigateToSignup = { showLoginForm.value = false }
        )
    } else {
        CreateAccountContent(
            viewModel = viewModel,
            loading = loading,
            error = error,
            onBack = { showLoginForm.value = true },
            onSignUp = { email, password, role, name, phone ->
                viewModel.createUserWithEmailAndPassword(email, password, role, name, phone) {
                    when (role) {
                        "owner" -> navController.navigate(UrbanScreens.OwnerScreen.name)
                        "admin" -> navController.navigate(UrbanScreens.AdminScreen.name)
                        else -> navController.navigate(UrbanScreens.BachelorScreen.name)
                    }
                }
            },
            onNavigateToLogin = { showLoginForm.value = true }
        )
    }
}

@Composable
fun LoginContent(
    viewModel: LoginScreenViewModel,
    loading: Boolean,
    error: String?,
    onSignIn: (String, String) -> Unit,
    onNavigateToSignup: () -> Unit
) {

    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val email = viewModel.email.value
    val password = viewModel.password.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F1F2), Color.White)
                )
            )
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF245D69)
            )
            Text(
                text = "UrbanEase",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF245D69)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Welcome Back",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Access your curated urban sanctuary.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Email Field
        CustomInputField(
            label = "EMAIL ADDRESS",
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "name@example.com",
            icon = Icons.Default.Email,
            error = viewModel.emailError.value
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Password Field
        CustomInputField(
            label = "PASSWORD",
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholder = "********",
            icon = Icons.Default.Lock,
            isPassword = true,
            passwordVisible = passwordVisible.value,
            onPasswordToggle = { passwordVisible.value = !passwordVisible.value },
            error = viewModel.passwordError.value
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (error != null) {
            Text(
                text = "Incorrect email or password. Please try again.",
                color = Color.Red,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { onSignIn(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF245D69)),
            shape = RoundedCornerShape(12.dp),
            enabled = !loading && viewModel.emailError.value == null && viewModel.passwordError.value == null && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row {
            Text(text = "Don't have an account? ", color = Color.Gray)
            Text(
                text = "Create Account",
                color = Color(0xFF245D69),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToSignup() }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "© 2024 URBANEASE PROPERTY SOLUTIONS",
            fontSize = 10.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun CreateAccountContent(
    viewModel: LoginScreenViewModel,
    loading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onSignUp: (String, String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val nameState = rememberSaveable { mutableStateOf("") }
    val phoneState = rememberSaveable { mutableStateOf("") }
    val selectedRole = rememberSaveable { mutableStateOf("bachelor") }
    val termsAccepted = rememberSaveable { mutableStateOf(false) }
    val email = viewModel.email.value
    val password = viewModel.password.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF245D69)
                )
            }

            Text(
                text = "UrbanEase",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF245D69)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Join the community of modern urban living.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SELECT YOUR ROLE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF245D69)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Bachelor", "Admin", "Owner").forEach { role ->
                val isSelected = selectedRole.value.equals(role, ignoreCase = true)
                Surface(
                    onClick = { selectedRole.value = role.lowercase() },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF245D69) else Color(0xFFF2F2F2),
                    modifier = Modifier.height(40.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = role,
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomInputField(
            label = "FULL NAME",
            value = nameState.value,
            onValueChange = { nameState.value = it },
            placeholder = "Enter your full name",
            icon = Icons.Default.Person
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomInputField(
            label = "EMAIL ADDRESS",
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "name@example.com",
            icon = Icons.Default.Email,
            error = viewModel.emailError.value
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomInputField(
            label = "PHONE NUMBER",
            value = phoneState.value,
            onValueChange = { phoneState.value = it },
            placeholder = "+91",
            icon = Icons.Default.Phone
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomInputField(
            label = "PASSWORD",
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholder = "********",
            icon = Icons.Default.Lock,
            isPassword = true,
            error = viewModel.passwordError.value
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = termsAccepted.value,
                onCheckedChange = { termsAccepted.value = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF245D69))
            )
            Text(
                text = "By creating an account, you agree to our Terms of Service and Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (error != null) {
            Text(text = error, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { onSignUp(email, password, selectedRole.value, nameState.value, phoneState.value) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF245D69)),
            shape = RoundedCornerShape(12.dp),
            enabled = !loading &&
                    termsAccepted.value &&
                    viewModel.emailError.value == null &&
                    viewModel.passwordError.value == null &&
                    email.isNotBlank() &&
                    password.isNotBlank()
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Already have an account? ", color = Color.Gray)
            Text(
                text = "Sign In",
                color = Color(0xFF245D69),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    trailingText: String? = null,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF245D69),
                    modifier = Modifier.clickable { /* Handle forgot password */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            isError = error != null,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.LightGray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.LightGray) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onPasswordToggle) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Close else Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF245D69),
                unfocusedBorderColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

    }
}

@Composable
fun SocialButton(text: String, icon: Int, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { /* Handle Social Login */ },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
