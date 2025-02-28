package com.example.chatvica.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("Login")    // Пример, если сервер возвращает другие имена
    val username: String,

    @SerializedName("Name")
    val email: String
)