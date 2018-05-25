package br.com.gabryel.logineer.controller.vo

data class UserVO @JvmOverloads constructor(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phones: List<PhoneVO> = listOf()
)