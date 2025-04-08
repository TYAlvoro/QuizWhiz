package com.quizwhiz.quizservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "achievements")
data class AchievementRecord(
    @Id val id: String? = null,
    val nickname: String,
    val achievementName: String,
    val description: String,
    val awardedAt: Date = Date()
)
