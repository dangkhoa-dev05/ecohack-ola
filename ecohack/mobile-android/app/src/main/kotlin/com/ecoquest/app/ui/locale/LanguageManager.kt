package com.ecoquest.app.ui.locale

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageManager {
    private const val PREFS = "ecoquest_prefs"
    private const val KEY_LANGUAGE = "language_code"
    const val ENGLISH = "en"
    const val VIETNAMESE = "vi"

    fun initialize(context: Context) {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, ENGLISH) ?: ENGLISH
        applyLanguage(context, saved)
    }

    fun getSelectedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, ENGLISH) ?: ENGLISH
    }

    fun applyLanguage(context: Context, languageCode: String) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, languageCode)
            .apply()
    }
}
