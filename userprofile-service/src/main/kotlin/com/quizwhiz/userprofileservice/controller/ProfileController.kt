package com.quizwhiz.userprofileservice.controller

import com.quizwhiz.userprofileservice.dto.ProfileDto
import com.quizwhiz.userprofileservice.service.ProfileService
import com.quizwhiz.userprofileservice.client.QuizServiceClient
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ProfileController(
    private val profileService: ProfileService,
    private val quizServiceClient: QuizServiceClient
) {
    private val logger = LoggerFactory.getLogger(ProfileController::class.java)

    @GetMapping("/profile/{username}")
    fun viewProfile(@PathVariable username: String, model: Model): String {
        // Если пользователь не аутентифицирован или пытается посмотреть чужой профиль, перенаправляем
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.name != username) return "redirect:/login"

        // Получаем профиль
        val profile: ProfileDto = profileService.getProfile(username)
        logger.info("Получен профиль для пользователя: ${profile.username}")

        // Получаем последние попытки квизов
        val recentQuizzes = try {
            quizServiceClient.getRecentQuizAttempts(username)
        } catch (ex: Exception) {
            logger.error("Ошибка при получении попыток квизов для $username", ex)
            emptyList()
        }

        // Получаем последние курсы
        val recentCourses = try {
            quizServiceClient.getCoursesByTeacherId(profile.id)
        } catch (ex: Exception) {
            logger.error("Ошибка при получении курсов для учителя с id ${profile.id}", ex)
            emptyList()
        }

        // Получаем ачивки
        val achievements = try {
            val ach = quizServiceClient.getAchievements(username)
            logger.info("Получено ${ach.size} ачивок для $username")
            ach
        } catch (ex: Exception) {
            logger.error("Ошибка при получении ачивок для $username", ex)
            emptyList()
        }

        // Добавляем все в модель
        model.addAttribute("profile", profile)
        model.addAttribute("recentQuizzes", recentQuizzes)
        model.addAttribute("recentCourses", recentCourses)
        model.addAttribute("achievements", achievements)

        return "profile"
    }
}
