package com.ecoquest.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.R
import com.ecoquest.app.data.model.TaskDto
import com.ecoquest.app.ui.components.AnimatedNatureBackdrop
import com.ecoquest.app.ui.components.NatureBackdropStyle
import com.ecoquest.app.ui.viewmodel.TaskViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onBack: (() -> Unit)? = null,
    onTaskClick: (String) -> Unit = {},
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var previousScrollMarker by remember { mutableIntStateOf(0) }
    var scrollImpulse by remember { mutableFloatStateOf(0f) }
    val parallaxOffsetPx =
        (listState.firstVisibleItemIndex * 84 + listState.firstVisibleItemScrollOffset) * 0.18f
    val scrollReactiveInfluence = if (listState.isScrollInProgress) scrollImpulse else 0f

    LaunchedEffect(Unit) { viewModel.loadDailyTasks() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val currentMarker = listState.firstVisibleItemIndex * 10_000 + listState.firstVisibleItemScrollOffset
        val delta = (currentMarker - previousScrollMarker).toFloat()
        previousScrollMarker = currentMarker
        scrollImpulse = (scrollImpulse * 0.70f + delta * 0.075f).coerceIn(-26f, 26f)
    }

    uiState.submissionResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissResult() },
            confirmButton = {
                Button(onClick = { viewModel.dismissResult() }) {
                    Text("OK")
                }
            },
            title = {
                Text(if (result.isApproved) "Great job!" else "Need retry")
            },
            text = {
                Text(
                    if (result.isApproved) {
                        "You earned +${result.credits} credits"
                    } else {
                        result.reason ?: "Submission was rejected"
                    }
                )
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFEAF4EA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.task_list_title),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F3D27)
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = Color(0xFF1F3D27)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEAF4EA)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEAF4EA))
        ) {
            AnimatedNatureBackdrop(
                modifier = Modifier.offset { IntOffset(0, -parallaxOffsetPx.roundToInt()) },
                style = NatureBackdropStyle.Tasks,
                scrollInfluence = scrollReactiveInfluence
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.tasks.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No tasks yet")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadDailyTasks() }) {
                            Text(stringResource(R.string.common_retry))
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            TaskHeroCard(
                                taskCount = uiState.tasks.size,
                                totalCredits = uiState.tasks.sumOf { it.rewardCredits },
                                categoryCount = uiState.tasks.map { it.category }.distinct().size
                            )
                        }
                        items(uiState.tasks) { task ->
                            TaskCard(
                                task = task,
                                isSubmitting = uiState.submittingTaskId == task.id,
                                onClick = { onTaskClick(task.id) },
                                onSubmit = { viewModel.submitTask(task, null) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskDto,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    onSubmit: () -> Unit
) {
    val accent = taskAccentColor(task.category)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFFF8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.category.replaceFirstChar { it.uppercase() },
                    color = accent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(accent.copy(alpha = 0.14f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
                task.distanceKm?.let { distance ->
                    Text(
                        text = String.format("%.1f km away", distance),
                        color = Color(0xFF6C8465),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = accent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF234028)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF627C62)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+${task.rewardCredits} credits",
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.action_submit))
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskHeroCard(
    taskCount: Int,
    totalCredits: Int,
    categoryCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF9FFD8), Color(0xFFE3F2B8), Color(0xFFD5EFA8))
                    )
                )
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Daily mission board",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D842B)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Pick a task and turn small actions into visible eco progress.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF234028)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TaskSummaryPill(text = "$taskCount tasks")
                TaskSummaryPill(text = "$totalCredits credits")
                TaskSummaryPill(text = "$categoryCount zones")
            }
        }
    }
}

@Composable
private fun TaskSummaryPill(text: String) {
    Text(
        text = text,
        color = Color(0xFF234028),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.72f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

private fun taskAccentColor(category: String): Color {
    return when (category.uppercase()) {
        "PLANTING" -> Color(0xFF5C9F45)
        "CLEANUP" -> Color(0xFF3E8E7E)
        "RECYCLING" -> Color(0xFF6B88D8)
        "ENERGY" -> Color(0xFFE08A2E)
        else -> Color(0xFF5D842B)
    }
}
