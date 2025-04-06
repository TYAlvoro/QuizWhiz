package com.quizwhiz.quizservice.repository

import com.quizwhiz.quizservice.document.QuizAttemptDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface QuizAttemptRepository : MongoRepository<QuizAttemptDocument, String>