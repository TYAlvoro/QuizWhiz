package com.quizwhiz.userprofileservice.controller

import com.quizwhiz.userprofileservice.dto.ProfileDto
import com.quizwhiz.userprofileservice.service.ProfileService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ProfileController(private val profileService: ProfileService) {
    @GetMapping("/profile/{username}")
    fun viewProfile(@PathVariable username: String, model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication
        // Если пользователь не аутентифицирован или имена не совпадают — перенаправляем
        if (auth == null || auth.name != username) {
            // Можно либо перенаправить на страницу логина, либо на профиль текущего пользователя
            return "redirect:/login"
        }
        val profile: ProfileDto = profileService.getProfile(username)
        model.addAttribute("profile", profile)
        return "profile" // Отображение шаблона profile.html
    }
}