package com.example.smartcampuscompanion

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.navigation.AppNavGraph
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartCampusCompanionTheme {
                val navController = rememberNavController()
                AppNavGraph(navController, thi)
            }
        }
    }
}
