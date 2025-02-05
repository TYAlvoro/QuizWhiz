package com.quizwhiz.authservice.controller

import com.quizwhiz.authservice.dto.LoginRequest
import com.quizwhiz.authservice.dto.LoginResponse
import com.quizwhiz.authservice.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val token = authService.login(request)
        return ResponseEntity.ok(LoginResponse(token))
    }
}
