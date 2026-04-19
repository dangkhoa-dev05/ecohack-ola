package com.ecoquest.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.data.model.TaskDto
import com.ecoquest.app.ui.theme.EcoGold
import com.ecoquest.app.ui.viewmodel.SubmissionResult
import com.ecoquest.app.ui.viewmodel.TaskViewModel

private const val MOCK_PHOTO_URL =
    "https://ecoquestblob.blob.core.windows.net/task-images/mock-photo.jpg"

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDailyTasks()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error!!)
            viewModel.clearMessage()
        }
    }

    if (uiState.submissionResult != null) {
        SubmissionResultDialog(
            result = uiState.submissionResult!!,
            onDismiss = { viewModel.dismissResult() },
            onTryAgain = {
                val task = uiState.submissionResult!!.task
                viewModel.dismissResult()
                viewModel.openCameraSheet(task)
            }
        )
    }

    if (uiState.cameraSheetTask != null) {
        val task = uiState.cameraSheetTask!!
        AlertDialog(
            onDismissRequest = { viewModel.closeCameraSheet() },
            title = { Text("Add Photo Proof") },
            text = { Text("Choose how to add a photo for \"${task.title}\".") },
            confirmButton = {
                Button(onClick = {
                    viewModel.closeCameraSheet()
                    viewModel.submitTask(task, MOCK_PHOTO_URL)
                }) {
                    Text("Take Photo")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    viewModel.closeCameraSheet()
                    viewModel.submitTask(task, MOCK_PHOTO_URL)
                }) {
                    Text("From Gallery")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = uiState.error ?: "Unknown error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadDailyTasks() }) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Daily Tasks",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(uiState.tasks) { task ->
                        TaskCard(
                            task = task,
                            isSubmitting = uiState.submittingTaskId == task.id,
                            onSubmitWithPhoto = {
                                viewModel.openCameraSheet(task)
                            },
                            onSubmitWithoutPhoto = {
                                viewModel.submitTask(task, null)
                            }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}


@Composable
fun SubmissionResultDialog(
    result: SubmissionResult,
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit
) {
    if (result.isApproved) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Task Completed!") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "+${result.credits} credits earned",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Great job helping the environment!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Done")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Submission Rejected") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Reason: ${result.reason ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Please try again with the required proof.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(onClick = onTryAgain) {
                    Text("Try Again")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskDto,
    isSubmitting: Boolean,
    onSubmitWithPhoto: () -> Unit,
    onSubmitWithoutPhoto: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = { Text(task.category) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = EcoGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "+${task.rewardCredits} credits",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSubmitWithPhoto,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit")
                }

                OutlinedButton(
                    onClick = onSubmitWithoutPhoto,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("No Photo")
                }
            }
        }
    }
}
