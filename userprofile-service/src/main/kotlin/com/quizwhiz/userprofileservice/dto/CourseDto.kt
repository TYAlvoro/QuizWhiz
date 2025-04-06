package com.quizwhiz.userprofileservice.dto

data class CourseDto(
    val id: Long,
    val courseName: String,
    val description: String?,
    val teacherId: Long
)
