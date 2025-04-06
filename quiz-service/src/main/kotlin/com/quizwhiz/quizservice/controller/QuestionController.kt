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
import java.util.Date

@Controller
class QuestionController(
    private val questionRepository: QuestionRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val log = LoggerFactory.getLogger(QuestionController::class.java)

    @GetMapping("/quizzes/{quizId}/questions/new")
    fun newQuestionForm(
        @PathVariable quizId: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        val token = extractToken(request) ?: throw RuntimeException("Token is missing")
        val username = jwtTokenProvider.getUsernameFromJWT(token) ?: throw RuntimeException("Invalid token")
        val createQuestionDto = CreateQuestionDto(options = mutableListOf("", "", ""))
        model.addAttribute("createQuestionDto", createQuestionDto)
        model.addAttribute("quizId", quizId)
        model.addAttribute("token", token)
        return "newQuestion"
    }

    @PostMapping("/quizzes/{quizId}/questions")
    fun createQuestion(
        @PathVariable quizId: String,
        @ModelAttribute createQuestionDto: CreateQuestionDto,
        request: HttpServletRequest
    ): String {
        val token = extractToken(request) ?: ""
        val username = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")
        if (createQuestionDto.options.size < 3)
            throw RuntimeException("Нужно указать как минимум 3 варианта ответа")
        val correctIndex = createQuestionDto.correctOptionIndex
            ?: throw RuntimeException("Не выбран правильный ответ")
        if (correctIndex !in 0 until createQuestionDto.options.size)
            throw RuntimeException("Неверный индекс правильного ответа")
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
        return "redirect:/quizzes/new?courseId=$quizId"
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        request.cookies?.forEach {
            if (it.name == "JWT") return it.value
        }
        return null
    }
}
