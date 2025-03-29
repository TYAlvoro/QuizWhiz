package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.dto.QuizDto
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class QuizController(
    private val quizRepository: QuizRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val log = LoggerFactory.getLogger(QuizController::class.java)

    // Форма для создания нового квиза
    @GetMapping("/quizzes/new")
    fun newQuizForm(request: HttpServletRequest, model: Model): String {
        val token = request.getParameter("token")
        log.debug("Получен токен: {}", token)
        val courseId = request.getParameter("courseId") ?: ""
        log.debug("Получен courseId: {}", courseId)
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
        return "newquiz"
    }

    // Создание квиза (POST)
    @PostMapping("/quizzes")
    fun createQuiz(
        @ModelAttribute quizDto: QuizDto,
        request: HttpServletRequest
    ): String {
        val token = request.getParameter("token") ?: ""
        log.debug("Получен токен из формы: {}", token)
        val creatorUsername = jwtTokenProvider.getUsernameFromJWT(token) ?: ""

        // Сохраняем квиз (без publicLink)
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

        // Если квиз открыт для прохождения, генерируем публичную ссылку
        if (savedQuiz.isOpen) {
            val generatedLink = "http://127.0.0.1:8083/public/quizzes/${savedQuiz.id}"
            savedQuiz = quizRepository.save(savedQuiz.copy(publicLink = generatedLink))
        }
        log.info("Квиз успешно создан с id: {}", savedQuiz.id)
        // Редирект обратно на страницу курса
        return "redirect:http://127.0.0.1:8083/courses/${quizDto.courseId}?token=$token"
    }

    // Открытие квиза по ссылке /quizzes/{quizId}?token=... для владельца
    // Если текущий пользователь является создателем, открывается окно редактирования, иначе – окно прохождения
    @GetMapping("/quizzes/{quizId}")
    fun openQuiz(
        @PathVariable quizId: String,
        @RequestParam token: String,
        model: Model
    ): String {
        val currentUsername = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")
        val quiz = quizRepository.findById(quizId).orElseThrow { RuntimeException("Quiz not found") }
        model.addAttribute("quiz", quiz)
        model.addAttribute("token", token)

        return if (quiz.creatorUsername == currentUsername) {
            "editquiz"  // страница редактирования для владельца
        } else {
            "takequiz"  // страница прохождения для остальных
        }
    }

    // Обновление квиза (редактирование)
    @PostMapping("/quizzes/update")
    fun updateQuiz(
        @ModelAttribute quizDto: QuizDto,
        request: HttpServletRequest
    ): String {
        val token = request.getParameter("token") ?: ""
        val currentUsername = jwtTokenProvider.getUsernameFromJWT(token) ?: ""

        // Ищем существующий квиз
        val existingQuiz = quizRepository.findById(quizDto.id)
            .orElseThrow { RuntimeException("Quiz not found") }

        if (existingQuiz.creatorUsername != currentUsername) {
            throw RuntimeException("Unauthorized update attempt")
        }

        // Обновляем поля квиза
        var updatedQuiz = existingQuiz.copy(
            title = quizDto.title,
            description = quizDto.description,
            answerType = quizDto.answerType,
            isOpen = quizDto.isOpen,
            updatedAt = Date()
        )

        // Если квиз открыт, генерируем (или обновляем) публичную ссылку
        updatedQuiz = if (updatedQuiz.isOpen) {
            val generatedLink = "http://127.0.0.1:8083/public/quizzes/${updatedQuiz.id}"
            quizRepository.save(updatedQuiz.copy(publicLink = generatedLink))
        } else {
            quizRepository.save(updatedQuiz.copy(publicLink = null))
        }

        return "redirect:http://127.0.0.1:8083/courses/${updatedQuiz.courseId}?token=$token"
    }
}
