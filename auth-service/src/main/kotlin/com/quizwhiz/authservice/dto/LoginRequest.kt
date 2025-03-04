package com.quizwhiz.authservice.dto

data class LoginRequest(
    val username: String,
    val password: String
)