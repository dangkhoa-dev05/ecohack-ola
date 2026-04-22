package com.ecoquest.backend.service

import com.ecoquest.backend.dto.UserDto
import org.springframework.stereotype.Component

@Component
class MockUserStore {

    data class MockAuthUser(
        val email: String,
        val password: String,
        val token: String,
        val user: UserDto
    )

    private val users = listOf(
        MockAuthUser(
            email = "demo@ecoquest.app",
            password = "pass123",
            token = "mock-token-demo",
            user = UserDto(
                id = "user_001",
                displayName = "EcoWarrior",
                level = 3,
                credits = 250,
                streak = 5
            )
        ),
        MockAuthUser(
            email = "admin@ecoquest.app",
            password = "admin123",
            token = "mock-token-admin",
            user = UserDto(
                id = "user_002",
                displayName = "EcoAdmin",
                level = 8,
                credits = 920,
                streak = 12
            )
        )
    )

    fun findByCredentials(email: String, password: String): MockAuthUser? {
        return users.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true) &&
                    it.password == password
        }
    }

    fun getCurrentUser(): UserDto {
        return users.first().user
    }
}