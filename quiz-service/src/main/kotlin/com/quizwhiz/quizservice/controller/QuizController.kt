package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.dto.QuizDto
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import java.util.*

@Controller
class QuizController(
    private val quizRepository: QuizRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val log = LoggerFactory.getLogger(QuizController::class.java)

    // Отображение формы для создания нового квиза.
    // Параметры: token (для безопасности) и courseId (ID курса, к которому привязывается квиз)
    @GetMapping("/quizzes/new")
    fun newQuizForm(request: HttpServletRequest, model: Model): String {
        val token = request.getParameter("token")
        log.debug("Получен токен: {}", token)

        val courseId = request.getParameter("courseId") ?: ""
        log.debug("Получен courseId: {}", courseId)

        // Создаем пустой DTO для формы с дефолтными значениями
        val quizDto = QuizDto(
            courseId = courseId,
            title = "",
            description = "",
            questionIds = emptyList(),
            answerType = "MULTIPLE_CHOICE", // пример дефолтного типа ответа
            isOpen = false
        )
        model.addAttribute("quizDto", quizDto)
        model.addAttribute("token", token)
        return "newquiz"  // Файл newquiz.html должен находиться в src/main/resources/templates/
    }

    // Обработка формы создания квиза (POST)
    @PostMapping("/quizzes")
    fun createQuiz(
        @ModelAttribute quizDto: QuizDto,
        request: HttpServletRequest
    ): String {
        val token = request.getParameter("token") ?: ""
        log.debug("Получен токен из формы: {}", token)
        val teacherUsername = jwtTokenProvider.getUsernameFromJWT(token) ?: ""

        // Создаем документ квиза для MongoDB
        val quiz = QuizDocument(
            courseId = quizDto.courseId,
            title = quizDto.title,
            description = quizDto.description,
            questionIds = quizDto.questionIds,
            answerType = quizDto.answerType,
            isOpen = quizDto.isOpen,
            createdAt = Date(),
            updatedAt = Date()
        )
        val savedQuiz = quizRepository.save(quiz)
        log.info("Квиз успешно создан с id: {}", savedQuiz.id)

        // Редирект на страницу квизов курса (в сервисе user-profile)
        return "redirect:http://127.0.0.1:8083/courses/${quizDto.courseId}?token=$token"
    }
}