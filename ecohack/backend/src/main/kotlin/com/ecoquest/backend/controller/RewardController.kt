package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.StatsDto
import com.ecoquest.backend.service.ProfileService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RewardController(
    private val profileService: ProfileService
) {

    @GetMapping("/me/stats")
    fun getStats(): ApiResponse<StatsDto> {
        val base = mockUserStore.findById("user_001")
            ?: return ApiResponse.error("User not found")
    fun getStats(authentication: Authentication): ApiResponse<StatsDto> {
        val base = mockUserStore.findById(authentication.name)
            ?: throw IllegalArgumentException("User ${authentication.name} not found")
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
        return ApiResponse.success(profileService.getStats(authentication.name))
    }
}
