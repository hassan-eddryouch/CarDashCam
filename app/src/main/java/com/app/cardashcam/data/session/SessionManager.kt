package com.app.cardashcam.data.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveLogin(email: String) {
        prefs.edit().putString("user", email).apply()
    }

    fun isLogged(): Boolean {
        return prefs.getString("user", null) != null
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun currentUser(): String? {
        return prefs.getString("user", null)
    }
}
