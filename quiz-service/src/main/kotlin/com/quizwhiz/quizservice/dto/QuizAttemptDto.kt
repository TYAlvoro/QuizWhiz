package com.quizwhiz.quizservice.dto

data class QuizAttemptDto(
    val quizId: String,
    val nickname: String,
    val answers: List<AnswerDto>
)

data class AnswerDto(
    val questionId: String,
    val selectedOptionIndex: Int
)
