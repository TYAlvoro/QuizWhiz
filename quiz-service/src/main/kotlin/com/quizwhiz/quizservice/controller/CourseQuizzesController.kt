package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.model.CourseEntity
import com.quizwhiz.quizservice.repository.CourseRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class CourseQuizzesController(
    private val courseRepository: CourseRepository,
    private val quizRepository: QuizRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/courses/{courseId}")
    fun showCourseQuizzes(
        @PathVariable courseId: Long,
        request: HttpServletRequest,
        model: Model
    ): String {
        val token = extractToken(request)
        val username = token?.let { jwtTokenProvider.getUsernameFromJWT(it) }
            ?: throw RuntimeException("Invalid token")
        val course: CourseEntity = courseRepository.findById(courseId)
            .orElseThrow { RuntimeException("Course not found") }
        model.addAttribute("username", username)
        model.addAttribute("course", course)
        val quizzes = quizRepository.findAllByCourseId(courseId.toString())
        model.addAttribute("quizzes", quizzes)
        return "courseQuizzes"
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
