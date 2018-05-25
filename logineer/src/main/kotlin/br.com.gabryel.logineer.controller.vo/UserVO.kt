package br.com.gabryel.logineer.controller.vo

data class UserVO(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phones: List<PhoneVO> = listOf()
)