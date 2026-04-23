package com.ecoquest.app.data.api

object AuthTokenStore {
    @Volatile
    private var token: String? = null

    fun getToken(): String? = token

    fun setToken(value: String?) {
        token = value
    }
import android.content.Context

object AuthTokenStore {
    private const val PREFS_NAME = "ecoquest_auth"
    private const val KEY_TOKEN = "auth_token"

    @Volatile
    private var applicationContext: Context? = null

    @Volatile
    private var cachedToken: String? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
        cachedToken = prefs().getString(KEY_TOKEN, null)
    }

    fun getToken(): String? {
        val token = cachedToken
        if (token != null) {
            return token
        }

        val persistedToken = prefs().getString(KEY_TOKEN, null)
        cachedToken = persistedToken
        return persistedToken
    }

    fun setToken(token: String?) {
        cachedToken = token
        prefs()
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    private fun prefs() = requireNotNull(applicationContext) {
        "AuthTokenStore must be initialized in Application.onCreate() before use."
    }.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
