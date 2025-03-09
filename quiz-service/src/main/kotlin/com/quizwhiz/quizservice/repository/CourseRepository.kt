package com.quizwhiz.quizservice.repository

import com.quizwhiz.quizservice.entity.CourseEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CourseRepository : JpaRepository<CourseEntity, Long> {
    fun findAllByTeacherId(teacherId: Long): List<CourseEntity>
}