package com.quizwhiz.authservice.controller

import com.quizwhiz.authservice.dto.LoginRequest
import com.quizwhiz.authservice.dto.LoginResponse
import com.quizwhiz.authservice.dto.RegistrationRequest
import com.quizwhiz.authservice.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        // Создаем пустой объект для связывания с формой
        model.addAttribute("registrationRequest", RegistrationRequest("", "", "USER", ""))
        return "register" // имя шаблона register.html
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegistrationRequest): ResponseEntity<String> {
        authService.register(request)
        return ResponseEntity.ok("User registered successfully")
    }


    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val token = authService.login(request)
        return ResponseEntity.ok(LoginResponse(token))
    }
}
