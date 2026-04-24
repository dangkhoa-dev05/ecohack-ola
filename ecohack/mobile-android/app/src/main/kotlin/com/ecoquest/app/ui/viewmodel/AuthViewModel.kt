package com.ecoquest.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.model.User
import com.ecoquest.app.data.repository.RepositoryProvider
import com.ecoquest.app.data.repository.UserSessionRepository
import com.ecoquest.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

data class AuthUiState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(
    private val userRepository: UserRepository = RepositoryProvider.userRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "AuthViewModel initialized with userRepository: ${userRepository.javaClass.simpleName}")
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            try {
                UserSessionRepository.currentUser.collect { user ->
                    Log.d(TAG, "User session changed: ${user?.displayName ?: "null"}")
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isLoggedIn = user != null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error observing current user", e)
            }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                userRepository.getCurrentUser()
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load current user", e)
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                Log.d(TAG, "Attempting login for: $email")
                val user = userRepository.login(email, password)
                Log.d(TAG, "Login successful for: $email")
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isLoading = false,
                    isLoggedIn = true
                )
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
                _uiState.value = AuthUiState()
            } catch (e: Exception) {
                Log.e(TAG, "Logout failed", e)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
