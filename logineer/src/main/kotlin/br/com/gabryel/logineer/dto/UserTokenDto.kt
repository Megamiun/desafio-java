package br.com.gabryel.logineer.dto

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
    constructor(
        id: String? = null,
        created: LocalDate? = null,
        modified: LocalDate? = null,
        lastLogin: LocalDateTime? = null,
        token: String? = null,
        userDto: UserDto
    ) : this(id, created, modified, lastLogin, token) {
        user = userDto
    }

    @field:JsonUnwrapped
    var user: UserDto? = null
}