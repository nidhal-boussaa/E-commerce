package tn.rnu.isetr.e_com.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "ecommerce_session",
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        prefs.edit().putString("TOKEN", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("TOKEN", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}