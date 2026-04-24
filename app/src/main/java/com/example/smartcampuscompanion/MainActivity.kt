package com.example.smartcampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.navigation.AppNavGraph
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartCampusCompanionTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // We wrap the NavGraph in a Box or Surface to handle the innerPadding
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavGraph(
                            navController = navController,
                            context = this@MainActivity
                        )
                    }
                }
            }
        }
    }
}