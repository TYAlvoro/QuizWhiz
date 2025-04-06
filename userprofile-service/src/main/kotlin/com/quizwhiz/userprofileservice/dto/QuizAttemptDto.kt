package com.quizwhiz.userprofileservice.dto

import java.util.Date

data class QuizAttemptDto(
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val attemptedAt: Date
)
