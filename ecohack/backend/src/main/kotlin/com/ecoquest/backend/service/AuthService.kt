package com.ecoquest.backend.service

import com.ecoquest.backend.dto.LoginResponse
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val mockUserStore: MockUserStore
) {

    fun login(email: String, password: String): LoginResponse? {
        val match = mockUserStore.findByCredentials(email, password) ?: return null

        return LoginResponse(
            token = "mock-jwt-token-${match.user.id}",
            user = match.user
        )
    }
}
