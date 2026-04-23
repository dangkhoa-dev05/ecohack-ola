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
    val submissionResult: SubmissionResult? = null
)

class TaskViewModel : ViewModel() {

    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository
    private val userRepository: UserRepository = RepositoryProvider.userRepository

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
                    isLoading = false,
                    error = e.message ?: "Failed to load tasks"
                )
            }
        }
    }

    fun submitTask(task: TaskDto, imageUrl: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                submittingTaskId = task.id,
                error = null,
                submissionResult = null
            )

            try {
                val result = taskRepository.submitTask(task, imageUrl)
                val approved = result.status == "APPROVED"
                val formattedReason = formatReason(result.rejectionReason)

                if (approved) {
                    TaskHistoryRepository.recordCompletedTask(task, result.rewardCredits)
                    userRepository.getCurrentUser()?.let { user ->
                        userRepository.updateCurrentUser(user.applyTaskReward(result.rewardCredits))
                    }
                }

                _uiState.value = _uiState.value.copy(
                    submittingTaskId = null,
                    submissionResult = SubmissionResult(
                        isApproved = approved,
                        credits = result.rewardCredits,
                        reason = formattedReason,
                        task = task
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    submittingTaskId = null,
                    error = e.message ?: "Submission failed"
                )
            }
        }
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

    private fun formatReason(reason: String?): String? = when (reason) {
        "MISSING_IMAGE" -> "Image is required"
        "MISSING_LOCATION" -> "Location is required"
        "INVALID_LOCATION" -> "Location is invalid"
        "STALE_TIMESTAMP" -> "Photo is too old"
        "VISION_MISMATCH" -> "Photo does not match the task"
        else -> reason
    }
}
