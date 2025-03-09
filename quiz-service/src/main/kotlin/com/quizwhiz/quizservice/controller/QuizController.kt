package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.entity.QuizEntity
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/courses/{courseId}/quizzes")
class QuizController(
    private val quizRepository: QuizRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    // Страница с формой
    @GetMapping("/new")
    fun newQuizForm(
        @PathVariable courseId: Long,
        @RequestParam token: String,
        model: Model
    ): String {
        // Проверить токен, если нужно
        val username = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")

        model.addAttribute("courseId", courseId)
        model.addAttribute("token", token)
        return "newquiz" // шаблон формы
    }

    // Обработка формы (POST)
    @PostMapping
    fun createQuiz(
        @PathVariable courseId: Long,
        @RequestParam token: String,
        @RequestParam title: String,
        @RequestParam(required = false) description: String?
    ): String {
        // проверка токена
        val username = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")

        // Создаём сущность
        val quiz = QuizEntity(
            courseId = courseId,
            title = title,
            description = description
        )
        quizRepository.save(quiz)

        // После создания квиза возвращаемся на список квизов курса
        return "redirect:/courses/$courseId?token=$token"
    }
}