package com.ecoquest.app.data.repository

import com.ecoquest.app.data.model.User

class FakeUserRepository : UserRepository {
    companion object {
        private const val DEMO_EMAIL = "demo@ecoquest.app"
        private const val DEMO_PASSWORD = "pass123"
    }

    override suspend fun getCurrentUser(): User? {
        return UserSessionRepository.getCurrentUser()
    }

    override suspend fun login(
        email: String,
        password: String
    ): User {
        require(email.isNotBlank()) { "Email cannot be blank." }
        require(password.isNotBlank()) { "Password cannot be blank." }
        require(email.trim() == DEMO_EMAIL && password == DEMO_PASSWORD) {
            "Use demo@ecoquest.app / pass123"
        }

        val user = User(
            id = "user_001",
            displayName = "EcoWarrior",
            email = DEMO_EMAIL,
            level = 3,
            xp = 0,
            credits = 250,
            streak = 5,
            avatarUrl = null
        )

        UserSessionRepository.setCurrentUser(user)
        return user
    }

    override suspend fun logout() {
        UserSessionRepository.setCurrentUser(null)
    }

    override suspend fun updateCurrentUser(user: User) {
        UserSessionRepository.setCurrentUser(user)
    }
}
