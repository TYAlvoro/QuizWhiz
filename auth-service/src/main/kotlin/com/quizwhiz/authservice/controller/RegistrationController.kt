package com.quizwhiz.authservice.controller

import com.quizwhiz.authservice.dto.RegistrationRequest
import com.quizwhiz.authservice.service.AuthService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class RegistrationController(private val authService: AuthService) {

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        model.addAttribute("registrationRequest", RegistrationRequest("", "", "USER", ""))
        return "register"
    }

    @PostMapping("/register")
    fun processRegistrationForm(
        @Valid @ModelAttribute registrationRequest: RegistrationRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) return "register"
        return try {
            authService.register(registrationRequest)
            // Редирект на профиль; токен теперь будет установлен в cookie после логина
            "redirect:/login"
        } catch (e: Exception) {
            model.addAttribute("error", e.message)
            "register"
        }
    }
}
