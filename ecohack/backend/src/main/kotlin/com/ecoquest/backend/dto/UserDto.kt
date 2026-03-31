package com.ecoquest.backend.dto

data class UserDto(
    val id: String,
    val displayName: String,
    val level: Int,
    val credits: Int,
    val streak: Int
)
