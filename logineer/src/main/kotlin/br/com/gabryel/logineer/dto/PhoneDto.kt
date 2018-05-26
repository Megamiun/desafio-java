package br.com.gabryel.logineer.dto

import br.com.gabryel.logineer.entities.Phone

data class PhoneDto(var ddd: String? = null, var number: String? = null) {
    fun toPhone(): Phone {
        return Phone(ddd = ddd, number = number)
    }
}