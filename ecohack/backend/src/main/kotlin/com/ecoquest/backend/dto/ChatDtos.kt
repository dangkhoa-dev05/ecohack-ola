package com.ecoquest.backend.dto

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val reply: String
)

data class ExplainRejectionRequest(
    val rejectionReason: String?,
    val rejectionMessage: String? = null,
    val taskId: String? = null
)
