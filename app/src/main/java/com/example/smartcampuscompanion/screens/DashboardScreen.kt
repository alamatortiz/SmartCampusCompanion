package com.example.smartcampuscompanion.screens

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.navigation.Routes

@Composable
fun DashboardScreen(
    navController: NavController,
    prefs: SharedPreferences
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Welcome, Student!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Routes.CAMPUS_INFO) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Campus Information")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                prefs.edit().clear().apply()
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.DASHBOARD) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
