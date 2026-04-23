package com.ecoquest.app.data.repository

import android.util.Log
import com.ecoquest.app.BuildConfig

private const val TAG = "RepositoryProvider"

object RepositoryProvider {
    val userRepository: UserRepository by lazy {
        try {
            if (BuildConfig.USE_FAKE_USER_REPOSITORY) {
                Log.d(TAG, "Initializing FakeUserRepository (USE_FAKE_USER_REPOSITORY=true)")
                FakeUserRepository()
            } else {
                Log.d(TAG, "Initializing NetworkUserRepository (USE_FAKE_USER_REPOSITORY=false)")
                NetworkUserRepository()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize userRepository, falling back to FakeUserRepository", e)
            FakeUserRepository()
        }
    }

    val taskRepository: TaskRepository by lazy {
        try {
            if (BuildConfig.USE_FAKE_USER_REPOSITORY) {
                Log.d(TAG, "Initializing FakeTaskRepository (USE_FAKE_USER_REPOSITORY=true)")
                FakeTaskRepository()
            } else {
                Log.d(TAG, "Initializing NetworkTaskRepository (USE_FAKE_USER_REPOSITORY=false)")
                NetworkTaskRepository()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize taskRepository, falling back to FakeTaskRepository", e)
            FakeTaskRepository()
        }
    }
}
