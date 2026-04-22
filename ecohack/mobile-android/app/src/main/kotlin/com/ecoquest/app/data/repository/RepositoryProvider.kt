package com.ecoquest.app.data.repository

import com.ecoquest.app.BuildConfig

object RepositoryProvider {
    val userRepository: UserRepository by lazy {
        if (BuildConfig.USE_FAKE_USER_REPOSITORY) {
            FakeUserRepository()
        } else {
            NetworkUserRepository()
        }
    }

    val taskRepository: TaskRepository by lazy {
        if (BuildConfig.USE_FAKE_USER_REPOSITORY) {
            FakeTaskRepository()
        } else {
            NetworkTaskRepository()
        }
    }
}
