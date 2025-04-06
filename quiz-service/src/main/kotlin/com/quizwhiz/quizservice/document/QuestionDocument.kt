package com.quizwhiz.quizservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "questions")
data class QuestionDocument(
    @Id val id: String? = null,
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val createdBy: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
