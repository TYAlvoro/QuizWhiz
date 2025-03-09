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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ModelAttribute

@Controller
class CourseController(
    private val courseRepository: CourseRepository,
    private val userCourseRepository: UserCourseRepository, // <-- репозиторий для user_courses
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val log = LoggerFactory.getLogger(CourseController::class.java)

    @GetMapping("/courses/new")
    fun newCourseForm(request: HttpServletRequest, model: Model): String {
        val token = request.getParameter("token")
        val teacherId = token?.let { jwtTokenProvider.getUserIdFromJWT(it) } ?: 0L

        val courseDto = CourseDto(id = 0, courseName = "", description = null, teacherId = teacherId)
        model.addAttribute("courseDto", courseDto)
        model.addAttribute("token", token) // чтобы hidden-поле тоже заполнилось

        return "newcourse"
    }

    @PostMapping("/courses")
    fun createCourse(
        @ModelAttribute courseDto: CourseDto,
        request: HttpServletRequest
    ): String {
        val token = request.getParameter("token") ?: ""
        val teacherUsername = jwtTokenProvider.getUsernameFromJWT(token) ?: ""

        // Сохраняем сам курс
        val savedCourse = courseRepository.save(
            CourseEntity(
                courseName = courseDto.courseName,
                description = courseDto.description,
                teacherId = courseDto.teacherId
            )
        )

        // Добавляем запись в user_courses
        userCourseRepository.save(
            UserCourseEntity(
                userId = courseDto.teacherId,
                courseId = savedCourse.id
            )
        )

        // Редиректим на userprofile-service, передавая token
        return "redirect:http://127.0.0.1:8082/profile/$teacherUsername/courses?token=$token"
    }
}