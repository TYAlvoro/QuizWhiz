package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuestionDocument
import com.quizwhiz.quizservice.dto.CreateQuestionDto
import com.quizwhiz.quizservice.repository.QuestionRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.*

@Controller
class QuestionController(
    private val questionRepository: QuestionRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val log = LoggerFactory.getLogger(QuestionController::class.java)

    // Форма создания нового вопроса внутри процесса создания квиза
    @GetMapping("/quizzes/{quizId}/questions/new")
    fun newQuestionForm(
        @PathVariable quizId: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val token = request.getParameter("token")
        // Извлекаем имя пользователя из токена
        val username = jwtTokenProvider.getUsernameFromJWT(token ?: "")
            ?: throw RuntimeException("Invalid token")
        // Создаем пустой DTO для формы с тремя пустыми вариантами по умолчанию
        val createQuestionDto = CreateQuestionDto(
            options = mutableListOf("", "", "")
        )
        model.addAttribute("createQuestionDto", createQuestionDto)
        model.addAttribute("quizId", quizId)
        model.addAttribute("token", token)
        return "newquestion" // шаблон newquestion.html
    }

    // Обработка создания нового вопроса
    @PostMapping("/quizzes/{quizId}/questions")
    fun createQuestion(
        @PathVariable quizId: String,
        @ModelAttribute createQuestionDto: CreateQuestionDto,
        request: HttpServletRequest
    ): String {
        val token = request.getParameter("token") ?: ""
        val username = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")

        // Проверяем, что пользователь указал как минимум 3 варианта ответа
        if (createQuestionDto.options.size < 3) {
            throw RuntimeException("Нужно указать как минимум 3 варианта ответа")
        }
        // Проверяем, что выбран правильный вариант
        val correctIndex = createQuestionDto.correctOptionIndex
            ?: throw RuntimeException("Не выбран правильный ответ")
        if (correctIndex !in 0 until createQuestionDto.options.size) {
            throw RuntimeException("Неверный индекс правильного ответа")
        }

        // Создаем новый вопрос
        val newQuestion = QuestionDocument(
            text = createQuestionDto.text,
            options = createQuestionDto.options,
            correctOptionIndex = correctIndex,
            createdBy = username,
            createdAt = Date(),
            updatedAt = Date()
        )
        questionRepository.save(newQuestion)
        log.info("Вопрос создан с id: {}", newQuestion.id)
        // После создания вопроса перенаправляем обратно на страницу создания квиза.
        // Параметр quizId здесь используется для возврата на страницу создания квиза.
        return "redirect:/quizzes/new?token=$token&courseId=$quizId"
    }
}
