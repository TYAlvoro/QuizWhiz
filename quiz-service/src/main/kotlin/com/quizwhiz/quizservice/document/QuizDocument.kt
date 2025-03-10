package com.quizwhiz.quizservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "quizzes")
data class QuizDocument(
    @Id val id: String? = null,
    val courseId: String,           // ID курса из PostgreSQL
    val title: String,
    val description: String,
    val questionIds: List<String>,  // список ObjectId вопросов в виде строк
    val answerType: String,         // тип ответа, например: "multiple-choice", "drag-and-drop" и т.п.
    val isOpen: Boolean,            // открыт ли квиз для прохождения
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)