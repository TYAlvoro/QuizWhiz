package com.quizwhiz.quizservice.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String,
    @Value("\${jwt.expiration}")
    private val jwtExpirationInMs: Long
) {

    fun generateToken(userId: Long, username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        return Jwts.builder()
            .setSubject(userId.toString())  // subject содержит ID пользователя
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
    }

    // Новый метод для извлечения userId (teacherId) из токена
    fun getUserIdFromJWT(token: String): Long {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject.toLong()
    }

    // Если вам также нужен username:
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