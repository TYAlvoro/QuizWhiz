package com.quizwhiz.userprofileservice.dto

import java.util.Date

data class AchievementDto(
    val achievementName: String,
    val description: String,
    val awardedAt: Date
)
