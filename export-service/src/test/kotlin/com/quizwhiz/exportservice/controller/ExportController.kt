package com.quizwhiz.exportservice.controller

import com.quizwhiz.exportservice.document.QuizDocument
import com.quizwhiz.exportservice.security.JwtTokenProvider
import com.quizwhiz.exportservice.service.ExportService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.ui.ConcurrentModel

class ExportControllerTest {

    private lateinit var exportService: ExportService
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var controller: ExportController

    @BeforeEach
    fun setUp() {
        exportService = mock()
        jwtTokenProvider = mock()
        controller = ExportController(exportService, jwtTokenProvider)
    }

    @Test
    fun `listExports should set quizzes in model and return exportList`() {
        val request = mock<HttpServletRequest>()
        val model = ConcurrentModel()
        val token = "validToken"
        val username = "testuser"

        // Настроим заголовок
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        // Мокаем токен
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(username)

        val quizList = listOf(
            QuizDocument(id = "q1", courseId = "c1", title = "Quiz1", description = "desc", answerType = "multiple", isOpen = true, creatorUsername = username),
            QuizDocument(id = "q2", courseId = "c1", title = "Quiz2", description = "desc2", answerType = "multiple", isOpen = false, creatorUsername = username)
        )
        whenever(exportService.getExportableQuizzes(username)).thenReturn(quizList)

        val viewName = controller.listExports(model, request)

        assertEquals("exportList", viewName)
        assertNotNull(model.getAttribute("quizzes"))
        val actualList = model.getAttribute("quizzes") as? List<QuizDocument>
        assertEquals(2, actualList?.size)
        verify(exportService).getExportableQuizzes(username)
    }

    @Test
    fun `listExports should handle missing token and use username=unknown`() {
        val request = mock<HttpServletRequest>()
        val model = ConcurrentModel()
        // Никакого Authorization заголовка и cookie

        // Тогда username = "unknown"
        whenever(exportService.getExportableQuizzes("unknown")).thenReturn(emptyList())
        val viewName = controller.listExports(model, request)

        assertEquals("exportList", viewName)
        val quizzes = model.getAttribute("quizzes") as? List<QuizDocument>
        assertTrue(quizzes?.isEmpty() == true)
    }

    @Test
    fun `exportQuiz should write CSV to response`() {
        val quizId = "quiz1"
        val response = mock<HttpServletResponse>()
        val csv = "Quiz Title,Nickname,Score\n\"Sample Quiz\",\"user1\",5\n"

        whenever(exportService.exportQuizResults(quizId)).thenReturn(csv)
        // Мокаем writer
        val writer = mock<java.io.PrintWriter>()
        whenever(response.writer).thenReturn(writer)

        controller.exportQuiz(quizId, response)

        verify(response).contentType = "text/csv;charset=UTF-8"
        verify(response).characterEncoding = "UTF-8"
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"quiz_quiz1_results.csv\"")
        verify(writer).write(csv)
    }

    @Test
    fun `exportRoot should redirect to export list`() {
        val result = controller.exportRoot()
        assertEquals("redirect:/export/list", result)
    }
}
