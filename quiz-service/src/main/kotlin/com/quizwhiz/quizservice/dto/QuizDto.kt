package com.quizwhiz.quizservice.dto

import java.util.*

data class QuizDto(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val description: String = "",
    val questionIds: List<String> = emptyList(),
    val answerType: String = "multiple-choice",
    val isOpen: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
