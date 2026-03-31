package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.LoginRequest
import com.ecoquest.backend.dto.LoginResponse
import com.ecoquest.backend.dto.UserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

    private val mockUser = UserDto(
        id = "user_001",
        displayName = "EcoWarrior",
        level = 3,
        credits = 250,
        streak = 5
    )

    @PostMapping("/auth/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        return ApiResponse.success(
            LoginResponse(
                token = "mock-jwt-token-abc123",
                user = mockUser
            )
        )
    }

    @GetMapping("/me")
    fun me(): ApiResponse<UserDto> {
        return ApiResponse.success(mockUser)
    }
}
