package com.ecoquest.backend.service

import com.ecoquest.backend.dto.LoginResponse
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val mockUserStore: MockUserStore,
    private val jwtService: JwtService
) {

    fun login(email: String, password: String): LoginResponse? {
        val match = mockUserStore.findByCredentials(email, password) ?: return null

        return LoginResponse(
            token = jwtService.generateToken(match.user.id),
            user = match.user
        )
    }
}
