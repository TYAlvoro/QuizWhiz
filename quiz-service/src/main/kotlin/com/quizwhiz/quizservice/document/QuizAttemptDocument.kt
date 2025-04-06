package com.quizwhiz.quizservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "quiz_attempts")
data class QuizAttemptDocument(
    @Id val id: String? = null,
    val quizId: String,
    val nickname: String,
    val answers: List<AnswerRecord>,
    val score: Int,
    val totalQuestions: Int,
    val attemptedAt: Date = Date()
)

data class AnswerRecord(
    val questionId: String,
    val selectedOptionIndex: Int,
    val correct: Boolean
)