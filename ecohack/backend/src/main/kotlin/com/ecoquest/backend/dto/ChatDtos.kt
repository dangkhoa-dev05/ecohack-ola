package com.ecoquest.backend.dto

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val reply: String
)
