package com.ecoquest.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecoquest.app.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.data.model.TaskDto
import com.ecoquest.app.ui.theme.EcoDimens
import com.ecoquest.app.ui.theme.EcoGreen
import com.ecoquest.app.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onBack: (() -> Unit)? = null,
    onTaskClick: (String) -> Unit = {},
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDailyTasks()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.submitMessage, uiState.error) {
        val msg = uiState.submitMessage ?: uiState.error
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.task_list_title)) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.tasks.isEmpty() && uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: stringResource(R.string.common_unknown_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadDailyTasks() }) {
                            Text(stringResource(R.string.common_retry))
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.animateContentSize(),
                        contentPadding = PaddingValues(
                            horizontal = EcoDimens.ScreenHorizontal,
                            vertical = EcoDimens.ScreenVertical
                        ),
                        verticalArrangement = Arrangement.spacedBy(EcoDimens.ItemGap + 2.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.task_list_heading),
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.tasks) { task ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(
                                    initialOffsetY = { 100 },
                                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 120f)
                                ),
                                modifier = Modifier.animateItem()
                            ) {
                                TaskCard(
                                    task = task,
                                    onSubmit = { viewModel.submitTask(task) },
                                    onClick = { onTaskClick(task.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskDto,
    onSubmit: () -> Unit,
    onClick: () -> Unit = {}
) {
    val (categoryEmoji, categoryBg) = when (task.category.uppercase()) {
        "CLEANUP"   -> "🧹" to Color(0xFFE3F2FD)
        "PLANTING"  -> "🌱" to Color(0xFFE8F5E9)
        "RECYCLING" -> "♻️" to Color(0xFFE0F7FA)
        "ENERGY"    -> "⚡" to Color(0xFFFFFDE7)
        "WATER"     -> "💧" to Color(0xFFE1F5FE)
        else        -> "🌿" to Color(0xFFF1F8E9)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category emoji badge
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(categoryBg),
                contentAlignment = Alignment.Center
            ) {
                Text(categoryEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D4739)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A8C7E),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Credits badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF8E1)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 13.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.reward_credits_format, task.rewardCredits),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57F17)
                            )
                        }
                    }

                    // Submit button
                    Button(
                        onClick = onSubmit,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
                    ) {
                        Text("📸", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.action_submit), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
