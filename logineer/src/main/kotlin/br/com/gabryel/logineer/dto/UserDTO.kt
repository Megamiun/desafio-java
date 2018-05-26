package br.com.gabryel.logineer.dto

import br.com.gabryel.logineer.entities.User
import java.time.LocalDate

data class UserDTO @JvmOverloads constructor(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phones: List<PhoneDTO> = listOf()
) {
    fun toUser(): User {
        val now = LocalDate.now()
        return User(
            name = name, password = password, email = email,
            created = now, lastLogin = now, modified = now,
            phones = phones.map { dto -> dto.toPhone() }
        )
    }
}