package com.quizwhiz.authservice.dto

data class RegistrationRequest(
    val username: String,
    val password: String,
    val role: String = "USER",
    val email: String?
)
