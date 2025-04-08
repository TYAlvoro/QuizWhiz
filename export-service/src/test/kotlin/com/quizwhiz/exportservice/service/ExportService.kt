package com.quizwhiz.exportservice.service

import com.quizwhiz.exportservice.document.QuizAttemptDocument
import com.quizwhiz.exportservice.document.QuizDocument
import com.quizwhiz.exportservice.repository.QuizAttemptRepository
import com.quizwhiz.exportservice.repository.QuizRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.*
import java.util.*

class ExportServiceTest {

    private lateinit var quizRepository: QuizRepository
    private lateinit var quizAttemptRepository: QuizAttemptRepository
    private lateinit var exportService: ExportService

    @BeforeEach
    fun setUp() {
        quizRepository = mock()
        quizAttemptRepository = mock()
        exportService = ExportService(quizRepository, quizAttemptRepository)
    }

    @Test
    fun `exportQuizResults should throw if quiz not found`() {
        whenever(quizRepository.findById("123")).thenReturn(Optional.empty())

        assertThrows(RuntimeException::class.java) {
            exportService.exportQuizResults("123")
        }
        verify(quizAttemptRepository, never()).findAllByQuizId(any())
    }

    @Test
    fun `exportQuizResults should return CSV content`() {
        val quiz = QuizDocument(
            id = "quiz1",
            courseId = "course",
            title = "Sample Quiz",
            description = "desc",
            answerType = "multiple",
            isOpen = true,
            creatorUsername = "teacher"
        )
        whenever(quizRepository.findById("quiz1")).thenReturn(Optional.of(quiz))

        val attempts = listOf(
            QuizAttemptDocument(
                id = "att1",
                quizId = "quiz1",
                nickname = "user1",
                score = 5,
                totalQuestions = 10
            ),
            QuizAttemptDocument(
                id = "att2",
                quizId = "quiz1",
                nickname = "user2",
                score = 8,
                totalQuestions = 10
            )
        )
        whenever(quizAttemptRepository.findAllByQuizId("quiz1")).thenReturn(attempts)

        val csv = exportService.exportQuizResults("quiz1")
        // Проверяем структуру CSV
        assertTrue(csv.contains("Quiz Title,Nickname,Score\n"))
        assertTrue(csv.contains("\"Sample Quiz\",\"user1\",5\n"))
        assertTrue(csv.contains("\"Sample Quiz\",\"user2\",8\n"))
    }

    @Test
    fun `getExportableQuizzes should return quizzes from repository`() {
        val username = "teacher1"
        val quizList = listOf(
            QuizDocument(id = "q1", courseId = "course1", title = "Quiz1", description = "", answerType = "multiple", isOpen = true, creatorUsername = username),
            QuizDocument(id = "q2", courseId = "course2", title = "Quiz2", description = "", answerType = "multiple", isOpen = false, creatorUsername = username)
        )
        whenever(quizRepository.findAllByCreatorUsername(username)).thenReturn(quizList)

        val result = exportService.getExportableQuizzes(username)
        assertEquals(2, result.size)
        assertEquals("q1", result[0].id)
        verify(quizRepository).findAllByCreatorUsername(username)
    }
}
