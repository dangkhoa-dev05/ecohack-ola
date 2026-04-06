package com.ecoquest.backend.dto.Leaderboard

import com.ecoquest.backend.entities.Leaderboard

class LeaderboardResponse (
    val success: Boolean,
    val data: List<Leaderboard>,
    val error: String?
)

