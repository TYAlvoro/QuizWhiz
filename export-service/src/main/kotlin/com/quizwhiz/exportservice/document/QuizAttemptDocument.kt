package com.quizwhiz.exportservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "quiz_attempts")
data class QuizAttemptDocument(
    @Id val id: String? = null,
    val quizId: String,
    val nickname: String,
    val score: Int,
    val totalQuestions: Int,
    val attemptedAt: Date = Date()
)
