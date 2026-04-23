package com.ecoquest.backend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret:ZWNvcXVlc3QtZGV2LXNlY3JldC1rZXktZm9yLWxvY2FsLXRlc3Rpbmctb25seQ==}")
    private val secret: String,
    @Value("\${jwt.expiration-ms:86400000}")
    private val expirationMs: Long
) {

    fun generateToken(userId: String): String {
        val now = Instant.now()

        return Jwts.builder()
            .subject(userId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(signingKey())
            .compact()
    }

    fun extractUserId(token: String): String = extractAllClaims(token).subject

    fun isTokenValid(token: String): Boolean {
        return try {
            !extractAllClaims(token).expiration.before(Date())
        } catch (_: Exception) {
            false
        }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun signingKey(): SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
}
