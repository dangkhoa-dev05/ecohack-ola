package com.ecoquest.backend.dto

data class LeaderboardEntryDto(
    val rank: Int,
    val displayName: String,
    val credits: Int,
    val level: Int
)
