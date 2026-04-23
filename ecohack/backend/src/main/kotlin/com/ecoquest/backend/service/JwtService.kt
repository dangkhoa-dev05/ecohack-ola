package com.ecoquest.backend.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Minimal HMAC-SHA256 signed token service (JWT-compatible header.payload.signature format).
 *
 * Kept dependency-free so the project does not need to pull in a JWT library.
 * The token layout is:
 *   base64url(header).base64url(payload).base64url(HMAC-SHA256(header.payload))
 * Payload is `{ "sub": userId, "iat": epochSecond, "exp": epochSecond + ttl }`.
 */
@Service
class JwtService(
    @Value("\${security.jwt.secret:ecoquest-dev-secret-change-me-please-32-bytes-minimum}")
    private val secret: String,
    @Value("\${security.jwt.ttl-seconds:86400}")
    private val ttlSeconds: Long,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(JwtService::class.java)

    private val mac: Mac by lazy {
        Mac.getInstance("HmacSHA256").apply {
            init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        }
    }

    private val encoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder: Base64.Decoder = Base64.getUrlDecoder()

    fun generateToken(userId: String): String {
        val now = Instant.now().epochSecond
        val header = mapOf("alg" to "HS256", "typ" to "JWT")
        val payload = mapOf(
            "sub" to userId,
            "iat" to now,
            "exp" to (now + ttlSeconds)
        )

        val headerSeg = encode(objectMapper.writeValueAsBytes(header))
        val payloadSeg = encode(objectMapper.writeValueAsBytes(payload))
        val signature = sign("$headerSeg.$payloadSeg")

        return "$headerSeg.$payloadSeg.$signature"
    }

    /**
     * Validates [token] and returns the contained userId, or null if the token
     * is malformed / tampered with / expired.
     */
    fun parseUserId(token: String): String? {
        val parts = token.split(".")
        if (parts.size != 3) return null

        val (headerSeg, payloadSeg, signature) = parts
        val expected = sign("$headerSeg.$payloadSeg")
        if (!constantTimeEquals(expected, signature)) {
            log.debug("JWT signature mismatch")
            return null
        }

        return try {
            val payload = objectMapper.readTree(decoder.decode(payloadSeg))
            val exp = payload.path("exp").asLong(0)
            if (exp > 0 && exp < Instant.now().epochSecond) {
                log.debug("JWT expired at {}", exp)
                return null
            }
            payload.path("sub").asText(null)?.takeIf { it.isNotBlank() }
        } catch (ex: Exception) {
            log.debug("JWT payload parse failed: {}", ex.message)
            null
        }
    }

    private fun encode(bytes: ByteArray): String = encoder.encodeToString(bytes)

    @Synchronized
    private fun sign(data: String): String {
        val bytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return encoder.encodeToString(bytes)
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var diff = 0
        for (i in a.indices) diff = diff or (a[i].code xor b[i].code)
        return diff == 0
    }
}
