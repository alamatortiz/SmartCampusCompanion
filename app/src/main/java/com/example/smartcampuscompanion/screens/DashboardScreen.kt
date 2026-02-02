package com.example.smartcampuscompanion.screens

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.navigation.Routes

@Composable
fun DashboardScreen(
    navController: NavController,
    prefs: SharedPreferences
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2E003E),
                        Color(0xFF8E2DE2),
                        Color(0xFF2E003E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Welcome, Student!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF6A1B9A)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        navController.navigate(Routes.CAMPUS_INFO)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A5ACD)
                    )
                ) {
                    Text(text = "Campus Information", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        prefs.edit().putBoolean("logged_in", false).apply()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A5ACD)
                    )
                ) {
                    Text(text = "Logout", color = Color.White)
                }
            }
        }
    }
}
