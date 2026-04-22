package com.ecoquest.backend.dto.leaderboard

data class LeaderboardDTO(
    val userId: String,
    val username: String,
    val score: Int,
    val rank: Int,
    val avatarUrl: String?
)