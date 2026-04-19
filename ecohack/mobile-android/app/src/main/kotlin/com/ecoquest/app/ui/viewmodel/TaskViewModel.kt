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

data class TaskUiState(
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitMessage: String? = null,
    val submittingTaskId: String? = null
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
                submitMessage = null,
                submittingTaskId = task.id
            )
            try {
                val result = taskRepository.submitTask(task, imageUrl)
                if (result.status == "APPROVED") {
                    TaskHistoryRepository.recordCompletedTask(task, result.rewardCredits)
                    userRepository.getCurrentUser()?.let { user ->
                        userRepository.updateCurrentUser(user.applyTaskReward(result.rewardCredits))
                    }
                }
                val message = when (result.status) {
                    "APPROVED" -> "Approved! +${result.rewardCredits} credits earned"
                    "REJECTED" -> "Rejected: ${formatReason(result.rejectionReason)}"
                    else -> "Status: ${result.status}"
                }
                _uiState.value = _uiState.value.copy(
                    tasks = _uiState.value.tasks.filterNot { it.id == task.id },
                    submitMessage = message,
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

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(submitMessage = null, error = null)
    }

    private fun formatReason(reason: String?): String = when (reason) {
        "MISSING_IMAGE" -> "Image is required"
        "MISSING_LOCATION" -> "Location is required"
        else -> reason ?: "Unknown reason"
    }
}
