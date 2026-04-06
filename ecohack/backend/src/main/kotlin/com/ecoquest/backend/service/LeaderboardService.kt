package com.ecoquest.backend.service

import com.ecoquest.backend.entities.Leaderboard
import com.ecoquest.backend.repositories.Leaderboard.LeaderboardRepo
import org.springframework.stereotype.Service

@Service
class LeaderboardService ( private val leaderboardRepo: LeaderboardRepo ) {
    fun getAllLeaderboard(): List<Leaderboard>
    {
        return leaderboardRepo.findAll().sortedBy { it.rank }
    }

    fun getTopLeaderboard(limit: Int) : List<Leaderboard> {
        return leaderboardRepo.findAll().sortedBy { it.rank }.take(limit.coerceAtLeast(1))
    }

    //FIND BY RANK

//    fun getLeaderboardByRank(rank: Int): Leaderboard? {
//        return leaderboardRepo.fin
//    }
}