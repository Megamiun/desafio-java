package br.com.gabryel.logineer.service

import br.com.gabryel.logineer.dto.PhoneDto
import br.com.gabryel.logineer.entities.Phone
import br.com.gabryel.logineer.entities.User

interface PhoneService {
    fun register(user: User, phoneDto: PhoneDto): Phone

    fun getPhones(user: User): List<Phone>
}