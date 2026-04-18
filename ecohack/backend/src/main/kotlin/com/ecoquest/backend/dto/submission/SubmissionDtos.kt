package com.ecoquest.backend.dto.submission

data class InitSubmissionRequest(
    val taskId: String,
    val latitude: Double?,
    val longitude: Double?
)

data class InitSubmissionResponse(
    val submissionId: String,
    val uploadUrl: String,
    val uploadUrlExpiresAt: String
)

data class CompleteSubmissionRequest(
    val imageUrl: String?
)

data class SubmissionDto(
    val id: String,
    val taskId: String,
    val status: String,
    val rewardCredits: Int,
    val rejectionReason: String?,
    val createdAt: String,
    val updatedAt: String
)

data class SubmissionSummaryDto(
    val id: String,
    val taskId: String,
    val status: String,
    val rewardCredits: Int,
    val createdAt: String
)
