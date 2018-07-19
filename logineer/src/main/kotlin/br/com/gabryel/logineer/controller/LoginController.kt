package br.com.gabryel.logineer.controller

import br.com.gabryel.logineer.dto.LoginDto
import br.com.gabryel.logineer.dto.UserDto
import br.com.gabryel.logineer.dto.UserTokenDto
import br.com.gabryel.logineer.exceptions.LogineerException
import br.com.gabryel.logineer.service.PhoneService
import br.com.gabryel.logineer.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("api")
class LoginController(private val phoneService: PhoneService, private val userService: UserService) {

    companion object {
        const val AUTH_HEADER = "Authorization"
    }

    @PutMapping("user")
    @Throws(LogineerException::class)
    fun createUser(@Valid @RequestBody userDto: UserDto): ResponseEntity<UserTokenDto> {
        val user = userService.register(userDto)

        return ResponseEntity.ok(UserTokenDto.of(user, userDto))
    }

    @PostMapping("login")
    @Throws(LogineerException::class)
    fun login(@Valid @RequestBody loginDto: LoginDto): ResponseEntity<UserTokenDto> {
        val user = userService.login(loginDto.email!!, loginDto.password!!)
        val phones = phoneService.getPhones(user)

        return ResponseEntity.ok(UserTokenDto.of(user, phones))
    }

    @GetMapping("user/{id}")
    @Throws(LogineerException::class)
    fun getUser(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<UserTokenDto> {
        val user = userService.getUser(id, request.getHeader(AUTH_HEADER))
        val phones = phoneService.getPhones(user)

        return ResponseEntity.ok(UserTokenDto.of(user, phones))
    }
}