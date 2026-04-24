package com.ecoquest.app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.R
import com.ecoquest.app.data.api.RetrofitClient
import com.ecoquest.app.data.model.CompleteSubmissionRequest
import com.ecoquest.app.data.model.InitSubmissionRequest
import com.ecoquest.app.data.repository.SubmittedTasksCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

enum class SubmissionRetryStage { INIT, UPLOAD, COMPLETE }

data class SubmitProofUiState(
    val photoUri: Uri? = null,
    val isLoading: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null,
    val retryStage: SubmissionRetryStage? = null,
    val canRetry: Boolean = false,
    val pendingSubmissionId: String? = null,
    val pendingUploadUrl: String? = null,
    val pendingBlobUrl: String? = null
)

class SubmitProofViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SubmitProofUiState())
    val uiState: StateFlow<SubmitProofUiState> = _uiState.asStateFlow()

    // Dedicated OkHttpClient for direct Azure Blob upload (no JWT, longer timeouts)
    private val uploadClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun t(@StringRes id: Int, vararg args: Any): String =
        getApplication<Application>().getString(id, *args)

    fun setPhotoUri(uri: Uri) {
        _uiState.value = SubmitProofUiState(photoUri = uri)
    }

    fun submit(taskId: String) {
        val photoUri = _uiState.value.photoUri ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, canRetry = false)

            // ── Step 1: POST /submissions/init → get SAS upload URL ──
            val (submissionId, uploadUrl, blobUrl) = try {
                val resp = RetrofitClient.withFallback { api ->
                    api.initSubmission(InitSubmissionRequest(taskId, 10.7769, 106.7009))
                }
                if (!resp.success || resp.data == null) {
                    setError(resp.error ?: t(R.string.error_failed_create_submission), SubmissionRetryStage.INIT)
                    return@launch
                }
                val data = resp.data
                // blobUrl is the permanent URL without the SAS query string
                val blobBaseUrl = data.uploadUrl.substringBefore("?")
                Triple(data.submissionId, data.uploadUrl, blobBaseUrl)
            } catch (e: Exception) {
                setError(e.message ?: t(R.string.error_network), SubmissionRetryStage.INIT)
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                pendingSubmissionId = submissionId,
                pendingUploadUrl = uploadUrl,
                pendingBlobUrl = blobUrl
            )

            // ── Step 2: PUT image bytes directly to Azure Blob via SAS URL ──
            try {
                uploadImageToBlob(photoUri, uploadUrl)
            } catch (e: Exception) {
                setError(e.message ?: t(R.string.error_upload_failed), SubmissionRetryStage.UPLOAD)
                return@launch
            }

            // ── Step 3: POST /submissions/{id}/complete with the permanent blobUrl ──
            try {
                val resp = RetrofitClient.withFallback { api ->
                    api.completeSubmission(submissionId, CompleteSubmissionRequest(imageUrl = blobUrl))
                }
                if (resp.success) {
                    SubmittedTasksCache.markSubmitted(taskId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submitted = true,
                        canRetry = false,
                        retryStage = null
                    )
                } else {
                    setError(resp.error ?: t(R.string.error_submission_failed), SubmissionRetryStage.COMPLETE)
                }
            } catch (e: Exception) {
                setError(e.message ?: t(R.string.error_network), SubmissionRetryStage.COMPLETE)
            }
        }
    }

    private suspend fun uploadImageToBlob(photoUri: Uri, sasUrl: String) = withContext(Dispatchers.IO) {
        val contentResolver = getApplication<Application>().contentResolver
        val bytes = contentResolver.openInputStream(photoUri)?.use { it.readBytes() }
            ?: throw Exception(t(R.string.error_read_photo))

        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
        val request = Request.Builder()
            .url(sasUrl)
            .put(requestBody)
            .header("x-ms-blob-type", "BlockBlob")
            .header("Content-Type", "image/jpeg")
            .build()

        uploadClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Azure upload failed (HTTP ${response.code}): ${response.body?.string()}")
            }
        }
    }

    fun retry(taskId: String) {
        submit(taskId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, retryStage = null, canRetry = false)
    }

    private fun setError(message: String, stage: SubmissionRetryStage) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = message,
            retryStage = stage,
            canRetry = true
        )
    }
}
