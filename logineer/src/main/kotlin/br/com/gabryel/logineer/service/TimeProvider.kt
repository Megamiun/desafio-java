package br.com.gabryel.logineer.service

import java.time.LocalDateTime

interface TimeProvider {
    fun now(): LocalDateTime
}
