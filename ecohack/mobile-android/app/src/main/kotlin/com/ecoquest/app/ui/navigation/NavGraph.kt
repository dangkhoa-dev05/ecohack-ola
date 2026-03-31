package com.ecoquest.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ecoquest.app.ui.screens.TaskListScreen

object Routes {
    const val TASKS = "tasks"
}

@Composable
fun EcoQuestNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.TASKS) {
        composable(Routes.TASKS) {
            TaskListScreen()
        }
    }
}
