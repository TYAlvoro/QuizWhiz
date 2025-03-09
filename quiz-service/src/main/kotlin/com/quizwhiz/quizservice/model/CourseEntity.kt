package com.quizwhiz.quizservice.model

import jakarta.persistence.*

@Entity
@Table(name = "courses")
data class CourseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val courseName: String = "",
    val description: String? = "",
    val teacherId: Long = 0
)