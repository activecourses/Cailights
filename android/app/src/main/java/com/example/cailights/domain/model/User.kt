package com.example.cailights.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: Role
)

data class Role(
    val id: String,
    val name: String
)
