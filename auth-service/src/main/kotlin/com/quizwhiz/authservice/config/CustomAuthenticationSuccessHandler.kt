package com.quizwhiz.authservice.config

import com.quizwhiz.authservice.repository.UserRepository
import jakarta.servlet.http.Cookie
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
            ?: throw UsernameNotFoundException("User not found: $username")
        val token = jwtTokenProvider.generateToken(user)

        // Устанавливаем JWT в HTTP-only cookie через метод setHttpOnly()
        val cookie = Cookie("AUTH_TOKEN", token).apply {
            setHttpOnly(true)
            secure = request.isSecure
            path = "/"
            maxAge = (jwtTokenProvider.jwtExpirationInMs / 1000).toInt()
        }
        response.addCookie(cookie)

        // Перенаправляем на профиль без передачи токена через URL
        response.sendRedirect("http://127.0.0.1:8082/profile/$username")
    }
}
