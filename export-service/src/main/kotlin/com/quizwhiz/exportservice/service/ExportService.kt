package com.quizwhiz.exportservice.service

import com.quizwhiz.exportservice.document.QuizAttemptDocument
import com.quizwhiz.exportservice.document.QuizDocument
import com.quizwhiz.exportservice.repository.QuizAttemptRepository
import com.quizwhiz.exportservice.repository.QuizRepository
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class ExportService(
    private val quizRepository: QuizRepository,
    private val quizAttemptRepository: QuizAttemptRepository
) {
    fun exportQuizResults(quizId: String): String {
        val quiz: QuizDocument = quizRepository.findById(quizId)
            .orElseThrow { RuntimeException("Quiz not found") }
        val attempts: List<QuizAttemptDocument> = quizAttemptRepository.findAllByQuizId(quizId)
        val writer = StringWriter()
        writer.append("Quiz Title,Nickname,Score\n")
        attempts.forEach { attempt ->
            writer.append("\"${quiz.title}\",\"${attempt.nickname}\",${attempt.score}\n")
        }
        return writer.toString()
    }

    fun getExportableQuizzes(username: String): List<QuizDocument> {
        return quizRepository.findAllByCreatorUsername(username)
    }
}
