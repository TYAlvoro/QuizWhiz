package com.quizwhiz.quizservice.config

import com.quizwhiz.quizservice.security.JwtTokenProvider
import com.quizwhiz.quizservice.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class QuizServiceSecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                // Разрешаем доступ к внутренним endpoint'ам и публичным URL без аутентификации
                auth.requestMatchers("/internal/**", "/public/**").permitAll()
                    .anyRequest().authenticated()
            }
            // Наш фильтр для JWT проверяется для остальных URL, но внутренние маршруты уже разрешены
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }
}
