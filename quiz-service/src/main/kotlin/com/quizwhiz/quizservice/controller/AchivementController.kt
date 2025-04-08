package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.dto.AchievementDto
import com.quizwhiz.quizservice.repository.AchievementRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/achievements")
class AchievementController(
    private val achievementRepository: AchievementRepository
) {

    @GetMapping("/{nickname}")
    fun getAchievements(@PathVariable nickname: String): List<AchievementDto> {
        val records = achievementRepository.findByNickname(nickname)
        return records.map { record ->
            AchievementDto(
                achievementName = record.achievementName,
                description = record.description,
                awardedAt = record.awardedAt
            )
        }
    }
}
