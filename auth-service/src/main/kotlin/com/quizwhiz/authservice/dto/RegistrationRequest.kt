package com.quizwhiz.authservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Email

data class RegistrationRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,
    @field:NotBlank(message = "Password is required")
    val password: String,
    val role: String = "USER",
    @field:Email(message = "Email should be valid")
    val email: String?
)