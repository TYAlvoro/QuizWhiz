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

    /**
     * Возвращаем последние N (например, 5) попыток прохождения квизов для указанного nickname,
     * отсортированные по дате убыванием (последние сначала).
     */
    @GetMapping("/recent/{nickname}")
    fun getRecentQuizAttempts(@PathVariable nickname: String): List<RecentQuizAttemptDto> {
        // Вариант: завести в QuizAttemptRepository метод вроде:
        // fun findTop5ByNicknameOrderByAttemptedAtDesc(nickname: String): List<QuizAttemptDocument>
        // Либо просто findAllByNickname(...) и сами сортируем. Ниже — упрощённо.

        // Допустим, хотим 5 последних:
        val allAttempts: List<QuizAttemptDocument> =
            quizAttemptRepository.findAll() // или findAllByNickname(nickname)
                .filter { it.nickname == nickname }
                .sortedByDescending { it.attemptedAt }
                .take(5)

        // Для вывода названия квиза можно подгрузить QuizDocument:
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
