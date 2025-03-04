package com.quizwhiz.authservice.config

import com.quizwhiz.authservice.repository.UserRepository
import com.quizwhiz.authservice.config.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val username = authentication.name
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        // Генерируем JWT (например, со сроком действия 1 час)
        val token = jwtTokenProvider.generateToken(user)
        // Перенаправляем в Profile Service с токеном в параметре
        response.sendRedirect("http://127.0.0.1:8082/profile/$username?token=$token")
    }
}