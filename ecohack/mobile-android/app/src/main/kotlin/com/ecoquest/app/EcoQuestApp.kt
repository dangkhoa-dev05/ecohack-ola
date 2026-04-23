package com.ecoquest.app

import android.app.Application
import com.ecoquest.app.data.api.AuthTokenStore

class EcoQuestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthTokenStore.initialize(this)
    }
}
