package com.quizwhiz.quizservice.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "quizzes")
data class QuizEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "course_id")
    val courseId: Long,
    val title: String,
    val description: String? = null,
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
