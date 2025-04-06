package com.quizwhiz.quizservice.dto

import java.util.Date

data class RecentQuizAttemptDto(
    val quizId: String,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val attemptedAt: Date
)
