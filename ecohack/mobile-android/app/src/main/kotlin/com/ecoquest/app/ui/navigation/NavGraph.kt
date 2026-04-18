package com.ecoquest.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecoquest.app.ui.screens.ChatScreen
import com.ecoquest.app.ui.screens.LeaderboardScreen
import com.ecoquest.app.ui.screens.LoginScreen
import com.ecoquest.app.ui.screens.TaskListScreen

object Routes {
    const val LOGIN = "login"
    const val TASKS = "tasks"
    const val LEADERBOARD = "leaderboard"
    const val CHAT = "chat"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.TASKS, "Tasks", Icons.Default.TaskAlt),
    BottomNavItem(Routes.LEADERBOARD, "Leaderboard", Icons.Default.EmojiEvents),
    BottomNavItem(Routes.CHAT, "Chat", Icons.Default.Chat)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoQuestNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showAppChrome = currentRoute != Routes.LOGIN

    Scaffold(
        topBar = {
            if (showAppChrome) {
                TopAppBar(
                    title = { Text("EcoQuest") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (showAppChrome) {
                NavigationBar {
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
                            label = { Text(item.label) }
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
                LoginScreen(
                    onLoginClick = { _, _ ->
                        navController.navigate(Routes.TASKS) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(Routes.TASKS) {
                TaskListScreen()
            }
            composable(Routes.LEADERBOARD) {
                LeaderboardScreen()
            }
            composable(Routes.CHAT) {
                ChatScreen()
            }
        }
    }
}
