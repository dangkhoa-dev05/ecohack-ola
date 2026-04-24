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
    fun getStats(authentication: Authentication): ApiResponse<StatsDto> {
        return ApiResponse.success(profileService.getStats(authentication.name))
    }
}
