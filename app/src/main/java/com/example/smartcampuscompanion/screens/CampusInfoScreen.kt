package com.example.smartcampuscompanion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartcampuscompanion.data.CampusData

@Composable
fun CampusInfoScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(CampusData.departments) { dept ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(dept.name, style = MaterialTheme.typography.titleMedium)
                    Text(dept.contact)
                }
            }
        }
    }
}
