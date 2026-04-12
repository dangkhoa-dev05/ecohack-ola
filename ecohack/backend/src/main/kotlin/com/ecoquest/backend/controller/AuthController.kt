package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.LoginRequest
import com.ecoquest.backend.dto.LoginResponse
import com.ecoquest.backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        val result = authService.login(request.email, request.password)
            ?: return ApiResponse.error("Invalid email or password")

        return ApiResponse.success(result)
    }
}
