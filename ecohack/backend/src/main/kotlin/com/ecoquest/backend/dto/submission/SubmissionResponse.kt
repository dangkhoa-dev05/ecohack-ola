package com.ecoquest.backend.dto.submission

data class SubmissionResponse(
    val id: String,
    val taskId: String,
    val status: String,
    val rewardCredits: Int,
    val rejectionReason: String?
)
