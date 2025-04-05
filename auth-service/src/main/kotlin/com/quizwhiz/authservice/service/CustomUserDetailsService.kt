package com.quizwhiz.authservice.service

import com.quizwhiz.authservice.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String) =
        userRepository.findByUsername(username)?.let {
            User.withUsername(it.username)
                .password(it.passwordHash)
                .roles(it.role)
                .build()
        } ?: throw UsernameNotFoundException("User not found: $username")
}
