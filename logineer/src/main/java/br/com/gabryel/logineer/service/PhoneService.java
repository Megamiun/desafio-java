package br.com.gabryel.logineer.service;

import br.com.gabryel.logineer.dto.PhoneDto;
import br.com.gabryel.logineer.entities.Phone;
import br.com.gabryel.logineer.entities.User;

import java.util.List;

public interface PhoneService {
    Phone register(User user, PhoneDto phoneDto);

    List<Phone> getPhones(User user);
}