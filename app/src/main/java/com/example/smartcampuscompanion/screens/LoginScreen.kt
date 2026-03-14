package com.example.smartcampuscompanion.screens

import android.content.SharedPreferences
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import com.example.smartcampuscompanion.data.local.UserDao
import com.example.smartcampuscompanion.navigation.Routes
import kotlinx.coroutines.launch
import androidx.core.content.edit

@Composable
fun LoginScreen(
    navController: NavController,
    userDao: UserDao,
    prefs: SharedPreferences
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Gradient matching the Dashboard theme
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF121212))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .imePadding(), // Adjusts for keyboard
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HERO SECTION ---
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color(0xFF8E2DE2).copy(alpha = 0.15f)
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = Color(0xFF8E2DE2),
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                "Sign in to your student account",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- INPUT FIELDS ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; isError = false },
                label = { Text("Student ID") },
                placeholder = { Text("e.g. 2024-10001") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                isError = isError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8E2DE2),
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; isError = false },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                isError = isError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8E2DE2),
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )

            // Error Message
            AnimatedVisibility(visible = isError) {
                Text(
                    "Invalid Student ID or Password",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- ACTION BUTTON ---
            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        val user = userDao.login(username, password)
                        if (user != null) {
                            prefs.edit {
                                putString("user_name", user.name)
                                putBoolean("logged_in", true)
                            }
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        } else {
                            isError = true
                            isLoading = false
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
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- FOOTER ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("New student?", color = Color.White.copy(alpha = 0.6f))
                TextButton(onClick = { navController.navigate(Routes.REGISTRATION) }) {
                    Text("Create Account", color = Color(0xFF8E2DE2), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}