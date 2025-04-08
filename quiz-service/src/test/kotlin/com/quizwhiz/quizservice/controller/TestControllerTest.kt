package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.*
import com.quizwhiz.quizservice.dto.AnswerDto
import com.quizwhiz.quizservice.repository.QuestionRepository
import com.quizwhiz.quizservice.repository.QuizAttemptRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import com.quizwhiz.quizservice.service.AchievementService
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.ui.ConcurrentModel
import java.util.Date
import java.util.Optional

class TestControllerTest {

    private lateinit var quizRepository: QuizRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var quizAttemptRepository: QuizAttemptRepository
    private lateinit var achievementService: AchievementService
    private lateinit var controller: TestController

    @BeforeEach
    fun setUp() {
        quizRepository = mock()
        questionRepository = mock()
        quizAttemptRepository = mock()
        achievementService = mock()
        controller = TestController(quizRepository, questionRepository, quizAttemptRepository, achievementService)
    }

    @Test
    fun `showQuizAttemptPage should return quizAttempt template`() {
        val quizDoc = QuizDocument(
            id="q123", courseId="10", title="Quiz", description="", answerType="multiple", isOpen=true
        )
        whenever(quizRepository.findById("q123")).thenReturn(Optional.of(quizDoc))
        whenever(questionRepository.findAllByIdIn(any())).thenReturn(emptyList())

        val model = ConcurrentModel()
        val viewName = controller.showQuizAttemptPage("q123", "userNick", model)
        assertEquals("quizAttempt", viewName)
        assertEquals(quizDoc, model.getAttribute("quiz"))
        assertEquals("userNick", model.getAttribute("nickname"))
    }

    @Test
    fun `submitQuizAttempt should save attempt and redirect to quizResult`() {
        val request = mock<HttpServletRequest>()
        whenever(request.getParameter("nickname")).thenReturn("userNick")
        whenever(request.parameterMap).thenReturn(
            mapOf("answers[q1]" to arrayOf("1"), "answers[q2]" to arrayOf("0"))
        )

        val quizDoc = QuizDocument(
            id="q123", courseId="10", title="Quiz Title", description="", answerType="multiple",
            isOpen=true, questionIds=listOf("q1","q2")
        )
        whenever(quizRepository.findById("q123")).thenReturn(Optional.of(quizDoc))

        val question1 = QuestionDocument(id="q1", text="?", options=listOf("A","B"), correctOptionIndex=1, createdBy="creator")
        val question2 = QuestionDocument(id="q2", text="?", options=listOf("A","B"), correctOptionIndex=0, createdBy="creator")
        whenever(questionRepository.findAllByIdIn(listOf("q1","q2"))).thenReturn(listOf(question1, question2))

        val model = ConcurrentModel()
        val viewName = controller.submitQuizAttempt("q123", request, model)
        assertEquals("quizResult", viewName)
        assertNotNull(model.getAttribute("attempt"))

        // Проверяем, что quizAttemptRepository.save(...) был вызван
        verify(quizAttemptRepository).save(any<QuizAttemptDocument>())
        // Проверяем вызов processAchievements
        verify(achievementService).processAchievements(eq("userNick"), eq("q123"), any(), eq(2))

    }
}
