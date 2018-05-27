package br.com.gabryel.logineer.dto

import br.com.gabryel.logineer.entities.Phone
import br.com.gabryel.logineer.entities.User
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.time.LocalDate
import java.time.LocalDateTime

data class UserTokenDto @JvmOverloads constructor(
    var id: String? = null,
    var created: LocalDate? = null,
    var modified: LocalDate? = null,

    @JsonProperty("last_login")
    var lastLogin: LocalDateTime? = null,
    var token: String? = null
) {
    companion object {
        @JvmStatic
        fun of(user: User, phones: List<Phone>): UserTokenDto {
            val tokenDto = UserTokenDto(user.id, user.created, user.modified, user.lastLogin, user.token)
            val phoneDtos = phones.map { phone -> PhoneDto(ddd = phone.ddd, number = phone.number) }
            tokenDto.user = UserDto(name = user.name, email = user.email, phones = phoneDtos)
            return tokenDto
        }

        @JvmStatic
        fun of(user: User, userDto: UserDto): UserTokenDto {
            val tokenDto = UserTokenDto(user.id, user.created, user.modified, user.lastLogin, user.token)
            tokenDto.user = userDto.copy(password = null)
            return tokenDto
        }
    }

    @field:JsonUnwrapped
    var user: UserDto? = null
}