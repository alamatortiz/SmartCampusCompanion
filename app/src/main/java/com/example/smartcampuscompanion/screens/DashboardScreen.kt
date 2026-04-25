package com.example.smartcampuscompanion.screens

import android.content.SharedPreferences
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.navigation.Routes
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    prefs: SharedPreferences,
    authViewModel: com.example.smartcampuscompanion.ui.viewmodel.AuthViewModel
) {
    val isDark = remember { prefs.getBoolean("is_dark_mode", true) }
    val userRole = remember { prefs.getString("user_role", "student") ?: "student" }
    val isAdmin = userRole == "admin"

    val startGradient by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1A1A2E) else Color(0xFFF0F2F5),
        animationSpec = tween(500), label = "bg"
    )
    val endGradient by animateColorAsState(
        targetValue = if (isDark) Color(0xFF121212) else Color(0xFFFFFFFF),
        animationSpec = tween(500), label = "bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isDark) Color.White else Color(0xFF1A1A1A),
        label = "text"
    )
    val subTextColor = textColor.copy(alpha = 0.6f)
    val studentName = remember { prefs.getString("user_name", "Student") ?: "Student" }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SMART CAMPUS",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                        color = subTextColor
                    )
                },
                actions = {
                    // Settings
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = textColor)
                    }
                    // Logout
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(startGradient, endGradient)))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Welcome back,", style = MaterialTheme.typography.bodyLarge, color = subTextColor)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = studentName,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    if (isAdmin) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF8E2DE2),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "ADMIN",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                QuickStatusCard(isDark)

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "University Services", style = MaterialTheme.typography.titleSmall, color = subTextColor)
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        MenuCard("Announcements", Icons.Default.Campaign, Color(0xFF00BFA5), isDark) {
                            navController.navigate(Routes.ANNOUNCEMENTS)
                        }
                    }
                    item {
                        MenuCard("Tasks", Icons.AutoMirrored.Filled.EventNote, Color(0xFF8E2DE2), isDark) {
                            navController.navigate(Routes.TASK_LIST)
                        }
                    }
                    item {
                        MenuCard("Campus Info", Icons.Default.Map, Color(0xFF6A5ACD), isDark) {
                            navController.navigate(Routes.CAMPUS_INFO)
                        }
                    }
                    item {
                        MenuCard("Resources", Icons.AutoMirrored.Filled.LibraryBooks, Color(0xFFFF7043), isDark) {
                            navController.navigate(Routes.RESOURCES)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatusCard(isDark: Boolean) {
    val surfaceColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
    val textColor = if (isDark) Color.White else Color.Black

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        border = CardDefaults.outlinedCardBorder(enabled = true)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFF8E2DE2).copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF8E2DE2))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Next Class", color = if (isDark) Color.Gray else Color.DarkGray, fontSize = 12.sp)
                Text("Advanced UI Design - 10:30 AM", color = textColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    color: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isDark) 0.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
        }
    }
}