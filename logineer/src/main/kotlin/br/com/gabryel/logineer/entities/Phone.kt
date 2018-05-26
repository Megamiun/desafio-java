package br.com.gabryel.logineer.entities

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class Phone(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,

    val ddd: String? = null,
    val number: String? = null
)