package com.ecoquest.app.data.repository

import com.ecoquest.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSessionRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun getCurrentUser(): User? = _currentUser.value

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }
}