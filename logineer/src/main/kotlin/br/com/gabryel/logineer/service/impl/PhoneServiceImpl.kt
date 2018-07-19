package br.com.gabryel.logineer.service.impl

import br.com.gabryel.logineer.dto.PhoneDto
import br.com.gabryel.logineer.entities.Phone
import br.com.gabryel.logineer.entities.User
import br.com.gabryel.logineer.repository.PhoneRepository
import br.com.gabryel.logineer.service.PhoneService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PhoneServiceImpl(private val phoneRepository: PhoneRepository) : PhoneService {

    override fun register(user: User, phoneDto: PhoneDto): Phone {
        val phone = Phone(UUID.randomUUID().toString(), user, phoneDto.ddd, phoneDto.number)
        return phoneRepository.save(phone)
    }

    override fun getPhones(user: User) = phoneRepository.findByUser(user)
}
