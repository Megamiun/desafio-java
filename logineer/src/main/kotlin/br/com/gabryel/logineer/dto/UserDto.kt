package br.com.gabryel.logineer.dto

data class UserDto @JvmOverloads constructor(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phones: List<PhoneDto> = listOf()
)