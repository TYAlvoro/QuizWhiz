package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.document.QuizAttemptDocument
import com.quizwhiz.quizservice.document.QuizDocument
import com.quizwhiz.quizservice.repository.QuizAttemptRepository
import com.quizwhiz.quizservice.repository.QuizRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.Date
import java.util.Optional

class QuizAttemptInternalControllerTest {

    private val quizAttemptRepository = mock<QuizAttemptRepository>()
    private val quizRepository = mock<QuizRepository>()
    private val controller = QuizAttemptInternalController(quizAttemptRepository, quizRepository)

    @Test
    fun `getRecentQuizAttempts should return up to 5 attempts`() {
        val nickname = "testUser"
        val attempts = (1..10).map { i ->
            QuizAttemptDocument(
                id = "att$i",
                quizId = "quiz$i",  // уникальное значение для каждой попытки
                nickname = nickname,
                answers = emptyList(),
                score = i,
                totalQuestions = 10,
                attemptedAt = Date(System.currentTimeMillis() + i * 1000)
            )
        }
        whenever(quizAttemptRepository.findAll()).thenReturn(attempts)
        whenever(quizRepository.findById(any())).thenReturn(
            Optional.of(QuizDocument(id="quiz0", courseId="c", title="Mocked Quiz", description="", answerType="multiple", isOpen=true))
        )

        val result = controller.getRecentQuizAttempts(nickname)
        assertEquals(5, result.size)
        // Первый элемент имеет quizId "quiz10", проверим (при замене "quiz" на "att")
        assertEquals("att10", result[0].quizId.replace("quiz", "att"))
    }

}
