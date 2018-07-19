package br.com.gabryel.logineer.service.impl

import br.com.gabryel.logineer.dto.UserDto
import br.com.gabryel.logineer.entities.User
import br.com.gabryel.logineer.exceptions.LogineerException
import br.com.gabryel.logineer.repository.UserRepository
import br.com.gabryel.logineer.service.PhoneService
import br.com.gabryel.logineer.service.TimeProvider
import br.com.gabryel.logineer.service.TokenManager
import br.com.gabryel.logineer.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID

@Service
class UserServiceImpl(private val userRepository: UserRepository, private val phoneService: PhoneService,
                      private val timeProvider: TimeProvider, private val encoder: PasswordEncoder,
                      private val tokenManager: TokenManager)
    : UserService {

    @Throws(LogineerException::class)
    override fun register(userDto: UserDto): User {
        val email = userDto.email?: throw unacceptableException("Nenhum e-mail foi dado.")

        if (userRepository.existsByEmail(email)) {
            throw unacceptableException("E-mail já existente")
        }

        with(userRepository.save(convertToUser(userDto))) {
            userDto.phones.forEach { phoneDto -> phoneService.register(this, phoneDto) }
            return this
        }
    }

    @Throws(LogineerException::class)
    override fun login(email: String, password: String): User {
        val user = userRepository.getByEmail(email)
        if (user == null || !encoder.matches(password, user.password)) {
            throw authenticationException("Usuário e/ou senha inválidos")
        }

        val lastLogin = timeProvider.now()

        user.lastLogin = lastLogin
        user.token = tokenManager.createToken(user.id, email, lastLogin)
        userRepository.save(user)
        return user
    }

    @Throws(LogineerException::class)
    override fun getUser(id: String, token: String?): User {
        val user = userRepository.findById(id)
                .orElseThrow { authenticationException("Não autorizado") }

        if (isTokenInvalid(token, user)) throw authenticationException("Não autorizado")
        if (isSessionExpired(user)) throw authenticationException("Sessão inválida")

        return user
    }

    private fun isTokenInvalid(token: String?, user: User) = token != "Bearer ${user.token}"

    /**
     * Says if the session from the user is expired, based on the last login time
     *
     * @param user User to validate
     * @return If the session can still be used
     */
    private fun isSessionExpired(user: User): Boolean {
        val minimumLastLoginTime = timeProvider.now().minusMinutes(30)
        val lastLogin = Objects.requireNonNull<LocalDateTime>(user.lastLogin)
        return lastLogin.isBefore(minimumLastLoginTime)
    }

    override fun convertToUser(userDto: UserDto): User {
        val now = timeProvider.now()
        val today = now.toLocalDate()

        val uuid = UUID.randomUUID().toString()
        val password = encoder.encode(userDto.password)
        val token = tokenManager.createToken(uuid, userDto.email!!, now)

        return User(uuid, today, today, now, userDto.name,
                userDto.email, password, token)
    }

    private fun unacceptableException(message: String) =
            LogineerException(LogineerException.ErrorType.UNACCEPTABLE, message)

    private fun authenticationException(message: String) =
            LogineerException(LogineerException.ErrorType.AUTHENTICATION, message)
}
