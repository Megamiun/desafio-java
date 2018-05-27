package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PhoneDto @JvmOverloads constructor(var ddd: String? = null, var number: String? = null)