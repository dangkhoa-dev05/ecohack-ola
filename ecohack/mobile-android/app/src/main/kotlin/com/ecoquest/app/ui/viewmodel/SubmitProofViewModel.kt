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

class SubmitProofViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SubmitProofUiState())
    val uiState: StateFlow<SubmitProofUiState> = _uiState.asStateFlow()

    private fun t(@StringRes id: Int, vararg args: Any): String {
        return getApplication<Application>().getString(id, *args)
    }

    fun setPhotoUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(photoUri = uri, error = null)
    }

    fun submit(taskId: String) {
        if (_uiState.value.photoUri == null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val (initResp, completeResp) = RetrofitClient.withFallback { api ->
                    val initResponse = api.initSubmission(
                        InitSubmissionRequest(
                            taskId = taskId,
                            latitude = 10.7769,
                            longitude = 106.7009
                        )
                    )

                    val completeResponse = if (initResponse.success && initResponse.data != null) {
                        api.completeSubmission(
                            initResponse.data.submissionId,
                            CompleteSubmissionRequest(
                                imageUrl = _uiState.value.photoUri.toString()
                            )
                        )
                    } else {
                        null
                    }

                    initResponse to completeResponse
                }

                if (initResp.success && initResp.data != null && completeResp != null) {
                    if (completeResp.success) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            submitted = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = completeResp.error ?: t(R.string.error_submission_failed)
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = initResp.error ?: t(R.string.error_failed_create_submission)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: t(R.string.error_network)
                )
            }
        }
    }
}
