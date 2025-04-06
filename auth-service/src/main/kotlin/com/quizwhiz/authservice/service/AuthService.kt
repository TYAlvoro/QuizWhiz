package com.quizwhiz.authservice.service

import com.quizwhiz.authservice.dto.RegistrationRequest
import com.quizwhiz.authservice.model.User
import com.quizwhiz.authservice.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun register(request: RegistrationRequest) {
        if (userRepository.existsByUsername(request.username)) {
            throw RuntimeException("Username already exists")
        }
        val user = User(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password),
            role = request.role,
            email = request.email
        )
        userRepository.save(user)
    }
}