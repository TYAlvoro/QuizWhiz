package com.quizwhiz.quizservice.repository

import com.quizwhiz.quizservice.document.QuizDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface QuizRepository : MongoRepository<QuizDocument, String> {
    fun findAllByCourseId(courseId: String): List<QuizDocument>
}
