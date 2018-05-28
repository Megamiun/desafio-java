package br.com.gabryel.logineer.entities

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(
    @Id val id: String = UUID.randomUUID().toString(),
    var created: LocalDate? = null,
    var modified: LocalDate? = null,
    var lastLogin: LocalDateTime? = null,
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,

    @Column(length = 512)
    var token: String? = null
)