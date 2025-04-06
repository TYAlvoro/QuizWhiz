package com.quizwhiz.exportservice.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String
) {
    fun getUsernameFromJWT(token: String): String? {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        return claims["username"] as? String
    }
}
