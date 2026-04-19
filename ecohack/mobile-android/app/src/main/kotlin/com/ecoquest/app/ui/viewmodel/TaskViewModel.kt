package com.ecoquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.api.RetrofitClient
import com.ecoquest.app.data.model.CompleteSubmissionRequest
import com.ecoquest.app.data.model.InitSubmissionRequest
import com.ecoquest.app.data.model.TaskDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskUiState(
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitMessage: String? = null,
    val submittingTaskId: String? = null,
    val cameraSheetTask: TaskDto? = null
)

class TaskViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.api

    fun loadDailyTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getDailyTasks()
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        tasks = response.data,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.error ?: "Failed to load tasks",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Network error",
                    isLoading = false
                )
            }
        }
    }

    fun submitTask(task: TaskDto, imageUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                submitMessage = null,
                submittingTaskId = task.id
            )
            try {
                val initResponse = api.initSubmission(
                    InitSubmissionRequest(
                        taskId = task.id,
                        latitude = task.latitude,
                        longitude = task.longitude
                    )
                )
                if (!initResponse.success || initResponse.data == null) {
                    _uiState.value = _uiState.value.copy(
                        error = initResponse.error ?: "Failed to init submission",
                        submittingTaskId = null
                    )
                    return@launch
                }

                val submissionId = initResponse.data.submissionId

                val completeResponse = api.completeSubmission(
                    id = submissionId,
                    request = CompleteSubmissionRequest(imageUrl = imageUrl)
                )

                if (completeResponse.success && completeResponse.data != null) {
                    val result = completeResponse.data
                    val message = when (result.status) {
                        "APPROVED" -> "Approved! +${result.rewardCredits} credits earned"
                        "REJECTED" -> "Rejected: ${formatReason(result.rejectionReason)}"
                        else -> "Status: ${result.status}"
                    }
                    _uiState.value = _uiState.value.copy(
                        submitMessage = message,
                        submittingTaskId = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = completeResponse.error ?: "Submission failed",
                        submittingTaskId = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Network error",
                    submittingTaskId = null
                )
            }
        }
    }

    fun openCameraSheet(task: TaskDto) {
        _uiState.value = _uiState.value.copy(cameraSheetTask = task)
    }

    fun closeCameraSheet() {
        _uiState.value = _uiState.value.copy(cameraSheetTask = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(submitMessage = null, error = null)
    }

    private fun formatReason(reason: String?): String = when (reason) {
        "MISSING_IMAGE" -> "Image is required"
        "MISSING_LOCATION" -> "Location is required"
        else -> reason ?: "Unknown reason"
    }
}
