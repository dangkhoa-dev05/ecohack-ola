package com.ecoquest.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ecoquest.app.ui.screens.HomeScreen
import com.ecoquest.app.ui.screens.TaskDetailScreen
import com.ecoquest.app.ui.screens.TaskListScreen

object Routes {
    const val HOME = "home"
    const val TASKS = "tasks"
    const val TASK_DETAIL = "tasks/{taskId}"

    fun taskDetail(taskId: String) = "tasks/$taskId"
}

@Composable
fun EcoQuestNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToTasks = { navController.navigate(Routes.TASKS) }
            )
        }
        composable(Routes.TASKS) {
            TaskListScreen(
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId ->
                    navController.navigate(Routes.taskDetail(taskId))
                }
            )
        }
        composable(Routes.TASK_DETAIL) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
