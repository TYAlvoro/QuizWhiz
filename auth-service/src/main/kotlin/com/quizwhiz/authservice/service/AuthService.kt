package com.quizwhiz.authservice.service

import com.quizwhiz.authservice.config.JwtTokenProvider
import com.quizwhiz.authservice.dto.LoginRequest
import com.quizwhiz.authservice.dto.RegistrationRequest
import com.quizwhiz.authservice.model.User
import com.quizwhiz.authservice.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Transactional
    fun register(request: RegistrationRequest) {
        println("Регистрация пользователя: $request")
        if (userRepository.existsByUsername(request.username)) {
            throw RuntimeException("Username already exists")
        }
        val user = User(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password),
            role = request.role,
            email = request.email
        )
        println("Сохраняем пользователя: $user")
        userRepository.save(user)
    }

    fun login(request: LoginRequest): String {
        val user = userRepository.findByUsername(request.username)
            ?: throw RuntimeException("User not found")
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw RuntimeException("Invalid credentials")
        }
        return jwtTokenProvider.generateToken(user)
    }
}
