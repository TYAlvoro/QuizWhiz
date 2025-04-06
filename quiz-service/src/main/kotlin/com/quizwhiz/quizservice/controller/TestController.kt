package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.AnswerRecord
import com.quizwhiz.quizservice.document.QuizAttemptDocument
import com.quizwhiz.quizservice.document.QuestionDocument
import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.dto.AnswerDto
import com.quizwhiz.quizservice.repository.QuestionRepository
import com.quizwhiz.quizservice.repository.QuizAttemptRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.Date

@Controller
class TestController(
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val quizAttemptRepository: QuizAttemptRepository
) {

    private val log = LoggerFactory.getLogger(TestController::class.java)

    @PermitAll
    @GetMapping("/quizzes/{quizId}/attempt")
    fun showQuizAttemptPage(
        @PathVariable quizId: String,
        @RequestParam nickname: String,
        model: Model
    ): String {
        val quiz: QuizDocument = quizRepository.findById(quizId)
            .orElseThrow { RuntimeException("Quiz not found") }
        val questions: List<QuestionDocument> = questionRepository.findAllByIdIn(quiz.questionIds)
        model.addAttribute("quiz", quiz)
        model.addAttribute("questions", questions)
        model.addAttribute("nickname", nickname)
        return "quizAttempt"
    }

    @PostMapping("/quizzes/{quizId}/attempt")
    fun submitQuizAttempt(
        @PathVariable quizId: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val nickname = request.getParameter("nickname") ?: throw RuntimeException("Nickname is required")
        val paramMap = request.parameterMap
        val answers = mutableListOf<AnswerDto>()
        paramMap.filterKeys { it.startsWith("answers[") && it.endsWith("]") }
            .forEach { (key, valueArr) ->
                val questionId = key.substringAfter("[").substringBefore("]")
                val selectedIndex = valueArr.firstOrNull()?.toIntOrNull()
                    ?: throw RuntimeException("Invalid answer for question $questionId")
                answers.add(AnswerDto(questionId, selectedIndex))
            }
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { RuntimeException("Quiz not found") }
        val questions: Map<String, QuestionDocument> = questionRepository.findAllByIdIn(quiz.questionIds)
            .associateBy { it.id!! }
        var correctCount = 0
        val answerRecords = answers.map { answerDto ->
            val question = questions[answerDto.questionId]
                ?: throw RuntimeException("Question not found: ${answerDto.questionId}")
            val isCorrect = question.correctOptionIndex == answerDto.selectedOptionIndex
            if (isCorrect) correctCount++
            AnswerRecord(
                questionId = answerDto.questionId,
                selectedOptionIndex = answerDto.selectedOptionIndex,
                correct = isCorrect
            )
        }
        val attempt = QuizAttemptDocument(
            quizId = quizId,
            nickname = nickname,
            answers = answerRecords,
            score = correctCount,
            totalQuestions = quiz.questionIds.size,
            attemptedAt = Date()
        )
        quizAttemptRepository.save(attempt)
        model.addAttribute("attempt", attempt)
        return "quizResult"
    }
}
