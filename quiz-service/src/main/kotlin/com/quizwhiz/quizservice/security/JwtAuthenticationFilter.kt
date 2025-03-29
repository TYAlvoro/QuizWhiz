package com.quizwhiz.quizservice.security

import com.quizwhiz.quizservice.security.JwtTokenProvider
import jakarta.servlet.FilterChain
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
        val token = request.getParameter("token")
        if (!token.isNullOrEmpty()) {
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
                } else {
                    logger.debug("Username is null or authentication already exists")
                }
            } catch (ex: Exception) {
                logger.error("Error validating token", ex)
                SecurityContextHolder.clearContext()
            }
        }
        filterChain.doFilter(request, response)
    }
}