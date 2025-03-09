package com.quizwhiz.quizservice.dto

data class CourseDto(
    val id: Long,
    val courseName: String,
    val description: String?,
    val teacherId: Long
)