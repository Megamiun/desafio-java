package br.com.gabryel.logineer.dto

import br.com.gabryel.logineer.entities.User
import java.time.LocalDateTime

data class UserDto @JvmOverloads constructor(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phones: List<PhoneDto> = listOf()
) {
    fun toUser(): User {
        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        return User(
            name = name, password = password, email = email,
            created = today, modified = today, lastLogin = now,
            phones = phones.map { Dto -> Dto.toPhone() }
        )
    }
}