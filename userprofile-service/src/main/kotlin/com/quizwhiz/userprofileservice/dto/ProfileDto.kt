package com.quizwhiz.userprofileservice.dto

data class ProfileDto(
    val username: String,
    val email: String?,
    val role: String
)