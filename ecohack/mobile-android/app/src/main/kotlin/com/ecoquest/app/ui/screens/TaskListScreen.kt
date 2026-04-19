package com.ecoquest.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecoquest.app.data.model.TaskDto
import com.ecoquest.app.ui.theme.EcoGold
import com.ecoquest.app.ui.viewmodel.SubmissionResult
import com.ecoquest.app.ui.viewmodel.TaskViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var pendingTask by remember { mutableStateOf<TaskDto?>(null) }
    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val task = pendingTask
        pendingTask = null
        if (success && task != null && cameraUri != null) {
            viewModel.submitTask(task, cameraUri.toString())
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        val task = pendingTask
        pendingTask = null
        if (uri != null && task != null) {
            viewModel.submitTask(task, uri.toString())
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraUri?.let { cameraLauncher.launch(it) }
        } else {
            pendingTask = null
            cameraUri = null
            viewModel.setError("Camera permission denied")
        }
    }

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
                    try {
                        val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        val photoFile = File(picturesDir, "photo_${System.currentTimeMillis()}.jpg")
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        pendingTask = task
                        cameraUri = uri
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    } catch (e: Exception) {
                        viewModel.setError("Could not open camera: ${e.message}")
                    }
                }) {
                    Text("Take Photo")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    viewModel.closeCameraSheet()
                    pendingTask = task
                    galleryLauncher.launch("image/*")
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
                            submissionState = uiState.taskStates[task.id],
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
    submissionState: String?,
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

            when (submissionState) {
                "APPROVED" -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Approved — +${task.rewardCredits} credits earned",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                "REJECTED" -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Rejected — tap Submit to try again",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                            Text("Retry")
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
                else -> {
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
    }
}
