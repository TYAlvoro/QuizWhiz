package controller

import dto.ProfileDto
import service.ProfileService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ProfileController(private val profileService: ProfileService) {

    // Отображение страницы профиля по имени пользователя
    @GetMapping("/profile/{username}")
    fun viewProfile(@PathVariable username: String, model: Model): String {
        val profile: ProfileDto = profileService.getProfile(username)
        model.addAttribute("profile", profile)
        return "profile" // Шаблон: src/main/resources/templates/profile.html
    }
}
