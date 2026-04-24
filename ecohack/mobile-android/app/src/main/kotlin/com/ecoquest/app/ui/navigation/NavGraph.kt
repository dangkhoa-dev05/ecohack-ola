package com.ecoquest.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecoquest.app.ui.screens.ChatScreen
import com.ecoquest.app.ui.screens.HomeScreen
import com.ecoquest.app.ui.screens.LeaderboardScreen
import com.ecoquest.app.ui.screens.LoginScreen
import com.ecoquest.app.ui.screens.ProfileScreen
import com.ecoquest.app.ui.screens.SubmissionHistoryScreen
import com.ecoquest.app.ui.screens.SubmitProofScreen
import com.ecoquest.app.ui.screens.TaskDetailScreen
import com.ecoquest.app.ui.screens.TaskListScreen
import com.ecoquest.app.ui.viewmodel.AuthViewModel
import com.ecoquest.app.ui.viewmodel.TaskViewModel

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val TASKS = "tasks"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val SUBMIT_PROOF = "submit_proof/{taskId}/{taskTitle}"
    const val LEADERBOARD = "leaderboard"
    const val HISTORY = "history"
    const val CHAT = "chat"
    const val PROFILE = "profile"

    fun taskDetail(taskId: String) = "task_detail/$taskId"
    fun submitProof(taskId: String, taskTitle: String) = "submit_proof/$taskId/${taskTitle.encodeForNav()}"
}

private fun String.encodeForNav() = java.net.URLEncoder.encode(this, "UTF-8")

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Default.Home),
    BottomNavItem(Routes.TASKS, "Tasks", Icons.Default.TaskAlt),
    BottomNavItem(Routes.LEADERBOARD, "Rank", Icons.Default.EmojiEvents),
    BottomNavItem(Routes.HISTORY, "History", Icons.Default.History),
    BottomNavItem(Routes.CHAT, "EcoBot", Icons.Default.Chat),
    BottomNavItem(Routes.PROFILE, "Profile", Icons.Default.Person)
)

@Composable
fun EcoQuestNavGraph() {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Routes.LOGIN

    Scaffold(
        containerColor = Color(0xFFEAF4EA),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFFB8CD7F),
                    contentColor = Color(0xFF1F3C27)
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1D3B24),
                                selectedTextColor = Color(0xFF1D3B24),
                                indicatorColor = Color.White.copy(alpha = 0.7f),
                                unselectedIconColor = Color(0xFF385947),
                                unselectedTextColor = Color(0xFF385947)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LOGIN) {
                val authViewModel: AuthViewModel = viewModel()
                val authUiState by authViewModel.uiState.collectAsState()

                LaunchedEffect(authUiState.isLoggedIn) {
                    if (authUiState.isLoggedIn) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }

                LoginScreen(
                    isLoading = authUiState.isLoading,
                    errorMessage = authUiState.error,
                    onLoginClick = authViewModel::login
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToTasks = {
                        navController.navigate(Routes.TASKS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Routes.TASKS) {
                TaskListScreen(
                    viewModel = taskViewModel,
                    onTaskClick = { taskId ->
                        navController.navigate(Routes.taskDetail(taskId))
                    }
                )
            }
            composable(Routes.TASK_DETAIL) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
                TaskDetailScreen(
                    taskId = taskId,
                    onBack = { navController.popBackStack() },
                    onSubmitProof = { id, title ->
                        navController.navigate(Routes.submitProof(id, title))
                    }
                )
            }
            composable(Routes.SUBMIT_PROOF) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
                val taskTitle = java.net.URLDecoder.decode(
                    backStackEntry.arguments?.getString("taskTitle") ?: "", "UTF-8"
                )
                SubmitProofScreen(
                    taskId = taskId,
                    taskTitle = taskTitle,
                    onBack = { navController.popBackStack() },
                    onSubmitResult = {
                        navController.navigate(Routes.TASKS) {
                            popUpTo(Routes.TASKS) { inclusive = false }
                        }
                    }
                )
            }
            composable(Routes.LEADERBOARD) { LeaderboardScreen() }
            composable(Routes.HISTORY) { SubmissionHistoryScreen() }
            composable(Routes.CHAT) { ChatScreen() }
            composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}
