package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.dto.QuizDto
import com.quizwhiz.quizservice.repository.QuestionRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.ui.ConcurrentModel
import java.util.Date
import java.util.Optional

class QuizControllerTest {

    private val quizRepository = mock<QuizRepository>()
    private val jwtTokenProvider = mock<JwtTokenProvider>()
    private val questionRepository = mock<QuestionRepository>()
    private val controller = QuizController(quizRepository, jwtTokenProvider, questionRepository)

    @Test
    fun `newQuizForm should return newquiz view and add quizDto`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")
        whenever(request.getParameter("courseId")).thenReturn("10")

        whenever(questionRepository.findAllByCreatedBy("testuser")).thenReturn(emptyList())

        val model = ConcurrentModel()
        val viewName = controller.newQuizForm(request, model)
        assertEquals("newquiz", viewName)
        val quizDto = model.getAttribute("quizDto") as? QuizDto
        assertEquals("10", quizDto?.courseId)
    }

    @Test
    fun `createQuiz should save quiz and redirect`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        val username = "testuser"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(username)

        val quizDto = QuizDto(courseId="10", title="Quiz Title", description="desc", isOpen=true)
        val savedQuiz = QuizDocument(
            id="q123", courseId="10", title="Quiz Title", description="desc",
            answerType="multiple", isOpen=true, creatorUsername=username, createdAt= Date(), updatedAt= Date()
        )
        whenever(quizRepository.save(any())).thenReturn(savedQuiz)

        val result = controller.createQuiz(quizDto, request)
        assertEquals("redirect:http://127.0.0.1:8083/courses/10", result)

        // Проверяем, что при isOpen=true в savedQuiz проставляется publicLink
        verify(quizRepository, times(2)).save(any())
        // Первый раз - без publicLink, второй раз - обновляем с generatedLink
    }

    @Test
    fun `openQuiz should return editQuiz if quiz belongs to user`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        val username = "creator"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(username)

        val quizDoc = QuizDocument(id="q123", courseId="10", title="Title", description="", answerType="multiple", isOpen=false, creatorUsername=username)
        whenever(quizRepository.findById("q123")).thenReturn(Optional.of(quizDoc))

        val model = ConcurrentModel()
        val viewName = controller.openQuiz("q123", request, model)
        assertEquals("editQuiz", viewName)
        assertEquals(quizDoc, model.getAttribute("quiz"))
    }

    @Test
    fun `openQuiz should return takeQuiz if quiz belongs to another user`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("someoneElse")

        val quizDoc = QuizDocument(id="q123", courseId="10", title="Title", description="", answerType="multiple", isOpen=false, creatorUsername="creator")
        whenever(quizRepository.findById("q123")).thenReturn(Optional.of(quizDoc))

        val model = ConcurrentModel()
        val viewName = controller.openQuiz("q123", request, model)
        assertEquals("takeQuiz", viewName)
    }

    @Test
    fun `updateQuiz should save and redirect`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("creator")

        val existing = QuizDocument(
            id="q123", courseId="10", title="Old", description="desc", answerType="multiple", isOpen=false, creatorUsername="creator"
        )
        whenever(quizRepository.findById("q123")).thenReturn(Optional.of(existing))
        whenever(quizRepository.save(any())).thenReturn(existing.copy(isOpen=true, publicLink="..."))

        val quizDto = QuizDto(id="q123", courseId="10", title="NewTitle", description="NewDesc", isOpen=true)
        val result = controller.updateQuiz(quizDto, request)
        assertEquals("redirect:http://127.0.0.1:8083/courses/10", result)
    }
}
