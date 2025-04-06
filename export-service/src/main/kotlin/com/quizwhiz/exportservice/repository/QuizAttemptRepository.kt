package com.quizwhiz.exportservice.repository

import com.quizwhiz.exportservice.document.QuizAttemptDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizAttemptRepository : MongoRepository<QuizAttemptDocument, String> {
    fun findAllByQuizId(quizId: String): List<QuizAttemptDocument>
}
