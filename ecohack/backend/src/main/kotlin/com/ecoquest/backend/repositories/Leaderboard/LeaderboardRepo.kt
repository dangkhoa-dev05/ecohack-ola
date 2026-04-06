package com.ecoquest.backend.repositories.Leaderboard

import com.ecoquest.backend.entities.Leaderboard

interface LeaderboardRepo {
    fun findAll(): List<Leaderboard>
//    fun findById(user: String): Leaderboard?
}