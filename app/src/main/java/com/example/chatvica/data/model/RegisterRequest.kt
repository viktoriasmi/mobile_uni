package com.example.chatvica.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("Login") // Убедитесь, что сервер ожидает именно такое имя поля
    val login: String,

    @SerializedName("Name") // Возможно сервер ожидает "Email" вместо "Name"
    val name: String,       // Проверьте документацию API!

    @SerializedName("Password") // Добавьте если сервер ожидает с большой буквы
    val password: String
)