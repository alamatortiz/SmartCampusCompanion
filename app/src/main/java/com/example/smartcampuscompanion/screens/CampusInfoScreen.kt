package com.example.smartcampuscompanion.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampuscompanion.data.CampusData
import com.example.smartcampuscompanion.model.Department

@Composable
fun CampusInfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2E003E), // dark purple
                        Color(0xFF8E2DE2), // bright purple
                        Color(0xFF2E003E)  // dark purple
                    )
                )
            )
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Campus Departments",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White, // visible on purple background
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusData.departments.forEach { department ->
            DepartmentCard(department)
        }
    }
}