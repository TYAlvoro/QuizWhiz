package com.quizwhiz.quizservice.service

import com.quizwhiz.quizservice.document.AchievementRecord
import com.quizwhiz.quizservice.repository.AchievementRepository
import com.quizwhiz.quizservice.repository.QuizAttemptRepository
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AchievementService(
    private val achievementRepository: AchievementRepository,
    private val quizAttemptRepository: QuizAttemptRepository
) {
    fun processAchievements(nickname: String, quizId: String, score: Int, totalQuestions: Int) {
        val allAttempts = quizAttemptRepository.findAll().filter { it.nickname == nickname }
        if (allAttempts.size == 1) {
            awardAchievement(nickname, "Первый опыт", "Вы сделали свою первую попытку квиза!")
        }
        if (score == totalQuestions) {
            if (achievementRepository.findByNickname(nickname) == null) {
                awardAchievement(nickname, "Идеальный результат", "Вы набрали 100% баллов на квизе!")
            }
        }
        val highScoreAttempts = allAttempts.filter {
            it.totalQuestions > 0 && (it.score.toDouble() / it.totalQuestions.toDouble()) >= 0.8
        }
        if (highScoreAttempts.size >= 10) {
            if (achievementRepository.findByNickname(nickname) == null) {
                awardAchievement(nickname, "Мастер квизов", "Вы успешно прошли 10 квизов с результатом 80% и более!")
            }
        }
    }

    private fun awardAchievement(nickname: String, name: String, description: String) {
        if (achievementRepository.findByNickname(nickname) == null) {
            val achievement = AchievementRecord(
                nickname = nickname,
                achievementName = name,
                description = description,
                awardedAt = Date()
            )
            achievementRepository.save(achievement)
        }
    }
}
