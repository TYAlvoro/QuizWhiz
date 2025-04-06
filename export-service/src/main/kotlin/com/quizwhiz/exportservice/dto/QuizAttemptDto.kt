package com.quizwhiz.exportservice.dto

import java.util.Date

data class QuizAttemptDto(
    val quizId: String,
    val nickname: String,
    val score: Int,
    val totalQuestions: Int,
    val attemptedAt: Date
)
