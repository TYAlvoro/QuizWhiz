package com.quizwhiz.quizservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "quizzes")
data class QuizDocument(
    @Id val id: String? = null,
    val courseId: String,           // ID курса (из PostgreSQL)
    val title: String,
    val description: String,
    val questionIds: List<String>,  // список идентификаторов вопросов
    val answerType: String,         // тип ответа: например, "multiple-choice", "drag-and-drop" и т.д.
    val isOpen: Boolean,            // открыт ли квиз для прохождения
    val creatorUsername: String = "", // имя пользователя, создавшего квиз (default чтобы не было null)
    val publicLink: String? = null, // публичная ссылка для прохождения квиза (генерируется, если isOpen=true)
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
