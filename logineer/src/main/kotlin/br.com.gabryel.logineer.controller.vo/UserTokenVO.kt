package br.com.gabryel.logineer.controller.vo

import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.time.LocalDate

data class UserTokenVO(
    var id: String,
    var created: LocalDate,
    var modified: LocalDate,
    var lastLogin: LocalDate,
    var token: String,

    @field:JsonUnwrapped
    var user: UserVO
)