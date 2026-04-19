package com.ecoquest.backend.service

import com.ecoquest.backend.entities.Leaderboard
import com.ecoquest.backend.repositories.Leaderboard.LeaderboardRepo
import org.springframework.stereotype.Service

/**
 * JIRA-412 — Aggregates the top users from in-memory mock data.
 *
 * The underlying repository provides a static snapshot (loaded from the
 * bundled leaderboard.json). This service re-ranks the snapshot by credits
 * descending so rank is always consistent with credits, and exposes:
 *
 *  - [getAllLeaderboard]  : the full aggregated leaderboard
 *  - [getTopLeaderboard]  : the top N entries (default 5)
 */
@Service
class LeaderboardService(
    private val leaderboardRepo: LeaderboardRepo
) {

    fun getAllLeaderboard(): List<Leaderboard> = aggregate()

    fun getTopLeaderboard(limit: Int = DEFAULT_TOP): List<Leaderboard> {
        val safeLimit = limit.coerceAtLeast(1)
        return aggregate().take(safeLimit)
    }

    private fun aggregate(): List<Leaderboard> {
        return leaderboardRepo.findAll()
            .sortedWith(
                compareByDescending<Leaderboard> { it.credits }
                    .thenByDescending { it.level }
                    .thenBy { it.displayName }
            )
            .mapIndexed { index, entry ->
                entry.copy(rank = index + 1)
            }
    }

    companion object {
        private const val DEFAULT_TOP = 5
    }
}
