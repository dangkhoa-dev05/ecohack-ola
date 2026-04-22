package com.ecoquest.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.data.model.SubmissionSummaryDto
import com.ecoquest.app.ui.viewmodel.SubmissionHistoryViewModel

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

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Submission History",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    color = if (isApproved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
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
