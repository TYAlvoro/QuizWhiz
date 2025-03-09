package com.quizwhiz.quizservice.model

import jakarta.persistence.*

@Entity
@Table(name = "user_courses")
data class UserCourseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "course_id")
    val courseId: Long
)