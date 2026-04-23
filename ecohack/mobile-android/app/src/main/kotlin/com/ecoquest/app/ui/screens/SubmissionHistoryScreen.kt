package com.ecoquest.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.data.model.SubmissionSummaryDto
import com.ecoquest.app.ui.components.AnimatedNatureBackdrop
import com.ecoquest.app.ui.components.NatureBackdropStyle
import com.ecoquest.app.ui.viewmodel.SubmissionHistoryViewModel
import kotlin.math.roundToInt

private val taskNames = mapOf(
    "task_001" to "Pick Up Park Litter",
    "task_002" to "Plant a Tree",
    "task_003" to "Sort Recyclable Waste",
    "task_004" to "Beach Cleanup"
)

@Composable
fun SubmissionHistoryScreen(
    viewModel: SubmissionHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var previousScrollMarker by remember { mutableIntStateOf(0) }
    var scrollImpulse by remember { mutableFloatStateOf(0f) }
    val parallaxOffsetPx =
        (listState.firstVisibleItemIndex * 82 + listState.firstVisibleItemScrollOffset) * 0.17f
    val scrollReactiveInfluence = if (listState.isScrollInProgress) scrollImpulse else 0f

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val currentMarker = listState.firstVisibleItemIndex * 10_000 + listState.firstVisibleItemScrollOffset
        val delta = (currentMarker - previousScrollMarker).toFloat()
        previousScrollMarker = currentMarker
        scrollImpulse = (scrollImpulse * 0.72f + delta * 0.070f).coerceIn(-24f, 24f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF4EA))
    ) {
        AnimatedNatureBackdrop(
            modifier = Modifier.offset { IntOffset(0, -parallaxOffsetPx.roundToInt()) },
            style = NatureBackdropStyle.History,
            scrollInfluence = scrollReactiveInfluence
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.error != null && uiState.submissions.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error ?: "Unknown error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadHistory() }) {
                        Text("Retry")
                    }
                }
            }
            uiState.submissions.isEmpty() -> {
                Text(
                    text = "No submissions yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        HistoryHeroCard(
                            total = uiState.submissions.size,
                            approved = uiState.submissions.count { it.status == "APPROVED" },
                            credits = uiState.submissions.filter { it.status == "APPROVED" }.sumOf { it.rewardCredits }
                        )
                    }
                    items(uiState.submissions) { submission ->
                        SubmissionHistoryCard(submission)
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionHistoryCard(submission: SubmissionSummaryDto) {
    val isApproved = submission.status == "APPROVED"
    val taskName = taskNames[submission.taskId] ?: submission.taskId
    val date = submission.createdAt.take(10)
    val statusColor = if (isApproved) Color(0xFF2E7D32) else Color(0xFFC75B39)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isApproved) Color(0xFFF9FFF7) else Color(0xFFFFF7F2)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isApproved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isApproved) "Approved" else "Needs retry",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = taskName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isApproved) "+${submission.rewardCredits}" else "Rejected",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                if (isApproved) {
                    Text(
                        text = "credits",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryHeroCard(
    total: Int,
    approved: Int,
    credits: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFE7F6D5), Color(0xFFD0EEBF), Color(0xFFC1E5B6))
                    )
                )
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Proof timeline",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF386141)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your recent submissions, approvals, and earned impact in one glance.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F3D27)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HistoryMetricPill(text = "$total entries")
                HistoryMetricPill(text = "$approved approved")
                HistoryMetricPill(text = "$credits credits")
            }
        }
    }
}

@Composable
private fun HistoryMetricPill(text: String) {
    Text(
        text = text,
        color = Color(0xFF1F3D27),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.72f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}
