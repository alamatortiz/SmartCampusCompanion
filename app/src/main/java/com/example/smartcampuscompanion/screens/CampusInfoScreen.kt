package com.example.smartcampuscompanion.screens

import android.content.Intent
import android.net.Uri
import android.content.SharedPreferences
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.CampusData
import com.example.smartcampuscompanion.model.Department

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen(
    navController: NavController,
    prefs: SharedPreferences
) {
    val context = LocalContext.current
    val isDark = remember { prefs.getBoolean("is_dark_mode", true) }

    // Theme colors
    val startGradient by animateColorAsState(if (isDark) Color(0xFF1A1A2E) else Color(0xFFF0F2F5), tween(500), label = "")
    val endGradient by animateColorAsState(if (isDark) Color(0xFF121212) else Color(0xFFFFFFFF), tween(500), label = "")
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)

    // Bottom Sheet State
    var selectedDept by remember { mutableStateOf<Department?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Directory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = textColor, navigationIconContentColor = textColor)
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
                    Text("Departments", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = textColor)
                    Text("Tap a department to contact or locate them.", color = textColor.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(CampusData.departments) { department ->
                    DepartmentCard(
                        department = department,
                        isDark = isDark,
                        onCardClick = {
                            selectedDept = department
                            showSheet = true
                        }
                    )
                }
            }

            // --- ACTION HUB BOTTOM SHEET ---
            if (showSheet && selectedDept != null) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray.copy(alpha = 0.5f)) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header info in sheet
                        Text(selectedDept!!.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = textColor)
                        Text(selectedDept!!.contact, color = Color(0xFF8E2DE2), fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ActionCircleButton("Call", Icons.Default.Call, Color(0xFF00BFA5)) {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${selectedDept!!.contact}"))
                                context.startActivity(intent)
                            }
                            ActionCircleButton("Email", Icons.Default.Email, Color(0xFF8E2DE2)) {
                                // Logic for email
                            }
                            ActionCircleButton("Map", Icons.Default.LocationOn, Color(0xFFFF7043)) {
                                // Logic for Google Maps location
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCircleButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = color.copy(alpha = 0.15f))
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DepartmentCard(department: Department, isDark: Boolean, onCardClick: () -> Unit) {
    val containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = department.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(department.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
                Text(department.contact, style = MaterialTheme.typography.bodySmall, color = Color(0xFF8E2DE2))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f))
        }
    }
}