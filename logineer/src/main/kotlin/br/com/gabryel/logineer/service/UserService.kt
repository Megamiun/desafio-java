package br.com.gabryel.logineer.service

import br.com.gabryel.logineer.dto.UserDto
import br.com.gabryel.logineer.entities.User
import br.com.gabryel.logineer.exceptions.LogineerException

interface UserService {

    @Throws(LogineerException::class)
    fun register(userDto: UserDto): User

    @Throws(LogineerException::class)
    fun login(email: String, password: String): User

    @Throws(LogineerException::class)
    fun getUser(id: String, token: String?): User

    fun convertToUser(userDto: UserDto): User
}