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

    // Отображает HTML‑форму регистрации
    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        println("GET /register вызван")
        model.addAttribute("registrationRequest", RegistrationRequest("", "", "USER", ""))
        return "register"  // шаблон в src/main/resources/templates/register.html
    }

    // Обрабатывает POST‑запрос с данными формы регистрации
    @PostMapping("/register")
    fun processRegistrationForm(
        @Valid @ModelAttribute registrationRequest: RegistrationRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        println("POST /register вызван с данными: $registrationRequest")
        if (bindingResult.hasErrors()) {
            println("Ошибки валидации: ${bindingResult.allErrors}")
            return "register"
        }
        try {
            authService.register(registrationRequest)
            println("Пользователь зарегистрирован успешно")
        } catch (e: Exception) {
            println("Ошибка регистрации: ${e.message}")
            model.addAttribute("error", e.message)
            return "register"
        }
        // После успешной регистрации перенаправляем на страницу логина
        return "redirect:/login"
    }
}
