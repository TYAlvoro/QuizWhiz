package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.dto.QuizDto
import com.quizwhiz.quizservice.repository.QuestionRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.Date

@Controller
class QuizController(
    private val quizRepository: QuizRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val questionRepository: QuestionRepository
) {

    private val log = LoggerFactory.getLogger(QuizController::class.java)

    @GetMapping("/quizzes/new")
    fun newQuizForm(request: HttpServletRequest, model: Model): String {
        val token = extractToken(request)
        val courseId = request.getParameter("courseId") ?: ""
        val quizDto = QuizDto(
            courseId = courseId,
            title = "",
            description = "",
            questionIds = emptyList(),
            answerType = "multiple-choice",
            isOpen = false
        )
        model.addAttribute("quizDto", quizDto)
        model.addAttribute("token", token)
        if (!token.isNullOrEmpty()) {
            val username = jwtTokenProvider.getUsernameFromJWT(token) ?: ""
            val questions = questionRepository.findAllByCreatedBy(username)
            model.addAttribute("questions", questions)
        }
        return "newquiz"
    }

    @PostMapping("/quizzes")
    fun createQuiz(
        @ModelAttribute quizDto: QuizDto,
        request: HttpServletRequest
    ): String {
        val token = extractToken(request) ?: ""
        val creatorUsername = jwtTokenProvider.getUsernameFromJWT(token) ?: ""
        var savedQuiz = quizRepository.save(
            QuizDocument(
                courseId = quizDto.courseId,
                title = quizDto.title,
                description = quizDto.description,
                questionIds = quizDto.questionIds,
                answerType = quizDto.answerType,
                isOpen = quizDto.isOpen,
                creatorUsername = creatorUsername,
                createdAt = Date(),
                updatedAt = Date()
            )
        )
        if (savedQuiz.isOpen) {
            val generatedLink = "http://127.0.0.1:8083/public/quizzes/${savedQuiz.id}"
            savedQuiz = quizRepository.save(savedQuiz.copy(publicLink = generatedLink))
        }
        return "redirect:http://127.0.0.1:8083/courses/${quizDto.courseId}"
    }

    @GetMapping("/quizzes/{quizId}")
    fun openQuiz(
        @PathVariable quizId: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val token = extractToken(request) ?: throw RuntimeException("Token is missing")
        val currentUsername = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")
        val quiz = quizRepository.findById(quizId).orElseThrow { RuntimeException("Quiz not found") }
        model.addAttribute("quiz", quiz)
        model.addAttribute("token", token)
        return if (quiz.creatorUsername == currentUsername) "editquiz" else "takequiz"
    }

    @PostMapping("/quizzes/update")
    fun updateQuiz(
        @ModelAttribute quizDto: QuizDto,
        request: HttpServletRequest
    ): String {
        val token = extractToken(request) ?: ""
        val currentUsername = jwtTokenProvider.getUsernameFromJWT(token) ?: ""
        val existingQuiz = quizRepository.findById(quizDto.id)
            .orElseThrow { RuntimeException("Quiz not found") }
        if (existingQuiz.creatorUsername != currentUsername) {
            throw RuntimeException("Unauthorized update attempt")
        }
        var updatedQuiz = existingQuiz.copy(
            title = quizDto.title,
            description = quizDto.description,
            answerType = quizDto.answerType,
            isOpen = quizDto.isOpen,
            updatedAt = Date()
        )
        updatedQuiz = if (updatedQuiz.isOpen) {
            val generatedLink = "http://127.0.0.1:8083/public/quizzes/${updatedQuiz.id}"
            quizRepository.save(updatedQuiz.copy(publicLink = generatedLink))
        } else {
            quizRepository.save(updatedQuiz.copy(publicLink = null))
        }
        return "redirect:http://127.0.0.1:8083/courses/${updatedQuiz.courseId}"
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        val cookies = request.cookies
        if (cookies != null) {
            cookies.forEach {
                if (it.name == "JWT") return it.value
            }
        }
        return null
    }
}
