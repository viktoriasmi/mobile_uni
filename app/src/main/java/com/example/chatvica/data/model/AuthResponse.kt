package com.example.chatvica.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: UserResponse
)