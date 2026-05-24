package com.example.cailights.data.auth

import android.content.Context
import com.example.cailights.domain.auth.AuthRepository
import com.example.cailights.domain.model.Role
import com.example.cailights.domain.model.User
import com.example.cailights.ui.AuthError
import com.example.cailights.ui.Result
import kotlinx.coroutines.delay

class FakeAuthRepository(
    private val context: Context
) : AuthRepository {
    
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override suspend fun signIn(email: String, password: String): Result<User, AuthError> {
        delay(1500) // Simulate network delay
        return if (email == "test@example.com" && password == "password123") {
            val user = User(
                id = "1",
                username = "testuser",
                email = email,
                role = Role("1", "User")
            )
            prefs.edit().putBoolean("is_logged_in", true).apply()
            Result.Success(user)
        } else {
            Result.Error(AuthError.InvalidCredentials)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<User, AuthError> {
        delay(1500)
        return if (email.contains("error")) {
            Result.Error(AuthError.UserAlreadyExists)
        } else {
            val user = User(
                id = "2",
                username = email.substringBefore("@"),
                email = email,
                role = Role("1", "User")
            )
            // Note: In a real app, we'd wait for verification, but for simulation we'll log them in after verify
            Result.Success(user)
        }
    }

    override suspend fun sendVerificationCode(email: String): Result<Unit, AuthError> {
        delay(1000)
        return Result.Success(Unit)
    }

    override suspend fun verifyCode(email: String, code: String): Result<Unit, AuthError> {
        delay(1000)
        return if (code == "123456") {
            prefs.edit().putBoolean("is_logged_in", true).apply()
            Result.Success(Unit)
        } else {
            Result.Error(AuthError.InvalidCredentials)
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }
}
