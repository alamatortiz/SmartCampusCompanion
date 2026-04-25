package com.example.smartcampuscompanion.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartcampuscompanion.data.repository.AnnouncementRepository
import com.example.smartcampuscompanion.data.repository.AuthRepository
import com.example.smartcampuscompanion.data.repository.TaskRepository
import com.example.smartcampuscompanion.screens.*
import com.example.smartcampuscompanion.ui.viewmodel.*

object Routes {
    const val LOGIN = "login"
    const val REGISTRATION = "registration"
    const val DASHBOARD = "dashboard"
    const val CAMPUS_INFO = "campus_info"
    const val TASK_LIST = "task_list"
    const val ANNOUNCEMENTS = "announcements"
    const val SETTINGS = "settings"
    const val RESOURCES = "resources"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context
) {
    val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val isLoggedIn = prefs.getBoolean("logged_in", false)

    val authRepository = remember { AuthRepository() }

    // CRITICAL: Sync role from Firebase when app starts already logged in
    LaunchedEffect(authRepository.currentUser) {
        if (authRepository.currentUser != null) {
            authRepository.getUserProfile().onSuccess { profile ->
                prefs.edit {
                    putString("user_name", profile.name)
                    putString("user_role", profile.role)
                    putString("user_email", profile.email)
                    putString("user_department", profile.department)
                    putString("user_student_id", profile.studentId)
                    putString("user_id", profile.uid)

                }
            }
        }
    }


    // Use userId as key so ViewModel recreates when user changes
    val userId = authRepository.currentUser?.uid ?: "none"
    val taskRepository = remember { TaskRepository() }
    val taskViewModel: TaskViewModel = viewModel(
        key = "task_vm_$userId",
        factory = TaskViewModelFactory(taskRepository, authRepository)
    )

    val announcementRepository = remember { AnnouncementRepository() }
    val announcementViewModel: AnnouncementViewModel = viewModel(
        key = "announcement_vm_$userId",  // ← same pattern
        factory = AnnouncementViewModelFactory(announcementRepository, prefs, context)
    )

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository, prefs)
    )

    val startDestination = if (isLoggedIn && authRepository.currentUser != null) Routes.DASHBOARD else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController, authViewModel, prefs)
        }
        composable(Routes.REGISTRATION) {
            RegistrationScreen(navController, authViewModel)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController, prefs, authViewModel)
        }
        composable(Routes.CAMPUS_INFO) {
            CampusInfoScreen(navController, prefs)
        }
        composable(Routes.TASK_LIST) {
            TaskScreen(viewModel = taskViewModel, navController = navController, prefs = prefs)
        }
        composable(Routes.ANNOUNCEMENTS) {
            AnnouncementScreen(viewModel = announcementViewModel, navController = navController, prefs = prefs)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController, prefs, authViewModel)
        }
        composable(Routes.RESOURCES) {
            ResourcesScreen(navController, prefs)
        }
    }
}