package br.com.gabryel.logineer.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDto(val mensagem: String)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MultiErrorDto(val memsagem: List<String>)