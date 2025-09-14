package com.example.repoviewer.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class KeyValueStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    var authToken: String?
        get() = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
        set(value) = sharedPreferences.edit {
            putString(KEY_AUTH_TOKEN, value)
        }

    fun clearAuthToken() {
        sharedPreferences.edit {
            remove(KEY_AUTH_TOKEN)
        }
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}