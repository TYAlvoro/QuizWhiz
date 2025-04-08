package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuizAttemptDocument
import com.quizwhiz.quizservice.dto.RecentQuizAttemptDto
import com.quizwhiz.quizservice.repository.QuizAttemptRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/quiz-attempts")
class QuizAttemptInternalController(
    private val quizAttemptRepository: QuizAttemptRepository,
    private val quizRepository: QuizRepository
) {
    @GetMapping("/recent/{nickname}")
    fun getRecentQuizAttempts(@PathVariable nickname: String): List<RecentQuizAttemptDto> {

        val allAttempts: List<QuizAttemptDocument> =
            quizAttemptRepository.findAll()
                .filter { it.nickname == nickname }
                .sortedByDescending { it.attemptedAt }
                .take(5)

        return allAttempts.map { attempt ->
            val quizDoc = quizRepository.findById(attempt.quizId).orElse(null)
            val quizTitle = quizDoc?.title ?: "(no title)"
            RecentQuizAttemptDto(
                quizId = attempt.quizId,
                quizTitle = quizTitle,
                score = attempt.score,
                totalQuestions = attempt.totalQuestions,
                attemptedAt = attempt.attemptedAt
            )
        }
    }
}
