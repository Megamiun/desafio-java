package br.com.gabryel.logineer.service.impl

import br.com.gabryel.logineer.service.TimeProvider
import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
class TimeProviderImpl : TimeProvider {
    override fun now() = LocalDateTime.now()!!
}
