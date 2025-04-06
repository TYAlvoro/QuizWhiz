package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.model.CourseEntity
import com.quizwhiz.quizservice.dto.CourseDto
import com.quizwhiz.quizservice.model.UserCourseEntity
import com.quizwhiz.quizservice.repository.CourseRepository
import com.quizwhiz.quizservice.repository.UserCourseRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class CourseController(
    private val courseRepository: CourseRepository,
    private val userCourseRepository: UserCourseRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val log = LoggerFactory.getLogger(CourseController::class.java)

    @GetMapping("/courses/new")
    fun newCourseForm(request: HttpServletRequest, model: Model): String {
        val token = extractToken(request)
        val teacherId = token?.let { jwtTokenProvider.getUserIdFromJWT(it) } ?: 0L
        val courseDto = CourseDto(id = 0, courseName = "", description = null, teacherId = teacherId)
        model.addAttribute("courseDto", courseDto)
        return "newcourse"
    }

    @PostMapping("/courses")
    fun createCourse(
        @ModelAttribute courseDto: CourseDto,
        request: HttpServletRequest
    ): String {
        val token = extractToken(request) ?: ""
        val teacherUsername = jwtTokenProvider.getUsernameFromJWT(token) ?: ""
        val savedCourse = courseRepository.save(
            CourseEntity(
                courseName = courseDto.courseName,
                description = courseDto.description,
                teacherId = courseDto.teacherId
            )
        )
        userCourseRepository.save(
            UserCourseEntity(
                userId = courseDto.teacherId,
                courseId = savedCourse.id
            )
        )
        return "redirect:http://127.0.0.1:8082/profile/$teacherUsername/courses"
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        val cookies = request.cookies
        if (cookies != null) {
            cookies.forEach {
                if (it.name == "JWT") return it.value
            }
        }
        return null
    }
}
