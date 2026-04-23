package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.UserDto
import com.ecoquest.backend.service.ProfileService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping("/me")
    fun me(authentication: Authentication?): ApiResponse<UserDto> {
        val userId = authentication
            ?.name
            ?.takeIf { it.isNotBlank() && it != "anonymousUser" }
            ?: "user_001"

        return ApiResponse.success(profileService.getCurrentProfile(userId))
    }
}
