package com.ecoquest.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecoquest.app.ui.theme.EcoGreen

enum class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("tab_home", "Home", Icons.Default.Home),
    TASKS("tab_tasks", "Tasks", Icons.AutoMirrored.Filled.FormatListBulleted),
    LEADERBOARD("tab_leaderboard", "Ranking", Icons.Default.EmojiEvents),
    CHAT("tab_chat", "EcoBot", Icons.Default.SmartToy)
}

@Composable
fun MainScreen(
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToSubmitProof: (String, String) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EcoGreen,
                            selectedTextColor = EcoGreen,
                            indicatorColor = EcoGreen.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.HOME.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomTab.HOME.route) {
                HomeScreen(
                    onNavigateToTasks = {
                        navController.navigate(BottomTab.TASKS.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomTab.TASKS.route) {
                TaskListScreen(
                    onBack = null,
                    onTaskClick = { taskId -> onNavigateToTaskDetail(taskId) }
                )
            }
            composable(BottomTab.LEADERBOARD.route) {
                LeaderboardScreen()
            }
            composable(BottomTab.CHAT.route) {
                ChatScreen()
            }
        }
    }
}
