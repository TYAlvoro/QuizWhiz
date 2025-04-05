package com.quizwhiz.userprofileservice.security

import com.quizwhiz.userprofileservice.config.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)
        if (token != null) {
            try {
                val username = jwtTokenProvider.getUsernameFromJWT(token)
                if (username != null && SecurityContextHolder.getContext().authentication == null) {
                    val auth = UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        listOf(SimpleGrantedAuthority("ROLE_USER"))
                    )
                    SecurityContextHolder.getContext().authentication = auth
                    logger.debug("Authenticated user: $username")
                }
            } catch (ex: Exception) {
                logger.error("Error validating token", ex)
                SecurityContextHolder.clearContext()
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        // Сначала пробуем получить токен из заголовка Authorization
        val bearerToken = request.getHeader("Authorization")
        if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        // Если заголовка нет, ищем токен в cookies
        request.cookies?.forEach { cookie ->
            if (cookie.name == "AUTH_TOKEN") {
                return cookie.value
            }
        }
        return null
    }
}
