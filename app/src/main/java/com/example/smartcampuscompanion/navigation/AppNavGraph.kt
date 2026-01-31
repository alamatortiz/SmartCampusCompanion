package com.example.smartcampuscompanion.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartcampuscompanion.screens.*

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val CAMPUS_INFO = "campus_info"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context
) {
    val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val isLoggedIn = prefs.getBoolean("logged_in", false)

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.DASHBOARD else Routes.LOGI
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController, prefs)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController, prefs)
        }
        composable(Routes.CAMPUS_INFO) {
            CampusInfoScreen()
        }
    }
}
