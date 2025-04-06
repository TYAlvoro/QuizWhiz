package com.quizwhiz.exportservice.controller

import com.quizwhiz.exportservice.document.QuizDocument
import com.quizwhiz.exportservice.service.ExportService
import com.quizwhiz.exportservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/export")
class ExportController(
    private val exportService: ExportService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/list")
    fun listExports(model: Model, request: HttpServletRequest): String {
        val token = extractToken(request)
        val username = token?.let { jwtTokenProvider.getUsernameFromJWT(it) } ?: "unknown"

        val quizzes: List<QuizDocument> = exportService.getExportableQuizzes(username)
        model.addAttribute("quizzes", quizzes)
        return "exportList"
    }

    @GetMapping("/quiz/{quizId}")
    fun exportQuiz(@PathVariable quizId: String, response: HttpServletResponse) {
        val csvContent = exportService.exportQuizResults(quizId)
        response.contentType = "text/csv;charset=UTF-8"
        response.characterEncoding = "UTF-8"
        response.setHeader("Content-Disposition", "attachment; filename=\"quiz_${quizId}_results.csv\"")
        response.writer.write(csvContent)
    }

    @GetMapping
    fun exportRoot(): String {
        return "redirect:/export/list"
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
