package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.StatsDto
import com.ecoquest.backend.service.MockUserStore
import com.ecoquest.backend.service.RewardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * JIRA-411 — exposes the mock reward state (credits + streak) via /me/stats.
 * The values are layered on top of the default MockUserStore profile so the
 * endpoint reflects rewards granted during the current process lifetime.
 */
@RestController
class RewardController(
    private val rewardService: RewardService,
    private val mockUserStore: MockUserStore
) {

    @GetMapping("/me/stats")
    fun getStats(): ApiResponse<StatsDto> {
        val base = mockUserStore.getCurrentUser()
        val grantedCredits = rewardService.getCredits(base.id)
        val grantedStreak = rewardService.getStreak(base.id)

        return ApiResponse.success(
            StatsDto(
                level = base.level,
                credits = base.credits + grantedCredits,
                streak = if (grantedStreak > 0) grantedStreak else base.streak,
                tasksCompleted = (grantedCredits / 50).coerceAtLeast(0)
            )
        )
    }
}
