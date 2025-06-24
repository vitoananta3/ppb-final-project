package com.example.gasnugas.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gasnugas.database.AppDatabase
import com.example.gasnugas.database.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    
    // DataStore extension
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")
    
    // Keys for DataStore
    private val USER_ID_KEY = longPreferencesKey("user_id")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    
    // Get current user ID from DataStore
    val currentUserId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }
    
    // Get current user info from DataStore
    val currentUser: Flow<UserSession?> = context.dataStore.data.map { preferences ->
        val userId = preferences[USER_ID_KEY]
        val email = preferences[USER_EMAIL_KEY]
        val name = preferences[USER_NAME_KEY]
        
        if (userId != null && email != null && name != null) {
            UserSession(userId, email, name)
        } else {
            null
        }
    }
    
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val user = userDao.login(email, password)
            if (user != null) {
                saveUserSession(user)
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Invalid email or password")
            }
        } catch (e: Exception) {
            AuthResult.Error("Login failed: ${e.message}")
        }
    }
    
    suspend fun register(email: String, password: String, name: String): AuthResult {
        return try {
            // Check if email already exists
            if (userDao.isEmailExists(email) > 0) {
                return AuthResult.Error("Email already exists")
            }
            
            val user = User(
                email = email,
                password = password, // In production, hash this
                name = name
            )
            
            val userId = userDao.insertUser(user)
            val insertedUser = user.copy(id = userId)
            saveUserSession(insertedUser)
            AuthResult.Success(insertedUser)
        } catch (e: Exception) {
            AuthResult.Error("Registration failed: ${e.message}")
        }
    }
    
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    private suspend fun saveUserSession(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.id
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_NAME_KEY] = user.name
        }
    }
    
    suspend fun isUserLoggedIn(): Boolean {
        return try {
            var isLoggedIn = false
            context.dataStore.data.collect { preferences ->
                isLoggedIn = preferences[USER_ID_KEY] != null
            }
            isLoggedIn
        } catch (e: Exception) {
            false
        }
    }
}

data class UserSession(
    val id: Long,
    val email: String,
    val name: String
)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
} 