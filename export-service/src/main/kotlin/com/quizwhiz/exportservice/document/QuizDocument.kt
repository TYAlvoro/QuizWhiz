package com.quizwhiz.exportservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "quizzes")
data class QuizDocument(
    @Id val id: String? = null,
    val courseId: String,
    val title: String,
    val description: String,
    val questionIds: List<String> = emptyList(),
    val answerType: String,
    val isOpen: Boolean,
    val creatorUsername: String? = null,
    val publicLink: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
