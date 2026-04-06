package com.ecoquest.backend.dto.leaderboard

import com.ecoquest.backend.entities.Leaderboard

data class LeaderboardResponse (
    val success: Boolean,
    val data: List<Leaderboard>,
    val error: String?
)

