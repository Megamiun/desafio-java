package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotBlank

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto @JvmOverloads constructor(
    @field:NotBlank var name: String? = null,
    @field:NotBlank var email: String? = null,
    @field:NotBlank var password: String? = null,
    var phones: List<PhoneDto> = listOf()
)