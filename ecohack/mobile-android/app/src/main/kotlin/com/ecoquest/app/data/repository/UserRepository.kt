package com.ecoquest.app.data.repository

import com.ecoquest.app.data.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?

    suspend fun login(
        email: String,
        password: String
    ): User

    suspend fun logout()

    suspend fun updateCurrentUser(user: User)
}