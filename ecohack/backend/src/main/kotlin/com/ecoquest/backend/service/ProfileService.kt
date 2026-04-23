package com.ecoquest.backend.service

import com.ecoquest.backend.dto.StatsDto
import com.ecoquest.backend.dto.UserDto
import org.springframework.stereotype.Service

/**
 * Single source of truth for "the currently-authenticated user's live state".
 *
 * Layers live [RewardService] credits + streak on top of the mock baseline
 * stored in [MockUserStore] so every consumer (profile, stats, leaderboard)
 * sees the same values after a submission is approved.
 */
@Service
class ProfileService(
    private val mockUserStore: MockUserStore,
    private val rewardService: RewardService
) {

    fun getCurrentProfile(userId: String): UserDto {
        val base = mockUserStore.findById(userId)
            ?: throw IllegalArgumentException("User $userId not found")

        val grantedCredits = rewardService.getCredits(userId)
        val grantedStreak = rewardService.getStreak(userId)

        return base.copy(
            credits = base.credits + grantedCredits,
            streak = if (grantedStreak > 0) grantedStreak else base.streak,
            totalTasksCompleted = base.totalTasksCompleted + tasksCompletedEstimate(grantedCredits)
        )
    }

    fun getStats(userId: String): StatsDto {
        val profile = getCurrentProfile(userId)
        return StatsDto(
            level = profile.level,
            credits = profile.credits,
            streak = profile.streak,
            tasksCompleted = profile.totalTasksCompleted
        )
    }

    /**
     * Extra tasks completed during this process lifetime, approximated from
     * the credits granted by [RewardService].
     */
    private fun tasksCompletedEstimate(grantedCredits: Int): Int =
        if (grantedCredits <= 0) 0 else (grantedCredits / 50).coerceAtLeast(1)
}
