package br.com.gabryel.logineer.entities

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Phone(
    @field:Id val id: String = UUID.randomUUID().toString(),
    val ddd: String? = null,
    val number: String? = null
)