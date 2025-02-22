package com.example.chatvica.data.model

data class AuthResponse(
    val token: String,
    val user: UserResponse
)