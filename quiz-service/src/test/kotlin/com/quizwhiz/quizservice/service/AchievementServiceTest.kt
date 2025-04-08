package com.quizwhiz.quizservice.service

import com.quizwhiz.quizservice.document.QuizAttemptDocument
import com.quizwhiz.quizservice.repository.AchievementRepository
import com.quizwhiz.quizservice.repository.QuizAttemptRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.Date

class AchievementServiceTest {

    private lateinit var achievementRepository: AchievementRepository
    private lateinit var quizAttemptRepository: QuizAttemptRepository
    private lateinit var achievementService: AchievementService

    @BeforeEach
    fun setUp() {
        achievementRepository = mock()
        quizAttemptRepository = mock()
        achievementService = AchievementService(achievementRepository, quizAttemptRepository)
    }

    @Test
    fun `processAchievements should award 'Первый опыт' if first attempt`() {
        val nickname = "newUser"
        val quizId = "quiz1"
        // Симулируем единственную попытку (first attempt)
        val attempt = QuizAttemptDocument(
            id = "att1", quizId = quizId, nickname = nickname,
            answers = emptyList(), score = 3, totalQuestions = 10
        )
        whenever(quizAttemptRepository.findAll()).thenReturn(listOf(attempt))
        // Симулируем, что для данного пользователя достижения отсутствуют
        whenever(achievementRepository.findByNickname(nickname)).thenReturn(null)

        achievementService.processAchievements(nickname, quizId, score = 3, totalQuestions = 10)

        verify(achievementRepository).save(argThat { achievementName == "Первый опыт" })
    }

    @Test
    fun `processAchievements should award 'Идеальный результат' if score equals totalQuestions`() {
        val nickname = "perfectUser"
        val quizId = "quiz1"
        // Симулируем несколько попыток (чтобы не сработала ветка "Первый опыт")
        val attempts = listOf(
            QuizAttemptDocument(id = "att1", quizId = quizId, nickname = nickname, answers = emptyList(), score = 4, totalQuestions = 5),
            QuizAttemptDocument(id = "att2", quizId = quizId, nickname = nickname, answers = emptyList(), score = 5, totalQuestions = 5)
        )
        whenever(quizAttemptRepository.findAll()).thenReturn(attempts)
        // Симулируем отсутствие достижения (возвращаем null)
        whenever(achievementRepository.findByNickname(nickname)).thenReturn(null)

        achievementService.processAchievements(nickname, quizId, score = 5, totalQuestions = 5)

        verify(achievementRepository).save(argThat { achievementName == "Идеальный результат" })
    }

    @Test
    fun `processAchievements should award 'Мастер квизов' after 10 attempts with at least 80 percent`() {
        val nickname = "quizMaster"
        val quizId = "quiz1"
        // Симулируем 10 попыток, где каждый результат равен 80%
        val attempts = (1..10).map { i ->
            QuizAttemptDocument(
                id = "att$i", quizId = quizId, nickname = nickname,
                answers = emptyList(), score = 8, totalQuestions = 10,
                attemptedAt = Date(System.currentTimeMillis() + i * 1000)
            )
        }
        whenever(quizAttemptRepository.findAll()).thenReturn(attempts)
        whenever(achievementRepository.findByNickname(nickname)).thenReturn(null)

        achievementService.processAchievements(nickname, quizId, score = 8, totalQuestions = 10)

        verify(achievementRepository).save(argThat { achievementName == "Мастер квизов" })
    }
}