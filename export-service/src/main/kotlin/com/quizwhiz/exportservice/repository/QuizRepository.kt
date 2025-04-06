package com.quizwhiz.exportservice.repository

import com.quizwhiz.exportservice.document.QuizDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : MongoRepository<QuizDocument, String> {
    fun findAllByCreatorUsername(creatorUsername: String): List<QuizDocument>
}
