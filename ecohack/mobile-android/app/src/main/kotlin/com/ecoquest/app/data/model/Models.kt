package com.ecoquest.app.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?
)

data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val rewardCredits: Int,
    val latitude: Double,
    val longitude: Double,
    val category: String
)

data class UserDto(
    val id: String,
    val displayName: String,
    val level: Int,
    val credits: Int,
    val streak: Int
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class InitSubmissionRequest(
    val taskId: String,
    val latitude: Double,
    val longitude: Double
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
    val rejectionReason: String? = null,
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

data class StatsDto(
    val level: Int,
    val credits: Int,
    val streak: Int,
    val tasksCompleted: Int
)

data class LeaderboardEntryDto(
    val rank: Int,
    val displayName: String,
    val credits: Int,
    val level: Int
)

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val reply: String
)
