package br.com.gabryel.logineer.service.impl;

import br.com.gabryel.logineer.dto.PhoneDto;
import br.com.gabryel.logineer.entities.Phone;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.repository.PhoneRepository;
import br.com.gabryel.logineer.service.PhoneService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PhoneServiceImpl implements PhoneService {

    private final PhoneRepository phoneRepository;

    public PhoneServiceImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public Phone register(User user, PhoneDto phoneDto) {
        Phone phone = new Phone(UUID.randomUUID().toString(), user, phoneDto.getDdd(), phoneDto.getNumber());
        return phoneRepository.save(phone);
    }

    @Override
    public List<Phone> getPhones(User user) {
        return phoneRepository.findByUser(user);
    }
}
