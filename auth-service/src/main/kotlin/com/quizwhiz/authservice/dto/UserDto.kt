package com.quizwhiz.authservice.dto

data class UserDto(
    val id: Long,
    val username: String,
    val email: String?,
    val role: String
)
