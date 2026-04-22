package com.ecoquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.model.TaskDto
import com.ecoquest.app.data.model.applyTaskReward
import com.ecoquest.app.data.repository.RepositoryProvider
import com.ecoquest.app.data.repository.TaskHistoryRepository
import com.ecoquest.app.data.repository.TaskRepository
import com.ecoquest.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubmissionResult(
    val isApproved: Boolean,
    val credits: Int,
    val reason: String?,
    val task: TaskDto
)

data class TaskUiState(
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val submittingTaskId: String? = null,
    val cameraSheetTask: TaskDto? = null,
    val submissionResult: SubmissionResult? = null,
    val taskStates: Map<String, String> = emptyMap()
)

class TaskViewModel(
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val userRepository: UserRepository = RepositoryProvider.userRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun loadDailyTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                _uiState.value = _uiState.value.copy(
                    tasks = taskRepository.getDailyTasks(),
                    isLoading = false
                )
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
                submittingTaskId = task.id,
                submissionResult = null
            )
            try {
                val result = taskRepository.submitTask(task, imageUrl)
                if (result.status == "APPROVED") {
                    TaskHistoryRepository.recordCompletedTask(task, result.rewardCredits)
                    userRepository.getCurrentUser()?.let { user ->
                        userRepository.updateCurrentUser(user.applyTaskReward(result.rewardCredits))
                    }
                }
                val newTaskStates = _uiState.value.taskStates + (task.id to result.status)
                _uiState.value = _uiState.value.copy(
                    submissionResult = SubmissionResult(
                        isApproved = result.status == "APPROVED",
                        credits = result.rewardCredits,
                        reason = formatReason(result.rejectionReason),
                        task = task
                    ),
                    taskStates = newTaskStates,
                    submittingTaskId = null
                )
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

    fun dismissResult() {
        _uiState.value = _uiState.value.copy(submissionResult = null)
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun formatReason(reason: String?): String = when (reason) {
        "MISSING_IMAGE" -> "Image is required"
        "MISSING_LOCATION" -> "Location is required"
        else -> reason ?: "Unknown reason"
    }
}
