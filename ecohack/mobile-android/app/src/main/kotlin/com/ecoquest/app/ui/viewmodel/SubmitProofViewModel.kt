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

data class SubmitProofUiState(
    val photoUri: Uri? = null,
    val isLoading: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null
)

class SubmitProofViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SubmitProofUiState())
    val uiState: StateFlow<SubmitProofUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.api

    fun setPhotoUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(photoUri = uri, error = null)
    }

    fun submit(taskId: String) {
        if (_uiState.value.photoUri == null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Step 1: Init submission
                val initResp = api.initSubmission(
                    InitSubmissionRequest(
                        taskId = taskId,
                        latitude = 10.7769,
                        longitude = 106.7009
                    )
                )

                if (initResp.success && initResp.data != null) {
                    // Step 2: Complete submission (mock image URL)
                    val completeResp = api.completeSubmission(
                        initResp.data.submissionId,
                        CompleteSubmissionRequest(
                            imageUrl = _uiState.value.photoUri.toString()
                        )
                    )

                    if (completeResp.success) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            submitted = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = completeResp.error ?: "Submission failed"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = initResp.error ?: "Failed to create submission"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Network error"
                )
            }
        }
    }
}
