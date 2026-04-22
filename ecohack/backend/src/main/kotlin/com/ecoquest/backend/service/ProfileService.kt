package com.ecoquest.backend.service

import com.ecoquest.backend.dto.UserDto
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val mockUserStore: MockUserStore
) {

    fun getCurrentProfile(userId: String): UserDto {
        return mockUserStore.findById(userId)
            ?: throw IllegalArgumentException("User $userId not found")
    }
}
