package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.time.LocalDate
import java.time.LocalDateTime

data class UserTokenDto(
    var id: String,
    var created: LocalDate,
    var modified: LocalDate,

    @JsonProperty("last_login")
    var lastLogin: LocalDateTime,
    var token: String,

    @JsonUnwrapped
    var user: UserDto
)