package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.dto.AchievementDto
import com.quizwhiz.quizservice.document.AchievementRecord
import com.quizwhiz.quizservice.repository.AchievementRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Date

class AchievementControllerTest {
    private val achievementRepository = mock<AchievementRepository>()
    private val controller = AchievementController(achievementRepository)

    @Test
    fun `getAchievements should return list of AchievementDto`() {
        val nickname = "testUser"
        val record1 = AchievementRecord(
            id = "1",
            nickname = nickname,
            achievementName = "First Blood",
            description = "First attempt done!",
            awardedAt = Date()
        )
        val record2 = AchievementRecord(
            id = "2",
            nickname = nickname,
            achievementName = "Champion",
            description = "Win 10 times",
            awardedAt = Date()
        )
        whenever(achievementRepository.findByNickname(nickname)).thenReturn(listOf(record1, record2))

        val result = controller.getAchievements(nickname)
        assertEquals(2, result.size)
        assertTrue(result.any { it.achievementName == "First Blood" })
        assertTrue(result.any { it.achievementName == "Champion" })
    }
}