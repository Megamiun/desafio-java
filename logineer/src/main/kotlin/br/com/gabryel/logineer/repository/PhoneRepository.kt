package br.com.gabryel.logineer.repository

import br.com.gabryel.logineer.entities.Phone
import br.com.gabryel.logineer.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhoneRepository : JpaRepository<Phone, String> {
    fun findByUser(user: User): List<Phone>
}