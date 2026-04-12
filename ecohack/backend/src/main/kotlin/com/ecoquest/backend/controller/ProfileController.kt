package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.UserDto
import com.ecoquest.backend.service.ProfileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping("/me")
    fun me(): ApiResponse<UserDto> {
        return ApiResponse.success(profileService.getCurrentProfile())
    }
}
