package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.model.CourseEntity
import com.quizwhiz.quizservice.model.QuizEntity
import com.quizwhiz.quizservice.repository.CourseRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CourseQuizzesController(
    private val courseRepository: CourseRepository,
    private val quizRepository: QuizRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/courses/{courseId}")
    fun showCourseQuizzes(
        @PathVariable courseId: Long,
        @RequestParam token: String, // получаем token как request param
        model: Model
    ): String {
        // 1) Проверить токен (опционально можно проверить userId/username)
        val username = jwtTokenProvider.getUsernameFromJWT(token)
            ?: throw RuntimeException("Invalid token")

        // 2) Найти курс по ID
        val course: CourseEntity = courseRepository.findById(courseId).orElseThrow {
            RuntimeException("Course not found")
        }

        // 3) Найти квизы для этого курса
        val quizzes: List<QuizEntity> = quizRepository.findAllByCourseId(courseId)

        // 4) Положить данные в модель
        model.addAttribute("course", course)
        model.addAttribute("quizzes", quizzes)
        model.addAttribute("token", token)
        model.addAttribute("username", username)

        // 5) Вернуть шаблон, например, "courseQuizzes"
        return "courseQuizzes"
    }
}