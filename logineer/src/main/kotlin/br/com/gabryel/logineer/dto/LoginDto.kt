package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginDto @JvmOverloads constructor(var email: String? = null, var password: String? = null)