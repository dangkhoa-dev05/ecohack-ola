package com.ecoquest.backend.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val rewardCredits: Int,
    val latitude: Double,
    val longitude: Double,
    val category: TaskCategory
)

enum class TaskCategory {
    CLEANUP, PLANTING, RECYCLING
}
