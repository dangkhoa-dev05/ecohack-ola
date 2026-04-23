package com.ecoquest.app.ui.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.R
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

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private fun t(@StringRes id: Int, vararg args: Any): String {
        return getApplication<Application>().getString(id, *args)
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.withFallback { api -> api.getLeaderboard() }
                if (response.success && response.data != null) {
                    _uiState.value = _uiState.value.copy(
                        entries = response.data,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.error ?: t(R.string.error_failed_load_leaderboard),
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
}
