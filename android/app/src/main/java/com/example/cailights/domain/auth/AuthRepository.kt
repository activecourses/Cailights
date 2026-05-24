package com.example.cailights.domain.auth

import com.example.cailights.domain.model.User
import com.example.cailights.ui.AuthError
import com.example.cailights.ui.Result

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User, AuthError>
    suspend fun signUp(email: String, password: String): Result<User, AuthError>
    suspend fun sendVerificationCode(email: String): Result<Unit, AuthError>
    suspend fun verifyCode(email: String, code: String): Result<Unit, AuthError>
}
