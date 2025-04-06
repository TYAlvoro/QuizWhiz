package com.quizwhiz.exportservice.dto

data class QuizDto(
    val id: String,
    val title: String,
    val description: String?,
    val teacherId: Long
)
