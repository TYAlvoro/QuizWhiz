package com.quizwhiz.quizservice.repository

import com.quizwhiz.quizservice.document.AchievementRecord
import org.springframework.data.mongodb.repository.MongoRepository

interface AchievementRepository : MongoRepository<AchievementRecord, String> {
    fun findByNickname(nickname: String): List<AchievementRecord>
}
