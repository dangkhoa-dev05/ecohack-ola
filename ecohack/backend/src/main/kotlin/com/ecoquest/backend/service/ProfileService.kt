package com.ecoquest.backend.service

import com.ecoquest.backend.dto.UserDto
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val mockUserStore: MockUserStore
) {

    fun getCurrentProfile(): UserDto {
        return mockUserStore.getCurrentUser()
    }
}