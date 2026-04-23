package com.ecoquest.app.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ecoquest.app.R
import com.ecoquest.app.ui.theme.EcoDimens
import com.ecoquest.app.ui.theme.EcoForest
import com.ecoquest.app.ui.theme.EcoGreen
import com.ecoquest.app.ui.theme.EcoMint
import com.ecoquest.app.ui.viewmodel.SubmissionRetryStage
import com.ecoquest.app.ui.viewmodel.SubmitProofViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitProofScreen(
    taskId: String,
    taskTitle: String,
    onBack: () -> Unit,
    viewModel: SubmitProofViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.setPhotoUri(photoUri!!)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setPhotoUri(uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "eco_proof_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            photoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color(0xFFEAF4EA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.submit_proof_title),
                        fontWeight = FontWeight.ExtraBold,
                        color = androidx.compose.ui.graphics.Color(0xFF1F3D27)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFEAF4EA),
                    titleContentColor = androidx.compose.ui.graphics.Color(0xFF1F3D27)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(androidx.compose.ui.graphics.Color(0xFFEAF4EA))
                .padding(horizontal = EcoDimens.ScreenHorizontal, vertical = EcoDimens.ScreenVertical),
            verticalArrangement = Arrangement.spacedBy(EcoDimens.SectionGap)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📸", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = taskTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color(0xFF2D4739)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Take or select a photo",
                            style = MaterialTheme.typography.labelMedium,
                            color = androidx.compose.ui.graphics.Color(0xFF7A8C7E)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = if (uiState.photoUri != null) EcoGreen
                        else androidx.compose.ui.graphics.Color(0xFFDDD),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(if (uiState.photoUri != null) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.photoUri != null) {
                    AsyncImage(
                        model = uiState.photoUri,
                        contentDescription = stringResource(R.string.cd_proof_photo),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📷",
                            fontSize = 56.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.prompt_take_or_select_photo),
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color(0xFF7A8C7E),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    border = BorderStroke(1.5f.dp, EcoGreen)
                ) {
                    Text("📷", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.action_camera))
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    border = BorderStroke(1.5f.dp, EcoGreen)
                ) {
                    Text("🖼️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.action_gallery))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFFFEBEE)
                    )
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("❌", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = uiState.error!!,
                            color = androidx.compose.ui.graphics.Color(0xFFC62828),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = when (uiState.retryStage) {
                                SubmissionRetryStage.INIT -> "Could not start the submission."
                                SubmissionRetryStage.COMPLETE -> "Upload finished, but final submission failed."
                                null -> "Submission failed."
                            },
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        if (uiState.canRetry) {
                            OutlinedButton(
                                onClick = { viewModel.retry(taskId) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (uiState.retryStage) {
                                        SubmissionRetryStage.INIT -> "Retry Start"
                                        SubmissionRetryStage.COMPLETE -> "Retry Submit"
                                        null -> "Retry"
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.submitted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✅", fontSize = 48.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Text(
                            text = stringResource(R.string.submission_sent),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreen
                        )
                        Text(
                            text = stringResource(R.string.submission_reviewing),
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color(0xFF7A8C7E),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (uiState.canRetry) {
                        viewModel.retry(taskId)
                    } else {
                        viewModel.submit(taskId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(EcoDimens.LargeActionHeight),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF5D842B)),
                enabled = uiState.photoUri != null && !uiState.isLoading && !uiState.submitted
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.submitted) {
                            stringResource(R.string.submitted_done)
                        } else {
                            stringResource(R.string.action_submit_proof)
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
