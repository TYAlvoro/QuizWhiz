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
class MyCoursesController(
    private val profileService: ProfileService,
    private val quizServiceClient: QuizServiceClient
) {
    @GetMapping("/profile/{username}/courses")
    fun showCourses(
        @PathVariable username: String,
        model: Model
    ): String {
        // 1) Проверяем аутентификацию
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.name != username) {
            return "redirect:/login"
        }

        // 2) Получаем профиль
        val profile: ProfileDto = profileService.getProfile(username)

        // 3) Вызываем Feign-клиент quizService, чтобы получить курсы по teacherId
        val courses = quizServiceClient.getCoursesByTeacherId(profile.id)

        // 4) Кладём в model
        model.addAttribute("profile", profile)
        model.addAttribute("courses", courses)

        // 5) Возвращаем шаблон mycourses.html
        return "mycourses"
    }
}