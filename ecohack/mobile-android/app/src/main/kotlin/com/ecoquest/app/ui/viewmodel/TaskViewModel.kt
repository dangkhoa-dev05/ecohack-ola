package com.ecoquest.app.ui.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.R
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

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private fun t(@StringRes id: Int, vararg args: Any): String {
        return getApplication<Application>().getString(id, *args)
    }

    fun loadDailyTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.withFallback { api -> api.getDailyTasks() }
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        tasks = response.data,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.error ?: t(R.string.error_failed_load_tasks),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: t(R.string.error_network),
                    isLoading = false
                )
            }
        }
    }

    fun submitTask(task: TaskDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(submitMessage = null)
            try {
                val response = RetrofitClient.withFallback { api ->
                    api.initSubmission(
                        InitSubmissionRequest(
                            taskId = task.id,
                            latitude = task.latitude,
                            longitude = task.longitude
                        )
                    )
                }
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        submitMessage = t(
                            R.string.submission_created_format,
                            response.data.submissionId
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: t(R.string.error_submit_failed)
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(submitMessage = null, error = null)
    }
}
