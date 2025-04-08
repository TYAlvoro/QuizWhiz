package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.ui.ConcurrentModel
import java.util.Optional

class PublicQuizControllerTest {

    private lateinit var quizRepository: QuizRepository
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var controller: PublicQuizController

    @BeforeEach
    fun setUp() {
        quizRepository = mock()
        jwtTokenProvider = mock()
        controller = PublicQuizController(quizRepository, jwtTokenProvider)
    }

    @Test
    fun `openPublicQuiz should redirect to login if no token`() {
        val request = mock<HttpServletRequest>()
        whenever(request.requestURL).thenReturn(StringBuffer("http://127.0.0.1:8083/public/quizzes/q123"))
        whenever(request.queryString).thenReturn("param=val")

        val result = controller.openPublicQuiz("q123", null, request, ConcurrentModel())
        assertTrue(result.startsWith("redirect:http://127.0.0.1:8081/login?redirectUrl="))
        // Можно дополнительно проверить корректность encodedUrl
    }

    @Test
    fun `openPublicQuiz should throw if invalid token`() {
        val request = mock<HttpServletRequest>()
        whenever(request.getHeader("Authorization")).thenReturn("Bearer badToken")
        whenever(jwtTokenProvider.getUsernameFromJWT("badToken")).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            controller.openPublicQuiz("q123", null, request, ConcurrentModel())
        }
    }

    @Test
    fun `openPublicQuiz should throw if quiz not found`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")
        whenever(quizRepository.findById("q123")).thenReturn(Optional.empty())

        assertThrows(RuntimeException::class.java) {
            controller.openPublicQuiz("q123", null, request, ConcurrentModel())
        }
    }

    @Test
    fun `openPublicQuiz should throw if quiz is not open`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")
        val closedQuiz = QuizDocument(
            id = "q123", courseId="10", title="Closed Quiz", description="desc", answerType="multiple", isOpen=false
        )
        whenever(quizRepository.findById("q123")).thenReturn(Optional.of(closedQuiz))

        assertThrows(RuntimeException::class.java) {
            controller.openPublicQuiz("q123", "nick", request, ConcurrentModel())
        }
    }

    @Test
    fun `openPublicQuiz should add quiz and nickname to model if open`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")
        val openQuiz = QuizDocument(
            id="q123", courseId="10", title="Open Quiz", description="desc", answerType="multiple", isOpen=true
        )
        whenever(quizRepository.findById("q123")).thenReturn(Optional.of(openQuiz))

        val model = ConcurrentModel()
        val result = controller.openPublicQuiz("q123", "nick", request, model)
        assertEquals("takequiz", result)
        assertEquals(openQuiz, model.getAttribute("quiz"))
        assertEquals("testuser", model.getAttribute("nickname"))
    }
}
