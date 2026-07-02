package com.example.urbanease.screens.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.urbanease.R
import com.example.urbanease.ui.theme.BrandGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onImageSelected(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    val avatarModel = uiState.selectedImageUri
                        ?: uiState.avatarUrl.ifEmpty { null }
                    Image(
                        painter = if (avatarModel != null)
                            rememberAsyncImagePainter(avatarModel)
                        else painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { imagePicker.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { imagePicker.launch("image/*") },
                        shape = CircleShape,
                        color = BrandGreen,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.padding(7.dp)
                        )
                    }
                }

                if (uiState.role.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = BrandGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = uiState.role.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            color = BrandGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.displayName,
                            onValueChange = viewModel::onDisplayNameChange,
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandGreen,
                                focusedLabelColor = BrandGreen,
                                cursorColor = BrandGreen
                            )
                        )
                        OutlinedTextField(
                            value = uiState.phoneNumber,
                            onValueChange = viewModel::onPhoneChange,
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandGreen,
                                focusedLabelColor = BrandGreen,
                                cursorColor = BrandGreen
                            )
                        )
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = {},
                            label = { Text("Email (cannot be changed)") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(uiState.error, color = Color(0xFFC62828), fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.save {
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !uiState.isSaving,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = Color.White
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
