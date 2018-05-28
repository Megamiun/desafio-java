package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginDto @JvmOverloads constructor(
    @field:NotBlank var email: String? = null,
    @field:NotBlank var password: String? = null
)