package com.quizwhiz.userprofileservice.controller

import com.quizwhiz.userprofileservice.dto.ProfileDto
import com.quizwhiz.userprofileservice.service.ProfileService
import com.quizwhiz.userprofileservice.client.QuizServiceClient
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
    @GetMapping("/profile/{username}")
    fun viewProfile(@PathVariable username: String, model: Model): String {
        // Если пользователь не аутентифицирован или пытается посмотреть чужой профиль, перенаправляем на страницу входа
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.name != username) return "redirect:/login"

        val profile: ProfileDto = profileService.getProfile(username)
        val recentQuizzes = try {
            quizServiceClient.getRecentQuizAttempts(username)
        } catch (ex: Exception) {
            emptyList()
        }
        val recentCourses = try {
            quizServiceClient.getCoursesByTeacherId(profile.id)
        } catch (ex: Exception) {
            emptyList()
        }

        model.addAttribute("profile", profile)
        model.addAttribute("recentQuizzes", recentQuizzes)
        model.addAttribute("recentCourses", recentCourses)
        return "profile"
    }
}
