package com.ecoquest.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.IntOffset
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
    val slideSpringSpec = spring<IntOffset>(dampingRatio = 0.85f, stiffness = 160f)

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 3 },
                animationSpec = slideSpringSpec
            ) + fadeIn(animationSpec = tween(500)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 160f)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 200f)
            ) + fadeOut(animationSpec = tween(300)) + scaleOut(
                targetScale = 0.97f,
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 200f)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = slideSpringSpec
            ) + fadeIn(animationSpec = tween(500)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 160f)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 3 },
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 200f)
            ) + fadeOut(animationSpec = tween(300)) + scaleOut(
                targetScale = 0.97f,
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 200f)
            )
        }
    ) {
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
