package com.ecoquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.api.RetrofitClient
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
    val submitMessage: String? = null
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

    fun submitTask(task: TaskDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(submitMessage = null)
            try {
                val response = api.initSubmission(
                    InitSubmissionRequest(
                        taskId = task.id,
                        latitude = task.latitude,
                        longitude = task.longitude
                    )
                )
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        submitMessage = "Submission created: ${response.data.submissionId}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Submit failed"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(submitMessage = null, error = null)
    }
}
