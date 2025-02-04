package com.quizwhiz.authservice.service

import com.quizwhiz.authservice.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userEntity = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        // Если в вашей базе поле role хранит, например, "USER", можно передать его как authority
        return User.withUsername(userEntity.username)
            .password(userEntity.passwordHash)
            .roles(userEntity.role)  // Учтите, что Spring Security автоматически добавляет префикс "ROLE_"
            .build()
    }
}
