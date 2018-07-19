package br.com.gabryel.logineer.repository

import br.com.gabryel.logineer.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun getByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean
}