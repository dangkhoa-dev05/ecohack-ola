package com.ecoquest.backend.dto

data class InitSubmissionRequest(
    val taskId: String,
    val latitude: Double,
    val longitude: Double
)

data class InitSubmissionResponse(
    val submissionId: String,
    val uploadUrl: String
)

data class CompleteSubmissionRequest(
    val imageUrl: String
)

data class SubmissionDto(
    val id: String,
    val taskId: String,
    val status: String,
    val rewardCredits: Int
)
