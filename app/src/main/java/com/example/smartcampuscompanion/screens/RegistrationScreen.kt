package com.example.smartcampuscompanion.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.User
import com.example.smartcampuscompanion.data.local.UserDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navController: NavController,
    userDao: UserDao
) {
    // State for input fields
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent // Background is handled by the Box
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF121212))))
                .padding(innerPadding)
                .imePadding() // Essential for keyboard handling
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState), // Allows scrolling when keyboard is up
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Create New Account",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Set up your student profile to get started",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                // --- FORM FIELDS ---

                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8E2DE2),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF8E2DE2), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Program (e.g., BSIT, BSCS)") },
                    leadingIcon = { Icon(Icons.Default.AccountTree, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF8E2DE2), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF8E2DE2), unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8E2DE2),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        when {
                            studentId.isEmpty() || name.isEmpty() || password.isEmpty() -> {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                            password != confirmPassword -> {
                                // THE MATCH CHECK
                                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                isLoading = true
                                scope.launch {
                                    try {
                                        userDao.registerUser(User(studentId, name, password, department))
                                        Toast.makeText(context, "Welcome, $name!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "ID already exists!", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Complete Registration", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel and return to Login", color = Color.White.copy(alpha = 0.5f))
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}