@file:Suppress("DEPRECATION")

package com.tuempresa.aprendeapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.tuempresa.aprendeapp.data.model.User

class AuthRepository(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    companion object {
        private const val KEY_USERS = "users"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun register(user: User): Boolean {
        val users = getUsers().toMutableList()

        // Verificar si el email ya existe
        if (users.any { it.email == user.email }) {
            return false
        }

        users.add(user)
        saveUsers(users)
        return true
    }

    fun login(email: String, password: String): Boolean {
        val users = getUsers()
        val user = users.find { it.email == email && it.password == password }

        return if (user != null) {
            sharedPreferences.edit().apply {
                putString(KEY_CURRENT_USER, gson.toJson(user))
                putBoolean(KEY_IS_LOGGED_IN, true)
                apply()
            }
            true
        } else {
            false
        }
    }

    fun logout() {
        sharedPreferences.edit().apply {
            remove(KEY_CURRENT_USER)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString(KEY_CURRENT_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    private fun getUsers(): List<User> {
        val usersJson = sharedPreferences.getString(KEY_USERS, "[]")
        return gson.fromJson(usersJson, Array<User>::class.java).toList()
    }

    private fun saveUsers(users: List<User>) {
        val usersJson = gson.toJson(users)
        sharedPreferences.edit().putString(KEY_USERS, usersJson).apply()
    }
}