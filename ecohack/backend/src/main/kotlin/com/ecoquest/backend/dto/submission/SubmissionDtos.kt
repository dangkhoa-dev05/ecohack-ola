package com.ecoquest.backend.dto.submission

data class InitSubmissionRequest(
    val taskId: String,
    val latitude: Double?,
    val longitude: Double?
)

data class InitSubmissionResponse(
    val submissionId: String,
    val uploadUrl: String
)

data class CompleteSubmissionRequest(
    val imageUrl: String?,
    /** ISO-8601 timestamp of when the photo was captured. Optional. */
    val capturedAt: String? = null
)

data class SubmissionDto(
    val id: String,
    val taskId: String,
    val status: String,
    val rewardCredits: Int,
    val streak: Int? = null,
    val rejectionReason: String? = null
)
