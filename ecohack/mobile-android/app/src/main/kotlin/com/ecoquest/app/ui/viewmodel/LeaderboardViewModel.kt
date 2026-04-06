package com.ecoquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.api.RetrofitClient
import com.ecoquest.app.data.model.LeaderboardEntryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val entries: List<LeaderboardEntryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LeaderboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.api

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getLeaderboard()
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        entries = response.data.sortedBy { it.rank },
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.error ?: "Failed to load leaderboard",
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
}
