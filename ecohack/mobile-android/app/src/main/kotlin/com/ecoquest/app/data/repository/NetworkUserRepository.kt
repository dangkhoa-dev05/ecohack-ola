package com.ecoquest.app.data.repository

import com.ecoquest.app.data.api.AuthTokenStore
import com.ecoquest.app.data.api.RetrofitClient
import com.ecoquest.app.data.model.LoginRequest
import com.ecoquest.app.data.model.User
import com.ecoquest.app.data.model.toUser

class NetworkUserRepository : UserRepository {
    companion object {
        private var authToken: String? = null
    }

    private val api = RetrofitClient.api

    override suspend fun getCurrentUser(): User? {
        UserSessionRepository.getCurrentUser()?.let { return it }

        return try {
            val response = api.getMe()
            if (response.success && response.data != null) {
                val user = response.data.toUser()
                UserSessionRepository.setCurrentUser(user)
                user
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun login(email: String, password: String): User {
        require(email.isNotBlank()) { "Email cannot be blank." }
        require(password.isNotBlank()) { "Password cannot be blank." }

        val response = api.login(
            LoginRequest(
                email = email.trim(),
                password = password
            )
        )

        if (!response.success || response.data == null) {
            throw IllegalStateException(response.error ?: "Login failed")
        }

        authToken = response.data.token
        AuthTokenStore.setToken(authToken)
        val user = response.data.user.toUser(
            email = email.trim()
        )
        UserSessionRepository.setCurrentUser(user)
        return user
    }

    override suspend fun logout() {
        UserSessionRepository.setCurrentUser(null)
        authToken = null
        AuthTokenStore.setToken(null)
    }

    override suspend fun updateCurrentUser(user: User) {
        UserSessionRepository.setCurrentUser(user)
    }
}
