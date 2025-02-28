package com.example.chatvica.data.model

data class RegisterRequest(
    val login: String,
    val name: String,
    val password: String
)