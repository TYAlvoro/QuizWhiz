package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.dto.CourseDto
import com.quizwhiz.quizservice.model.CourseEntity
import com.quizwhiz.quizservice.model.UserCourseEntity
import com.quizwhiz.quizservice.repository.CourseRepository
import com.quizwhiz.quizservice.repository.UserCourseRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.ui.ConcurrentModel

class CourseControllerTest {

    private lateinit var courseRepository: CourseRepository
    private lateinit var userCourseRepository: UserCourseRepository
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var controller: CourseController

    @BeforeEach
    fun setUp() {
        courseRepository = mock()
        userCourseRepository = mock()
        jwtTokenProvider = mock()
        controller = CourseController(courseRepository, userCourseRepository, jwtTokenProvider)
    }

    @Test
    fun `newCourseForm should populate model and return newCourse view`() {
        val request = mock<HttpServletRequest>()
        val model = ConcurrentModel()
        val token = "validToken"
        val teacherId = 42L

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUserIdFromJWT(token)).thenReturn(teacherId)

        val viewName = controller.newCourseForm(request, model)
        assertEquals("newCourse", viewName)
        val courseDto = model.getAttribute("courseDto") as CourseDto
        assertEquals(teacherId, courseDto.teacherId)
    }

    @Test
    fun `createCourse should save course and redirect to profile`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        val teacherUsername = "teacherName"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(teacherUsername)

        val courseDto = CourseDto(id=0, courseName="Kotlin 101", description="Learn Kotlin", teacherId=123)
        val savedCourse = CourseEntity(id=1, courseName="Kotlin 101", description="Learn Kotlin", teacherId=123)
        whenever(courseRepository.save(any())).thenReturn(savedCourse)

        val result = controller.createCourse(courseDto, request)
        assertEquals("redirect:http://127.0.0.1:8082/profile/$teacherUsername/courses", result)

        verify(courseRepository).save(argThat {
            this.courseName == "Kotlin 101" &&
                    this.description == "Learn Kotlin" &&
                    this.teacherId == 123L
        })
        verify(userCourseRepository).save(any<UserCourseEntity>())
    }
}
