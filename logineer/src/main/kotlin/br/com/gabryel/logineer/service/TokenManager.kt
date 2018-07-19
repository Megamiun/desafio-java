package br.com.gabryel.logineer.service

import java.time.LocalDateTime

interface TokenManager {
    fun createToken(uuid: String, email: String, lastLogin: LocalDateTime): String
}
