package com.quizwhiz.userprofileservice.dto

data class ProfileDto(
    val id: Long,
    val username: String,
    val email: String?,
    val role: String
)