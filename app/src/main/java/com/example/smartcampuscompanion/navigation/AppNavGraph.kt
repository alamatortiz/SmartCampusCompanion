package com.example.smartcampuscompanion.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartcampuscompanion.data.local.*
import com.example.smartcampuscompanion.screens.*
import com.example.smartcampuscompanion.ui.viewmodel.AnnouncementViewModel
import com.example.smartcampuscompanion.ui.viewmodel.AnnouncementViewModelFactory
import com.example.smartcampuscompanion.ui.viewmodel.TaskViewModel
import com.example.smartcampuscompanion.ui.viewmodel.TaskViewModelFactory

object Routes {
    const val LOGIN = "login"
    const val REGISTRATION = "registration"
    const val DASHBOARD = "dashboard"
    const val CAMPUS_INFO = "campus_info"
    const val TASK_LIST = "task_list" // New Route
    const val ANNOUNCEMENTS = "announcements" // New Route
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context
) {
    val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val isLoggedIn = prefs.getBoolean("logged_in", false)

    val database = AppDatabase.getDatabase(context)
    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(database.taskDao())
    )
    val announcementViewModel: AnnouncementViewModel = viewModel(
        factory = AnnouncementViewModelFactory(database.announcementDao())
    )

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.DASHBOARD else Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController, userDao = database.userDao(), prefs = prefs)
        }
        composable(Routes.REGISTRATION) {
            // We pass the userDao directly here
            RegistrationScreen(navController = navController, userDao = database.userDao())
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController, prefs)
        }
        composable(Routes.CAMPUS_INFO) {
            CampusInfoScreen(navController, prefs)
        }
        composable(Routes.TASK_LIST) {
            TaskScreen(viewModel = taskViewModel, navController = navController, prefs = prefs)
        }
        composable(Routes.ANNOUNCEMENTS) {
            AnnouncementScreen(viewModel = announcementViewModel, navController, prefs)
        }
    }
}