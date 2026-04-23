package com.ecoquest.app.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecoquest.app.R
import com.ecoquest.app.ui.theme.EcoGreen

enum class BottomTab(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    HOME("tab_home", R.string.tab_home, Icons.Default.Home),
    TASKS("tab_tasks", R.string.tab_tasks, Icons.AutoMirrored.Filled.FormatListBulleted),
    LEADERBOARD("tab_leaderboard", R.string.tab_ranking, Icons.Default.EmojiEvents),
    CHAT("tab_chat", R.string.tab_ecobot, Icons.Default.SmartToy),
    SETTINGS("tab_settings", R.string.tab_settings, Icons.Default.Settings)
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
            NavigationBar(
                tonalElevation = 10.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
            ) {
                BottomTab.entries.forEach { tab ->
                    val tabLabel = stringResource(tab.labelRes)
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
                                contentDescription = tabLabel,
                                modifier = Modifier.size(if (currentRoute == tab.route) 24.dp else 22.dp)
                            )
                        },
                        label = { Text(tabLabel) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EcoGreen,
                            selectedTextColor = EcoGreen,
                            indicatorColor = EcoGreen.copy(alpha = 0.16f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        val tabSlideSpec = spring<IntOffset>(dampingRatio = 0.8f, stiffness = 180f)
        NavHost(
            navController = navController,
            startDestination = BottomTab.HOME.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth / 5 },
                    animationSpec = tabSlideSpec
                ) + fadeIn(animationSpec = tween(400)) + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 180f)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 5 },
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 200f)
                ) + fadeOut(animationSpec = tween(250)) + scaleOut(
                    targetScale = 0.95f,
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 200f)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 5 },
                    animationSpec = tabSlideSpec
                ) + fadeIn(animationSpec = tween(400)) + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 180f)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth / 5 },
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 200f)
                ) + fadeOut(animationSpec = tween(250)) + scaleOut(
                    targetScale = 0.95f,
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 200f)
                )
            },
            modifier = Modifier
                .padding(padding)
                .animateContentSize()
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
            composable(BottomTab.SETTINGS.route) {
                SettingsScreen()
            }
        }
    }
}
