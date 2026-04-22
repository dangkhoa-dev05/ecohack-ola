package com.ecoquest.backend.dto

data class UserDto(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val level: Int,
    val credits: Int,
    val streak: Int,
    val totalTasksCompleted: Int,
    val joinDate: String
)
