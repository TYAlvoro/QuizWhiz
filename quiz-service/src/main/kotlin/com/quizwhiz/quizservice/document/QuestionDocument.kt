// File: src/main/kotlin/com/quizwhiz/quizservice/document/QuestionDocument.kt
package com.quizwhiz.quizservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "questions")
data class QuestionDocument(
    @Id val id: String? = null,
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int, // индекс от 0 до 2
    val createdBy: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
