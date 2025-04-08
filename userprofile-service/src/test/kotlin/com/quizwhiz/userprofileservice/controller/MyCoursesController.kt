package com.quizwhiz.userprofileservice.controller

import com.quizwhiz.userprofileservice.client.QuizServiceClient
import com.quizwhiz.userprofileservice.dto.CourseDto
import com.quizwhiz.userprofileservice.dto.ProfileDto
import com.quizwhiz.userprofileservice.service.ProfileService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.ConcurrentModel

class MyCoursesControllerTest {

    private val profileService = mock<ProfileService>()
    private val quizServiceClient = mock<QuizServiceClient>()
    private val controller = MyCoursesController(profileService, quizServiceClient)

    @Test
    fun `showCourses should populate model and return mycourses view if authenticated`() {
        // Задаём аутентификацию с именем пользователя "john"
        val auth = UsernamePasswordAuthenticationToken("john", null)
        SecurityContextHolder.getContext().authentication = auth

        val profile = ProfileDto(id = 100, username = "john", email = "john@example.com", role = "USER")
        whenever(profileService.getProfile("john")).thenReturn(profile)

        // Задаём тестовый список курсов типа List<CourseDto>
        val courses = listOf<CourseDto>(
            CourseDto(id = 1, courseName = "Course 1", description = "Desc 1", teacherId = 100),
            CourseDto(id = 2, courseName = "Course 2", description = "Desc 2", teacherId = 100)
        )
        whenever(quizServiceClient.getCoursesByTeacherId(100)).thenReturn(courses)

        val model = ConcurrentModel()
        val view = controller.showCourses("john", model)
        assertEquals("mycourses", view)
        assertEquals(profile, model.getAttribute("profile"))
        assertEquals(courses, model.getAttribute("courses"))
    }

    @Test
    fun `showCourses should redirect to login if not authenticated or username mismatch`() {
        // Очищаем контекст
        SecurityContextHolder.clearContext()
        val model = ConcurrentModel()
        val view = controller.showCourses("john", model)
        assertEquals("redirect:/login", view)
    }
}
