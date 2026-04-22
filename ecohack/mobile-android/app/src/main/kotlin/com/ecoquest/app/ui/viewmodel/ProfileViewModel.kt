package com.ecoquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.model.User
import com.ecoquest.app.data.repository.CompletedTask
import com.ecoquest.app.data.repository.RepositoryProvider
import com.ecoquest.app.data.repository.TaskHistoryRepository
import com.ecoquest.app.data.repository.UserSessionRepository
import com.ecoquest.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val recentTasks: List<CompletedTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository = RepositoryProvider.userRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
        observeRecentTasks()
        loadProfile()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            UserSessionRepository.currentUser.collect { user ->
                _uiState.update { currentState ->
                    currentState.copy(
                        user = user,
                        error = if (!currentState.isLoading && user == null) {
                            "No user is currently logged in."
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }

    private fun observeRecentTasks() {
        viewModelScope.launch {
            TaskHistoryRepository.completedTasks.collect { tasks ->
                _uiState.update { currentState ->
                    currentState.copy(recentTasks = tasks)
                }
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val user = userRepository.getCurrentUser()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = if (user == null) "No user is currently logged in." else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                userRepository.updateCurrentUser(user)
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update profile"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
