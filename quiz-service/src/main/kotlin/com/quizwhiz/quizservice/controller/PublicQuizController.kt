package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequestMapping("/public/quizzes")
class PublicQuizController(
    private val quizRepository: QuizRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/{quizId}")
    fun openPublicQuiz(
        @PathVariable quizId: String,
        @RequestParam(required = false) nickname: String?,
        request: HttpServletRequest,
        model: Model
    ): String {
        // Попытка извлечь JWT из заголовка или cookie
        val token = extractToken(request)
        if (token.isNullOrEmpty()) {
            // Если пользователь не авторизован – перенаправляем его на сервис авторизации с параметром redirectUrl
            val currentUrl = request.requestURL.toString() +
                    if (!request.queryString.isNullOrEmpty()) "?" + request.queryString else ""
            val encodedUrl = URLEncoder.encode(currentUrl, "UTF-8")
            return "redirect:http://127.0.0.1:8081/login?redirectUrl=$encodedUrl"
        }
        // Если токен есть, извлекаем username и подставляем его вместо временного nick
        val username = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { RuntimeException("Quiz not found") }
        if (!quiz.isOpen) {
            throw RuntimeException("Quiz is not available publicly")
        }
        model.addAttribute("quiz", quiz)
        model.addAttribute("nickname", username)
        return "takequiz"
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
