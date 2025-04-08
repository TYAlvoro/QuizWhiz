package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.dto.CreateQuestionDto
import com.quizwhiz.quizservice.repository.QuestionRepository
import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.ui.ConcurrentModel
import java.util.Date

class QuestionControllerTest {

    private lateinit var questionRepository: QuestionRepository
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var controller: QuestionController

    @BeforeEach
    fun setUp() {
        questionRepository = mock()
        jwtTokenProvider = mock()
        controller = QuestionController(questionRepository, jwtTokenProvider)
    }

    @Test
    fun `newQuestionForm should return newQuestion view`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")

        val model = ConcurrentModel()
        val viewName = controller.newQuestionForm("quiz123", request, model)
        assertEquals("newQuestion", viewName)
        val dto = model.getAttribute("createQuestionDto") as? CreateQuestionDto
        assertNotNull(dto)
        assertEquals("quiz123", model.getAttribute("quizId"))
        assertEquals(token, model.getAttribute("token"))
    }

    @Test
    fun `createQuestion should save question and redirect`() {
        val request = mock<HttpServletRequest>()
        val token = "validToken"
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")

        val dto = CreateQuestionDto(
            text="Sample Question",
            options= mutableListOf("opt1","opt2","opt3"),
            correctOptionIndex=1
        )

        val result = controller.createQuestion("quiz123", dto, request)
        assertTrue(result.startsWith("redirect:/quizzes/new?courseId="))
        verify(questionRepository).save(argThat {
            this.text == "Sample Question" &&
                    this.options.size == 3 &&
                    this.correctOptionIndex == 1 &&
                    this.createdBy == "testuser"
        })
    }

    @Test
    fun `createQuestion should throw if not enough options`() {
        val request = mock<HttpServletRequest>()
        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtTokenProvider.getUsernameFromJWT("token")).thenReturn("testuser")

        val dto = CreateQuestionDto(text="Q?", options= mutableListOf("opt1","opt2"), correctOptionIndex=0)
        assertThrows(RuntimeException::class.java) {
            controller.createQuestion("quiz123", dto, request)
        }
    }

    @Test
    fun `createQuestion should throw if correctIndex is null`() {
        val request = mock<HttpServletRequest>()
        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtTokenProvider.getUsernameFromJWT("token")).thenReturn("testuser")

        val dto = CreateQuestionDto(text="Q?", options= mutableListOf("opt1","opt2","opt3"), correctOptionIndex=null)
        assertThrows(RuntimeException::class.java) {
            controller.createQuestion("quiz123", dto, request)
        }
    }
}
