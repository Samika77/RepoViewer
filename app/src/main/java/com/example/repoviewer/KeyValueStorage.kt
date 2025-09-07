package com.example.repoviewer

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class KeyValueStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    var authToken: String?
        get() = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
        set(value) = sharedPreferences.edit {
            putString(KEY_AUTH_TOKEN, value)
        }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}