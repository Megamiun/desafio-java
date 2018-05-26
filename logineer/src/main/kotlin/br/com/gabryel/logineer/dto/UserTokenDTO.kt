package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.time.LocalDate

data class UserTokenDTO(
    var id: String,
    var created: LocalDate,
    var modified: LocalDate,

    @field:JsonAlias("last_login")
    var lastLogin: LocalDate,
    var token: String,

    @field:JsonUnwrapped
    var user: UserDTO
)