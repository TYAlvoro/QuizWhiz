package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.repository.QuizRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/public/quizzes")
class PublicQuizController(
    private val quizRepository: QuizRepository
) {
    @GetMapping("/{quizId}")
    fun openPublicQuiz(
        @PathVariable quizId: String,
        @RequestParam(required = false) nickname: String?,
        model: Model
    ): String {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { RuntimeException("Quiz not found") }
        if (!quiz.isOpen) {
            throw RuntimeException("Quiz is not available publicly")
        }
        // Если никнейм не указан – отображаем форму ввода
        if (nickname.isNullOrBlank()) {
            model.addAttribute("quizId", quizId)
            return "enterNickname"
        }
        // Если никнейм указан – переходим к прохождению квиза
        model.addAttribute("quiz", quiz)
        model.addAttribute("nickname", nickname)
        return "takequiz"
    }
}
