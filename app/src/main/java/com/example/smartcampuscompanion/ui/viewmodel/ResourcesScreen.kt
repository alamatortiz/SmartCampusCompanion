package com.example.smartcampuscompanion.screens

import android.content.SharedPreferences
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(navController: NavController, prefs: SharedPreferences) {
    val isDark = remember { prefs.getBoolean("is_dark_mode", true) }
    val startGradient by animateColorAsState(
        if (isDark) Color(0xFF1A1A2E) else Color(0xFFF0F2F5),
        tween(500), label = ""
    )
    val endGradient by animateColorAsState(
        if (isDark) Color(0xFF121212) else Color(0xFFFFFFFF),
        tween(500), label = ""
    )
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subTextColor = textColor.copy(alpha = 0.6f)

    val resources = listOf(
        ResourceItem("Student Portal", "Access grades, enrollment, and schedules", Icons.Default.School, Color(0xFF8E2DE2)),
        ResourceItem("E-Library", "Digital books and research papers", Icons.Filled.LibraryBooks, Color(0xFF00BFA5)),
        ResourceItem("Campus Map", "Interactive map of university buildings", Icons.Default.Map, Color(0xFFFF7043)),
        ResourceItem("Academic Calendar", "Important dates and holidays", Icons.Default.CalendarMonth, Color(0xFF6A5ACD)),
        ResourceItem("Scholarship Info", "Financial aid and applications", Icons.Default.AttachMoney, Color(0xFFFFB300)),
        ResourceItem("IT Helpdesk", "Technical support and requests", Icons.Default.SupportAgent, Color(0xFF00ACC1))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Study & Campus Resources",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        "Quick access to essential university services",
                        color = subTextColor,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                }

                items(resources) { resource ->
                    ResourceCard(resource, isDark)
                }
            }
        }
    }
}

data class ResourceItem(val title: String, val desc: String, val icon: ImageVector, val color: Color)

@Composable
fun ResourceCard(resource: ResourceItem, isDark: Boolean) {
    val containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(resource.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(resource.icon, contentDescription = null, tint = resource.color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(resource.title, fontWeight = FontWeight.Bold, color = textColor)
                Text(resource.desc, color = textColor.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}