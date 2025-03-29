package com.quizwhiz.quizservice.repository

import com.quizwhiz.quizservice.document.QuestionDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface QuestionRepository : MongoRepository<QuestionDocument, String> {
    fun findAllByCreatedBy(createdBy: String): List<QuestionDocument>
}
