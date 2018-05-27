package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto @JvmOverloads constructor(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phones: List<PhoneDto> = listOf()
)