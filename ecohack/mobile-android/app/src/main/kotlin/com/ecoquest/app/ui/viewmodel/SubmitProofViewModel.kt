package com.ecoquest.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.api.RetrofitClient
import com.ecoquest.app.data.model.CompleteSubmissionRequest
import com.ecoquest.app.data.model.InitSubmissionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SubmissionRetryStage {
    INIT,
    COMPLETE
}

data class SubmitProofUiState(
    val photoUri: Uri? = null,
    val isLoading: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null,
    val retryStage: SubmissionRetryStage? = null,
    val canRetry: Boolean = false,
    val pendingSubmissionId: String? = null
)

class SubmitProofViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SubmitProofUiState())
    val uiState: StateFlow<SubmitProofUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.api

    fun setPhotoUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            photoUri = uri,
            submitted = false,
            error = null,
            retryStage = null,
            canRetry = false,
            pendingSubmissionId = null
        )
    }

    fun submit(taskId: String) {
        val currentPhotoUri = _uiState.value.photoUri ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                retryStage = null,
                canRetry = false
            )
            try {
                val submissionId = _uiState.value.pendingSubmissionId ?: run {
                    val initResp = api.initSubmission(
                        InitSubmissionRequest(
                            taskId = taskId,
                            latitude = 10.7769,
                            longitude = 106.7009
                        )
                    )

                    if (!initResp.success || initResp.data == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = initResp.error ?: "Failed to start upload",
                            retryStage = SubmissionRetryStage.INIT,
                            canRetry = true
                        )
                        return@launch
                    }

                    initResp.data.submissionId
                }

                _uiState.value = _uiState.value.copy(pendingSubmissionId = submissionId)

                val completeResp = api.completeSubmission(
                    submissionId,
                    CompleteSubmissionRequest(
                        imageUrl = currentPhotoUri.toString()
                    )
                )

                if (completeResp.success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submitted = true,
                        canRetry = false,
                        retryStage = null,
                        pendingSubmissionId = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = completeResp.error ?: "Submission failed",
                        retryStage = SubmissionRetryStage.COMPLETE,
                        canRetry = true,
                        pendingSubmissionId = submissionId
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Network error",
                    retryStage = if (_uiState.value.pendingSubmissionId != null) {
                        SubmissionRetryStage.COMPLETE
                    } else {
                        SubmissionRetryStage.INIT
                    },
                    canRetry = true
                )
            }
        }
    }

    fun retry(taskId: String) {
        submit(taskId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            retryStage = null,
            canRetry = false
        )
    }
}
