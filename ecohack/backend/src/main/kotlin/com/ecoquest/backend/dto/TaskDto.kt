package com.ecoquest.backend.dto

data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val rewardCredits: Int,
    val latitude: Double,
    val longitude: Double,
    val category: String
)
