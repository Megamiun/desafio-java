package br.com.gabryel.logineer.entities

import java.time.LocalDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class User(
    @field:Id val id: String = UUID.randomUUID().toString(),
    val created: LocalDate? = null,
    val modified: LocalDate? = null,
    val lastLogin: LocalDate? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    @field:OneToMany private val phones: List<Phone> = listOf())