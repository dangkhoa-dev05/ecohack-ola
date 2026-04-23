package com.ecoquest.app.data.api

object AuthTokenStore {
    @Volatile
    private var token: String? = null

    fun getToken(): String? = token

    fun setToken(value: String?) {
        token = value
    }
}
