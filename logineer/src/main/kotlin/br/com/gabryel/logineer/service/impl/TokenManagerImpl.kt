package br.com.gabryel.logineer.service.impl

import br.com.gabryel.logineer.service.TokenManager
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.stereotype.Service

import java.io.UnsupportedEncodingException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

@Service
class TokenManagerImpl : TokenManager {
    companion object {
        const val SECRET = "MySecret"
    }

    private val algorithm: Algorithm
        get() {
            try {
                return Algorithm.HMAC256(SECRET)
            } catch (e: UnsupportedEncodingException) {
                throw IllegalArgumentException(e)
            }
        }

    override fun createToken(uuid: String, email: String, lastLogin: LocalDateTime): String {
        val lastLoginDate = toDate(lastLogin)

        return JWT.create()
                .withIssuedAt(lastLoginDate)
                .withExpiresAt(toDate(lastLogin.plusMinutes(30)))
                .withClaim("uuid", uuid)
                .withIssuer("logineer")
                .withSubject(email)
                .sign(algorithm)
    }

    private fun toDate(dateTime: LocalDateTime): Date {
        return Date.from(dateTime.toInstant(ZoneOffset.ofHours(0)))
    }
}
