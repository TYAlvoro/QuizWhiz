package com.quizwhiz.quizservice.repository

import com.quizwhiz.quizservice.model.QuizEntity
import org.springframework.data.jpa.repository.JpaRepository

interface QuizRepository : JpaRepository<QuizEntity, Long> {
    // метод для получения квизов по ID курса
    fun findAllByCourseId(courseId: Long): List<QuizEntity>
}