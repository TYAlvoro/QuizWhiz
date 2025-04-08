package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.model.CourseEntity
import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.repository.CourseRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.ui.ConcurrentModel
import java.util.Optional

class CourseQuizzesControllerTest {

    private val courseRepository = mock<CourseRepository>()
    private val quizRepository = mock<QuizRepository>()
    private val jwtTokenProvider = mock<JwtTokenProvider>()
    private val controller = CourseQuizzesController(courseRepository, quizRepository, jwtTokenProvider)

    @Test
    fun `showCourseQuizzes should return courseQuizzes view`() {
        val request = mock<HttpServletRequest>()
        val model = ConcurrentModel()
        val token = "validToken"
        val username = "testuser"
        val courseId = 10L

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(username)

        val course = CourseEntity(id=courseId, courseName="Test Course", description="desc", teacherId=123)
        whenever(courseRepository.findById(courseId)).thenReturn(Optional.of(course))

        val quizList = listOf(
            QuizDocument(id="q1", courseId=courseId.toString(), title="Quiz1", description="desc1", answerType="multiple", isOpen=true),
            QuizDocument(id="q2", courseId=courseId.toString(), title="Quiz2", description="desc2", answerType="multiple", isOpen=false)
        )
        whenever(quizRepository.findAllByCourseId(courseId.toString())).thenReturn(quizList)

        val viewName = controller.showCourseQuizzes(courseId, request, model)
        assertEquals("courseQuizzes", viewName)
        val quizzes = model.getAttribute("quizzes") as List<QuizDocument>
        assertEquals(2, quizzes.size)
        assertEquals(username, model.getAttribute("username"))
        assertEquals(course, model.getAttribute("course"))
    }

    @Test
    fun `showCourseQuizzes should throw if token invalid`() {
        val request = mock<HttpServletRequest>()
        whenever(request.getHeader("Authorization")).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            controller.showCourseQuizzes(1L, request, ConcurrentModel())
        }
    }
}
