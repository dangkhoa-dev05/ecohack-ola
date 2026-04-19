package com.ecoquest.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecoquest.app.data.api.RetrofitClient
import com.ecoquest.app.data.model.ChatRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Hi! I'm EcoBot 🌱 Ask me anything about eco-friendly living, recycling, or how to earn more credits!", isUser = false)
    ),
    val isLoading: Boolean = false
)

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.api

    fun sendMessage(text: String) {
        // Add user message
        val updatedMessages = _uiState.value.messages + ChatMessage(text, isUser = true)
        _uiState.value = _uiState.value.copy(messages = updatedMessages, isLoading = true)

        viewModelScope.launch {
            try {
                val response = api.chat(ChatRequest(text))
                if (response.success && response.data != null) {
                    val botReply = ChatMessage(response.data.reply, isUser = false)
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + botReply,
                        isLoading = false
                    )
                } else {
                    val errorMsg = ChatMessage("Sorry, I couldn't process that. Please try again.", isUser = false)
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMsg,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                val errorMsg = ChatMessage("Connection error. Please check your network.", isUser = false)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + errorMsg,
                    isLoading = false
                )
            }
        }
    }
}
