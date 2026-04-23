package com.ecoquest.backend.service

import com.ecoquest.backend.dto.UserDto
import org.springframework.stereotype.Component

@Component
class MockUserStore {

    data class MockAuthUser(
        val email: String,
        val password: String,
        val user: UserDto
    )

    private val users = listOf(
        MockAuthUser(
            email = "demo@ecoquest.app",
            password = "pass123",
            user = UserDto(
                id = "user_001",
                displayName = "EcoWarrior",
                email = "demo@ecoquest.app",
                avatarUrl = null,
                level = 3,
                credits = 250,
                streak = 5,
                totalTasksCompleted = 12,
                joinDate = "2026-03-01"
                totalTasksCompleted = 18,
                joinDate = "2025-01-15"
            )
        ),
        MockAuthUser(
            email = "admin@ecoquest.app",
            password = "admin123",
            user = UserDto(
                id = "user_002",
                displayName = "EcoAdmin",
                email = "admin@ecoquest.app",
                avatarUrl = null,
                level = 8,
                credits = 920,
                streak = 12,
                totalTasksCompleted = 41,
                joinDate = "2025-12-12"
                totalTasksCompleted = 57,
                joinDate = "2024-11-03"
            )
        )
    )

    fun findByCredentials(email: String, password: String): MockAuthUser? {
        return users.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true) &&
                it.password == password
        }
    }

    fun findById(userId: String): UserDto? =
        users.firstOrNull { it.user.id == userId }?.user

    fun allUsers(): List<UserDto> = users.map { it.user }
}
