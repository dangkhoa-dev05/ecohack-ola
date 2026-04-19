package com.ecoquest.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ecoquest.app.ui.screens.LoginScreen
import com.ecoquest.app.ui.screens.MainScreen
import com.ecoquest.app.ui.screens.SubmitProofScreen
import com.ecoquest.app.ui.screens.TaskDetailScreen

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val TASK_DETAIL = "tasks/{taskId}"
    const val SUBMIT_PROOF = "submit/{taskId}/{taskTitle}"

    fun taskDetail(taskId: String) = "tasks/$taskId"
    fun submitProof(taskId: String, taskTitle: String) =
        "submit/$taskId/${java.net.URLEncoder.encode(taskTitle, "UTF-8")}"
}

@Composable
fun EcoQuestNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            MainScreen(
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Routes.taskDetail(taskId))
                },
                onNavigateToSubmitProof = { taskId, title ->
                    navController.navigate(Routes.submitProof(taskId, title))
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
            val taskTitle = backStackEntry.arguments?.getString("taskTitle")?.let {
                java.net.URLDecoder.decode(it, "UTF-8")
            } ?: "Task"
            SubmitProofScreen(
                taskId = taskId,
                taskTitle = taskTitle,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
